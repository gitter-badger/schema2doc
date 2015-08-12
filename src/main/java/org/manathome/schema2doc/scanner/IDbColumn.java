package org.manathome.schema2doc.scanner;

import java.util.stream.Stream;

/** meta data for database column in table. */
public interface IDbColumn extends Comparable<IDbColumn> {
	
	public String 	getName();
	public String 	getTypename();
	public Integer 	getSize();
	public Integer  getPrecision();
	public boolean 	isNullable();
	public String 	getComment();
	
	public void 	setPrimaryKey(int pkIndex);
	public boolean  isPrimaryKey();
	public Integer  getPrimaryKeyIndex();
	
	public void 	addForeignKeyReference(IForeignKeyReference reference);
	public Stream<IForeignKeyReference> getForeignKeyReferences();
}
