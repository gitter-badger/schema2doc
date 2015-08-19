package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.NotNull;

/** describes a fk relation for a given table column. */
public interface IForeignKeyReference extends IReference {

	/** foreign key name. */
	String getName();

	String getColumn();

	int getIndex();

	String getReferencedCatalog();

	String getReferencedSchema();

	@NotNull String getReferencedTable();

	@NotNull String getReferencedColumn();
	
	default String display() { 
		return Convert.nvl2(getReferencedCatalog(), getReferencedCatalog() + ".", "") +  
			   Convert.nvl2(getReferencedSchema(), getReferencedSchema() + ".",  "") +
			   getReferencedTable() +
			   "[" + getReferencedColumn() + "(" + getIndex() + ")]";
	}
}
