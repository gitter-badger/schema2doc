package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.util.NotNull;

import java.util.List;
import java.util.stream.Stream;

import javax.sql.rowset.CachedRowSet;

/** interface for database data retrievers. */
public interface IScanner extends AutoCloseable {

	/** gets a list of tables. */
	@NotNull Stream<IDbTable> getTables();

	/** gets a list of table columns for a given table. */
	@NotNull List<IDbColumn> getColumns(@NotNull IDbTable table);
	
	/** get a list of database procedures. */
	@NotNull Stream<IDbProcedure> getProcedures();
	
	/** 
	 * retrieve arbitrary data samples for tables by executing given sql select. may return null if not supported. 
	 * 
	 * @throws ScannerException on sql errors.
	 * @return disconnected row set with data.
	 * */
	CachedRowSet getQueryData(@NotNull IDbTable table, @NotNull String sqlSelect);

	/** optional: restrict db documentation to these schemas. */
	void setSchemaFilter(String[] argSchema);

}
