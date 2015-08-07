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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/** get meta data by jdbc onboard methods. */
public class GenericDbScanner implements IScanner {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericDbScanner.class);

	
	private Connection connection;
	
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
		    	tables.add(
		    			new DbTableDefaultData(
		    			rs.getString("TABLE_NAME"),
		    			rs.getString("REMARKS")
		    			));
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
		LOG.debug("retrieve columns" + table);
		try {
			List<IDbColumn> columns = new ArrayList<>();
			ResultSet rs = connection.getMetaData().getColumns(null, null, Require.notNull(table).getName(), null);
		    while (rs.next()) {
		    	columns.add(
		    			new DbColumnDefaultData(
		    			rs.getString("COLUMN_NAME"),
		    			rs.getString("TYPE_NAME"),
		    			rs.getString("TYPE_NAME")
		    			));
	        }
		    return columns.stream();
		} catch (Exception ex) {
			throw new ScannerException("error retrieving colums for " + table, ex);
		}
	}

}
