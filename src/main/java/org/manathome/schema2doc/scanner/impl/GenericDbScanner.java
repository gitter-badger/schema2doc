package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbColumn;
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
import java.util.stream.Stream;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

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
	private Connection connection;

	/** list of schema or null for all. */
	private String[] schemaToDocument = null;

	public GenericDbScanner(@NotNull final Connection connection) {
		this.connection = Require.notNull(connection);
	}

	/**
	 * get table data retrieved with getMetaData().getTables().
	 * 
	 * @exception ScannerException
	 * @return list of tables in db.
	 */
	@Override
	@NotNull
	public Stream<IDbTable> getTables() {
		LOG.debug("retrieve tables");
		try {
			
			List<IDbTable> tables = new ArrayList<IDbTable>();
			ResultSet rsTable = connection.getMetaData().getTables(null, null, "%", null);
			while (rsTable.next()) {
				final String schema = rsTable.getString("TABLE_SCHEM");
				if (schemaToDocument == null
						|| Arrays.stream(schemaToDocument).anyMatch(s -> s.equalsIgnoreCase(schema))) {
					tables.add(new DbTableDefaultData(rsTable.getString("TABLE_CAT"), schema,
							rsTable.getString("TABLE_NAME"), rsTable.getString("REMARKS")));
				}
			}
			rsTable.close();

			for (IDbTable table : tables) {
				try (ResultSet rsPrivilege = connection.getMetaData()
						.getTablePrivileges(table.getCatalog(),	table.getSchema(), table.getName())) {
					while (rsPrivilege.next()) {
						table.addPrivilege(new DbPrivilegeDefaultData(rsPrivilege.getString("GRANTEE"),
								rsPrivilege.getString("PRIVILEGE")));
					}
					rsPrivilege.close();
				}
			}

			for (IDbTable table : tables) {
				try (ResultSet rsKey = connection.getMetaData().getExportedKeys(table.getCatalog(), table.getSchema(),
						table.getName())) {

					while (rsKey.next()) {
						table.addReferrer(new DbTableReferenceDefaultData(rsKey.getString("FKTABLE_CAT"),
								rsKey.getString("FKTABLE_SCHEM"), rsKey.getString("FKTABLE_NAME")));
					}
					rsKey.close();
				}
			}

			return tables.stream();
		} catch (Exception ex) {
			throw new ScannerException("error retrieving tables " + ex.getMessage(), ex);
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
	@NotNull public Stream<IDbColumn> getColumns(@NotNull final IDbTable table) {
		LOG.debug("retrieve columns for " + table);
		try {
			List<IDbColumn> columns = new ArrayList<>();
			try (ResultSet rsColumn = connection.getMetaData().getColumns(
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
			try (ResultSet rsKey = connection.getMetaData().getPrimaryKeys(
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
		    try (ResultSet rsKey = connection.getMetaData().getImportedKeys(
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
		    
		    return columns.stream();
		    
		} catch (Exception ex) {
			throw new ScannerException("error retrieving colums for " + table + ", " + ex.getMessage(), ex);
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

		try (Statement stmt = Require.notNull(this.connection, "connection").createStatement()) {
			ResultSet rsQuery = stmt.executeQuery(Require.notNull(sqlSelect, "sqlSelect"));

			rowSetFactory = RowSetProvider.newFactory();
			rowSet = rowSetFactory.createCachedRowSet();

			rowSet.populate(rsQuery);
			
			rsQuery.close();
			return rowSet;
		} catch (Exception ex) {
			LOG.error("error retrieving data für table " + table.fqnName() + ": " + ex.getMessage(), ex);
			throw new ScannerException("error retrieving data from " + table.fqnName(), ex);
		}
	}

}
