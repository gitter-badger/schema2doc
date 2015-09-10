package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbProcedure;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.ScannerException;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import com.zaxxer.hikari.HikariDataSource;


/**
 * get meta data by jdbc onboard methods.
 * 
 * @see <a href=
 *      "http://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html">
 *      DatabaseMetaData</a>
 */
public class GenericDbScanner implements IScanner {

	private static final Logger LOG = LoggerFactory.getLogger(GenericDbScanner.class);

	/** database connection. */
	private DataSource dataSource;
	
	/** use experimental parallel execution. */
	private boolean    useParallelStream = false;

	/** list of schema or null for all. */
	private String[] schemaToDocument = null;

	private List<IDbTable> cachedTables = null;
	
	private List<IDbProcedure> cachedProcedures = null;
	
	/** ctor. */
	public GenericDbScanner(@NotNull final DataSource dataSource) {
		this.dataSource = Require.notNull(dataSource, "dataSource");
	}
	
	private Connection getConnection() {
		try {
			return this.dataSource.getConnection();
		} catch (Exception ex) {
			LOG.error("error getting connection from pool: " + ex.getMessage(), ex);
			throw new ScannerException(ex.getMessage(), ex);
		}
	}
	
	private void returnPooledConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception ex) {
			LOG.error("error returning connection to pool: " + ex.getMessage(), ex);
			throw new ScannerException(ex.getMessage(), ex);
		}
	}
		
	
	@Override
	@NotNull
	public Stream<IDbTable> getTables() {
		
		if (this.cachedTables == null) {
			this.cachedTables = this.isUseParallelStream() ? getParallelTables() : getTablesInternal();
		} 
		return this.cachedTables.stream();
	}
	
	@Override
	@NotNull
	public Stream<IDbProcedure> getProcedures() {
		
		if (this.cachedProcedures == null) {
			this.cachedProcedures = getProceduresInternal();
		} 
		return this.cachedProcedures.stream();
	}	

	private List<IDbProcedure> getProceduresInternal() {
		LOG.debug("retrieve procedures");
		long procReadCount = 0;
		
		Connection connection = getConnection();
		try {
			List<IDbProcedure> procedures = new ArrayList<IDbProcedure>();
			
			ResultSet rsProcedures = this.dataSource.getConnection().getMetaData().getProcedures(null, null, null);
			while (rsProcedures.next()) {
				final String schema = rsProcedures.getString("PROCEDURE_SCHEM");
				final String name   = rsProcedures.getString("PROCEDURE_NAME");
				
				if (procReadCount == 0 || (procReadCount % 100) == 0) {
				    LOG.debug(" ... scanning procedure " 
				    			+ procReadCount + ", thereof relevant " 
				    			+ procedures.size() + " currently " 
				    			+ name);
				}
				procReadCount++;
				
				if (schemaToDocument == null
						|| Arrays.stream(schemaToDocument).anyMatch(s -> s.equalsIgnoreCase(schema))) {
					procedures.add(new DbProcedureDefaultData(
							rsProcedures.getString("PROCEDURE_CAT"),
							schema,
							name, 
							rsProcedures.getString("REMARKS")));
				}
			}
			rsProcedures.close();
			
			return procedures;
			
		} catch (SQLException sqlEx) {
			throw new ScannerException("error retrieving procedures " + sqlEx.getMessage(), sqlEx);			
		} finally {
			returnPooledConnection(connection);
		}
	}

	/**
	 * get table data retrieved with getMetaData().getTables().
	 * 
	 * @exception ScannerException
	 * @return list of tables in db.
	 */
	@NotNull
	private List<IDbTable> getTablesInternal() {
		LOG.debug("retrieve tables");
		
		Connection connection = getConnection();
		try {
			long tableReadCount = 0;
			List<IDbTable> tables = new ArrayList<IDbTable>();
			
			ResultSet rsTable = this.dataSource.getConnection().getMetaData().getTables(null, null, "%", null);
			while (rsTable.next()) {
				final String schema = rsTable.getString("TABLE_SCHEM");
				
				if (tableReadCount == 0 || (tableReadCount % 100) == 0) {
				    LOG.debug(" ... scanning table " 
				    			+ tableReadCount + ", thereof relevant " 
				    			+ tables.size() + " currently " 
				    			+ rsTable.getString("TABLE_NAME"));
				}
				tableReadCount++;
				
				if (schemaToDocument == null
						|| Arrays.stream(schemaToDocument).anyMatch(s -> s.equalsIgnoreCase(schema))) {
					tables.add(new DbTableDefaultData(rsTable.getString("TABLE_CAT"), schema,
							rsTable.getString("TABLE_NAME"), rsTable.getString("REMARKS")));
				}
			}
			rsTable.close();
			
			LOG.debug("scanning table columns for " + tables.size() + " tables.. ");

			long tableProcessedCount = 0;
			for (IDbTable table : tables) {
				
				if (tableProcessedCount == 0 || (tableProcessedCount % 100) == 0) {
				    LOG.debug(" ... scanning columns for " + tableProcessedCount + ". table " + table.fqnName());
				}
				tableProcessedCount++;				
				mapWithPrivilege(table);
			}
			
			LOG.debug("scanning keys..");

			tableProcessedCount = 0;
			for (IDbTable table : tables) {
				
				if (tableProcessedCount == 0 || (tableProcessedCount % 10) == 0) {
				    LOG.debug(" ... scanning foreign keys for " + tableProcessedCount + ". table " + table.fqnName());
				}
				tableProcessedCount++;				
				mapWithKey(table);
			}
			
			LOG.debug("scanning tables done.");

			return tables;
		} catch (Exception ex) {
			throw new ScannerException("error retrieving tables " + ex.getMessage(), ex);
		} finally {
			returnPooledConnection(connection);
		}
	}
	
	/* create table from result set. */
	private static IDbTable mapTable(@NotNull ResultSet row) {
		try {
			return new DbTableDefaultData(
					Require.notNull(row, "no row to map").getString("TABLE_CAT"), 
					row.getString("TABLE_SCHEM"),
					row.getString("TABLE_NAME"), 
					row.getString("REMARKS")
				);		
		} catch (SQLException sqlEx) {
			LOG.debug(sqlEx.getMessage(), sqlEx);
			throw new ScannerException(sqlEx.getMessage(), sqlEx);
		}
	}
	
	/** enrich table with grants. */
	private IDbTable mapWithPrivilege(@NotNull IDbTable table) {
		LOG.debug("map privileges for table " + table);
		Connection con = getConnection();
		try (ResultSet rsPrivilege = con.getMetaData()
				.getTablePrivileges(
						Require.notNull(table, "table").getCatalog(), 
						table.getSchema(), 
						table.getName())) {
			while (rsPrivilege.next()) {
				table.addPrivilege(new DbPrivilegeDefaultData(rsPrivilege.getString("GRANTEE"),
						rsPrivilege.getString("PRIVILEGE")));
			}
			rsPrivilege.close();
			return table;
		} catch	(Exception ex) {
			LOG.error("error mapWithKey: " + ex.getMessage(), ex);
			throw new ScannerException(ex.getMessage(), ex);
		} finally {
			returnPooledConnection(con);
		}
	}
	
	/* enrich table with foreign keys. */
	private IDbTable mapWithKey(@NotNull IDbTable table) {
		LOG.debug("map fk-keys for table " + table);
		Connection con = getConnection();
		try (ResultSet rsKey = con.getMetaData().getExportedKeys(
				Require.notNull(table, "table").getCatalog(), 
				table.getSchema(),
				table.getName())) {

			while (rsKey.next()) {
				table.addReferrer(new DbTableReferenceDefaultData(
						rsKey.getString("FKTABLE_CAT"),
						rsKey.getString("FKTABLE_SCHEM"), 
						rsKey.getString("FKTABLE_NAME")));
			}
			rsKey.close();
			return table;
		} catch	(Exception ex) {
			LOG.error("error mapWithKey: " + ex.getMessage(), ex);
			throw new ScannerException(ex.getMessage(), ex);
		} finally {
			returnPooledConnection(con);
		}
	}
	
	
	/**
	 * get table data retrieved with getMetaData().getTables().
	 * 
	 * try some parallel stream methods.
	 * 
	 * @exception ScannerException
	 */
	@NotNull
	public List<IDbTable> getParallelTables() {
		LOG.debug("retrieve tables parallel..");
		
		Connection con = getConnection();
		try {
			ResultSet rsTable = con.getMetaData().getTables(null, null, "%", null);

			// serialized reading of all tables..
			Stream<IDbTable> tables = StreamSupport
	                .stream(Spliterators.spliteratorUnknownSize(
                        	new ResultSetIterator<IDbTable>(
                        			rsTable, 
                        			GenericDbScanner::mapTable), 
                        	Spliterator.IMMUTABLE), false)
                .filter(tbl -> 
                		 schemaToDocument == null ||
                		 Arrays.stream(schemaToDocument).anyMatch(s -> s.equalsIgnoreCase(tbl.getSchema()))
                	   )
             //   .collect(Collectors.toList())	// force loading before return!
                ;
			
			// parallel (?) retrieval of metadata per table..
			ForkJoinPool forkJoinPool = new ForkJoinPool(12);
			
			return forkJoinPool.submit(() ->
					tables.parallel()
	                .map(tbl -> this.mapWithPrivilege(tbl))
	                .map(tbl -> this.mapWithKey(tbl))
					)
					.get()
					.collect(Collectors.toList());
			
		
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			throw new ScannerException("error retrieving tables " + ex.getMessage(), ex);
		} finally {
			returnPooledConnection(con);
		}
		
	}
	
	

	/**
	 * return all columns for the given tables.
	 * 
	 * @exception ScannerException
	 * @param table for which table should the tables retrieved
	 * @return all columns
	 */
	@Override
	@NotNull public List<IDbColumn> getColumns(@NotNull final IDbTable table) {

		LOG.debug("retrieve columns for " + table);
		Connection con = getConnection();
		try {
			List<IDbColumn> columns = new ArrayList<>();
			try (ResultSet rsColumn = con.getMetaData().getColumns(
					Require.notNull(table).getCatalog(), 
					table.getSchema(), 
					Require.notNull(table).getName(), 
					null)) {
						
			    while (rsColumn.next()) {
			    	columns.add(
			    			new DbColumnDefaultData(
			    			rsColumn.getString("COLUMN_NAME"),
			    			rsColumn.getString("TYPE_NAME"),
			    			rsColumn.getString("REMARKS"),
			    			rsColumn.getInt("COLUMN_SIZE"),
			    			rsColumn.getInt("DECIMAL_DIGITS"),
			    			rsColumn.getString("IS_NULLABLE")
			    			));
		        }
			    rsColumn.close();
			}
			    
			LOG.debug("retrieve primary key information for " + table);
			try (ResultSet rsKey = con.getMetaData().getPrimaryKeys(
				    		table.getCatalog(), 
				    		table.getSchema(), 
				    		table.getName())) {
				    while (rsKey.next()) {
					    String columnName = rsKey.getString("COLUMN_NAME");
					    int    keySeq     = rsKey.getInt("KEY_SEQ");
					    LOG.debug("found PK column: " + columnName + " index " + keySeq);
					    
							columns
								.stream()
								.filter(c -> c.getName().equals(columnName))
								.findFirst()
								.ifPresent(c -> c.setPrimaryKey(keySeq));
			        } 
				    rsKey.close();
			} catch (SQLException ex) {
				LOG.error("primary key information not processed for " + table, ex);
			}
		    
		    
		    LOG.debug("retrieve foreign key information for " + table);
		    try (ResultSet rsKey = con.getMetaData().getImportedKeys(
			    		table.getCatalog(), 
			    		table.getSchema(), 
			    		table.getName())) {
			    
			    while (rsKey.next()) {

				    String fkColumnName = rsKey.getString("FKCOLUMN_NAME");
				    String name       =  rsKey.getString("FK_NAME");
					int    keySeq 	  =  rsKey.getInt("KEY_SEQ");
					String refCatalog =  rsKey.getString("PKTABLE_CAT");
					String refSchema  =  rsKey.getString("PKTABLE_SCHEM");
					String refTable   =  rsKey.getString("PKTABLE_NAME");
					String refColumn  =  rsKey.getString("PKCOLUMN_NAME");
					
					LOG.debug("found foreign key reference for " + fkColumnName + " to " + refTable + "." + refColumn);
					
			    	columns.stream()
			    		   .filter(c -> c.getName().equals(fkColumnName))
			    		   .findFirst()
			    		   .ifPresent(c-> {			    			   
								c.addForeignKeyReference(
										   new DbForeignKeyReference(
												   name,
												   fkColumnName,
												   keySeq,
												   refCatalog,
												   refSchema,
												   refTable,
												   refColumn
												   )
										   );			    			   
			    		   });				    
				    
		        }
			    rsKey.close();
		    } catch (SQLException ex) {
	        	LOG.error("foreign key information not processed for table " + table.getName(), ex);
		    }
		    return columns;
		    
		} catch (Exception ex) {
			throw new ScannerException("error retrieving colums for " + table + ", " + ex.getMessage(), ex);
        } finally {
        	returnPooledConnection(con);
        }
	}

	/** a list of db schema for which the documentation should be generated. */
	@Override
	public void setSchemaFilter(final String[] argSchema) {
		this.schemaToDocument = argSchema != null ? argSchema.clone() : null;
	}

	/** execute an SELECT query.
	 * 
	 * @param table			table where the resulting data table should belong to.
	 * @param sqlSelect		sql query
	 * 
	 * @return a disconnect rowset with the selected data.
	 */
	@Override
	public CachedRowSet getQueryData(@NotNull final IDbTable table, @NotNull final String sqlSelect) {

		RowSetFactory rowSetFactory = null;
		CachedRowSet rowSet = null;

		Connection con = getConnection();
		try (Statement stmt = con.createStatement()) {
			ResultSet rsQuery = stmt.executeQuery(Require.notNull(sqlSelect, "sqlSelect"));

			rowSetFactory = RowSetProvider.newFactory();
			rowSet = rowSetFactory.createCachedRowSet();

			rowSet.populate(rsQuery);
			
			rsQuery.close();
			return rowSet;
		} catch (Exception ex) {
			LOG.error("error retrieving data für table " + table.fqnName() + ": " + ex.getMessage(), ex);
			throw new ScannerException("error retrieving data from " + table.fqnName(), ex);
		} finally {
			returnPooledConnection(con);
		}
	}

	public boolean isUseParallelStream() {
		return useParallelStream;
	}

	public void setUseParallelStream(boolean useParallelStream) {
		this.useParallelStream = useParallelStream;
	}


	@Override
	public void close() throws Exception {
		if (this.dataSource != null) {
			LOG.debug("closing db connection pool in scanner");
			((HikariDataSource) this.dataSource).close();
		}
		this.dataSource = null;		
	}

}
