package org.manathome.schema2doc.scanner;

/** meta data for database column in table. */
public interface IDbColumn extends Comparable<IDbColumn> {
	
	public String 	getName();
	public String 	getTypename();
	public int 		getLength();
	public boolean 	isNullable();
	String getComment();

}
