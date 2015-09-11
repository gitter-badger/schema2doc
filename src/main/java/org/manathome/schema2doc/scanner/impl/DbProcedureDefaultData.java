package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbProcedure;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

/** pl/sql code. */
public class DbProcedureDefaultData implements IDbProcedure {

	private String catalog;
	private String schema;
	private String name;
	private String comment;
	
	public DbProcedureDefaultData(String catalog, String schema, @NotNull String name, String comment) {
		this.catalog = catalog;
		this.schema = schema;
		this.name = Require.notNull(name);
		this.comment = comment;
	}
	
	@Override
	public String getCatalog() {
		return catalog;
	}

	@Override
	public String getSchema() {
		return schema;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getComment() {
		return comment;
	}

}
