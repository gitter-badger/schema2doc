package org.manathome.schema2doc.scanner;

/** stored procedure. */
public interface IDbProcedure {

	String getCatalog();
	String getSchema();
	String getName();
	String getComment();
}
