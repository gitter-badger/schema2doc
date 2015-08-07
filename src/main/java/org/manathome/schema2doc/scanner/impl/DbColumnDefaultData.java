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
	private int     length = 0;
	private boolean isNullable = true;

	public DbColumnDefaultData(@NotNull final String name, @NotNull final String typeName, String comment) {
		this.name = Require.notNull(name, "name required");
		this.typeName = Require.notNull(typeName , "typeName required");
		this.comment = comment;
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

	/** max length. */
	@Override
	public int getLength() {
		return length;
	}

	@Override
	public boolean isNullable() {
		return isNullable;
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
		return "Column[" + name + ", " + typeName + "]";
	}

}
