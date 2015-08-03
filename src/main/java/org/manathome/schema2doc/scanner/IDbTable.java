package org.manathome.schema2doc.scanner;

/** meta data for database table. */
public interface IDbTable extends Comparable<IDbTable>{
	
	/** table name. */
	public String getName();
	
	/** get documentation for table. */
	public String getComment();		
}
