package org.manathome.schema2doc.scanner;

/** meta data for database column in table. */
public interface IDbColumn extends Comparable<IDbColumn> {
	
	public String 	getName();
	public String 	getTypename();
	public Integer 	getSize();
	public Integer  getPrecision();
	public boolean 	isNullable();
	String getComment();
	public void setPrimaryKey(int pkIndex);
	public boolean  isPrimaryKey();

}
