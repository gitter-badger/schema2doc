package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.NotNull;

/** meta data for database table. */
public interface IDbTable extends Comparable<IDbTable>{
	
	/** table name. */
	@NotNull public String getName();
	
	/** get documentation for table. */
	public String getComment();

	/** optional catalog this table belongs to. */
	public String getCatalog();

	/** optional schema this table belongs to. */
	public String getSchema();

	/** default implementation of full qualified table name, giving string in "catalog.schema.name" form. */
	@NotNull public default String fqnName() {
		return Convert.nvl2(getCatalog(), getCatalog() + ".", "")  +
		       Convert.nvl2(getSchema(), getSchema() + ".", "") +
		       getName();
	}
}
