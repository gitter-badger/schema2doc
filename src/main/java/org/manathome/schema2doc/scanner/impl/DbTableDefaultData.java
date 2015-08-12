package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

/** test data. */
public class DbTableDefaultData implements IDbTable {
	
	@Override
	public String toString() {
		return "Table[" + name + "]";
	}

	private String catalog;
	private String schema;
	private String name;
	private String comment;

	public DbTableDefaultData(final String catalog, final String schema, @NotNull final String name, final String comment) {
		this.catalog = catalog;
		this.schema = schema;
		this.name = Require.notNull(name, "tableName");
		this.comment = comment;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public int compareTo(IDbTable otherTable) {
		return this.getName().compareTo(otherTable.getName());
	}

	@Override
	public String getCatalog() {
		return this.catalog;
	}

	@Override
	public String getSchema() {
		return this.schema;
	}

}
