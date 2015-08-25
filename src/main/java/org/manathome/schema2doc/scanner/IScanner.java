package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.util.NotNull;

import java.util.stream.Stream;

/** interface for database data retrievers. */
public interface IScanner {

	/** gets a list of tables. */
	@NotNull Stream<IDbTable> getTables();

	/** gets a list of table columns for a given table. */
	@NotNull Stream<IDbColumn> getColumns(@NotNull IDbTable table);

	/** optional: restrict db documentation to these schemas. */
	void setSchemaFilter(String[] argSchema);

}
