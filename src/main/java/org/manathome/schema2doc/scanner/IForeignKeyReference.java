package org.manathome.schema2doc.scanner;

/** describes a fk relation for a given table column. */
public interface IForeignKeyReference {

	/** foreign key name. */
	String getName();

	String getColumn();

	int getIndex();

	String getReferencedCatalog();

	String getReferencedSchema();

	String getReferencedTable();

	String getReferencedColumn();
}
