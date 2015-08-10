package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

/** description of one column in a specific table. 
 */
public class DbColumnDefaultData implements IDbColumn {
	
	private String  name;
	private String  typeName;
	private String  comment;
	private Integer size = 0;
	private Integer precision = 0;
	private boolean isNullable = false;
	private int     primaryKeyIndex = 0;

	public DbColumnDefaultData(
			@NotNull final String name, 
			@NotNull final String typeName, 
			String comment, 
			int    size, 
			int    precision,
			String  nullableDesc) {
		this.name = Require.notNull(name, "name required");
		this.typeName = Require.notNull(typeName , "typeName required");
		this.comment = comment;
		this.size = size <= 0 ? null : new Integer(size);
		this.precision = precision == 0 ? null : new Integer(precision);
		this.isNullable = "YES".equalsIgnoreCase(nullableDesc);
	}

	/** column name. */
	@Override
	public String getName() {
		return name;
	}

	/** data type of column. */
	@Override
	public String getTypename() {
		return typeName;
	}

	/** max length (or null if not applicable). */
	@Override
	public Integer getSize() {
		return size;
	}
	
	/** max precision (or null if not applicable). */
	@Override
	public Integer getPrecision() {
		return this.precision;
	}

	
	@Override
	public boolean isNullable() {
		return this.isNullable;
	}

	/** optional comment. */
	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public int compareTo(IDbColumn otherColumn) {
		return this.getName().compareTo(otherColumn.getName());
	}
	
	@Override
	public String toString() {
		return "Column[" + name + ", " + typeName + "," + this.size + ", PK: " + this.isPrimaryKey() + "]";
	}

	@Override
	public void setPrimaryKey(int pkIndex) {
		this.primaryKeyIndex = pkIndex;
	}
	
	@Override
	public boolean isPrimaryKey() { 
		return this.primaryKeyIndex > 0; 
	}

}
