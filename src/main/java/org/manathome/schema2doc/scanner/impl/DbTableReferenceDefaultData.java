package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IReference;
import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.Require;

/** reference to/from another table. */
public class DbTableReferenceDefaultData implements IReference {

	private String catalog;
	private String schema;
	private String table;

	public DbTableReferenceDefaultData(String catalog, String schema, String table) {
		this.catalog = catalog;
		this.schema = schema;
		this.table = Require.notNull(table);
	}

	@Override
	public String display() {
		return Convert.nvl2(catalog, catalog + ".", "") +  
			   Convert.nvl2(schema, schema + ".",  "") +
			   table;
	}

	@Override
	public int compareTo(IReference otherRef) {
		return display().compareTo(Require.notNull(otherRef.display()));
	}

}
