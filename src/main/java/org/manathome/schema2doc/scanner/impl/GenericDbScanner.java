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


/** get meta data by jdbc onboard methods. 
 * 
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html">DatabaseMetaData</a>
 * */
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
	@NotNull public Stream<IDbTable> getTables() {
		LOG.debug("retrieve tables");
		try {
			List<IDbTable> tables = new ArrayList<IDbTable>();
			ResultSet rs = connection.getMetaData().getTables(null, null, "%", null);
		    while (rs.next()) {
		    	final String schema = rs.getString("TABLE_SCHEM");
		    	if (schemaToDocument == null || 
		    	    Arrays.stream(schemaToDocument).anyMatch(s -> s.equalsIgnoreCase(schema))) {
			    	tables.add(
			    			new DbTableDefaultData(
			    			rs.getString("TABLE_CAT"),
			    			schema,
			    			rs.getString("TABLE_NAME"),
			    			rs.getString("REMARKS")
			    			));
		    	}
	        }
		    
		    for (IDbTable table : tables) {
				rs = connection.getMetaData()
						.getTablePrivileges(table.getCatalog(), 
	  									    table.getSchema(),
											table.getName());
			    while (rs.next()) {
			    	table.addPrivilege(new DbPrivilegeDefaultData(
			    				rs.getString("GRANTEE"),
			    				rs.getString("PRIVILEGE"))
			    			);
		        }
		    }

		    for (IDbTable table : tables) {
				rs = connection.getMetaData()
						.getExportedKeys(table.getCatalog(), 
	  									 table.getSchema(),
	  									 table.getName());
			    while (rs.next()) {
			    	table.addReferrer(new DbTableReferenceDefaultData(
			    				rs.getString("FKTABLE_CAT"),
			    				rs.getString("FKTABLE_SCHEM"),
			    				rs.getString("FKTABLE_NAME"))
			    			);
		        }
		    }		    
		    
		    return tables.stream();
		} catch (Exception ex) {
			throw new ScannerException("error retrieving tables", ex);
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
			ResultSet rs = connection.getMetaData().getColumns(
					Require.notNull(table).getCatalog(), 
					table.getSchema(), 
					Require.notNull(table).getName(), 
					null)
					;
		    while (rs.next()) {
		    	columns.add(
		    			new DbColumnDefaultData(
		    			rs.getString("COLUMN_NAME"),
		    			rs.getString("TYPE_NAME"),
		    			rs.getString("REMARKS"),
		    			rs.getInt("COLUMN_SIZE"),
		    			rs.getInt("DECIMAL_DIGITS"),
		    			rs.getString("IS_NULLABLE")
		    			));
	        }
		    
		    LOG.debug("retrieve primary key information for " + table);
		    try {
			    rs = connection.getMetaData().getPrimaryKeys(table.getCatalog(), table.getSchema(), table.getName());
			    while (rs.next()) {
				    String columnName = rs.getString("COLUMN_NAME");
				    int    keySeq     = rs.getInt("KEY_SEQ");
				    LOG.debug("found PK column: " + columnName + " index " + keySeq);
				    
						columns
							.stream()
							.filter(c -> c.getName().equals(columnName))
							.findFirst()
							.ifPresent(c -> c.setPrimaryKey(keySeq));
		        } 
		    } catch (SQLException ex) {
	        	LOG.error("primary key information not processed for " + table, ex);
	        }
		    
		    
		    LOG.debug("retrieve foreign key information for " + table);
		    try {
			    rs = connection.getMetaData().getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
			    
			    while (rs.next()) {

				    String fkColumnName = rs.getString("FKCOLUMN_NAME");
				    String name = rs.getString("FK_NAME");
					int keySeq = rs.getInt("KEY_SEQ");
					String refCatalog =  rs.getString("PKTABLE_CAT");
					String refSchema  =  rs.getString("PKTABLE_SCHEM");
					String refTable   =  rs.getString("PKTABLE_NAME");
					String refColumn  =  rs.getString("PKCOLUMN_NAME");
					
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
		    } catch (SQLException ex) {
	        	LOG.error("foreign key information not processed for table " + table.getName(), ex);
	        }		    
		    
		    return columns.stream();
		} catch (Exception ex) {
			throw new ScannerException("error retrieving colums for " + table, ex);
		}
	}

	@Override
	public void setSchemaFilter(final String[] argSchema) {		
		this.schemaToDocument = argSchema != null ? argSchema.clone() : null;		
	}

	@Override
	public CachedRowSet getQueryData(@NotNull final IDbTable table, @NotNull final String sqlSelect) {

		   RowSetFactory rowSetFactory = null;
		   CachedRowSet  rowSet = null;
		   
		   try {			   
			    Statement stmt = Require.notNull(this.connection, "connection").createStatement();
			    ResultSet rs   = stmt.executeQuery(Require.notNull(sqlSelect, "sqlSelect"));

		        rowSetFactory = RowSetProvider.newFactory();
		        rowSet = rowSetFactory.createCachedRowSet();
		        
		        rowSet.populate(rs);
		        return rowSet;
		   } catch (Exception ex) {
			   LOG.error("error retrieving data f�r table " + table.fqnName() + ": " + ex.getMessage(), ex);
			   throw new ScannerException("error retrieving data from " + table.fqnName(), ex);
		   }
	}

}
