package org.manathome.schema2doc.scanner;

/** Reference to another table. */
public interface ITableReference extends IReference {

	String getCatalog();
	String getSchema();
	String getTable();
}
