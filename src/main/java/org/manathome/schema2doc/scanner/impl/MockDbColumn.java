package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbColumn;

/** description of one column in a specific table. */
public class MockDbColumn implements IDbColumn {
	
	private String  name;
	private String  typeName;
	private String  comment;
	private int     length = 30;
	private boolean isNullable = true;

	public MockDbColumn(String name, String typeName, String comment) {
		this.name = name;
		this.typeName = typeName;
		this.comment = comment;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTypename() {
		return typeName;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public boolean isNullable() {
		return isNullable;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public int compareTo(IDbColumn otherColumn) {
		return this.getName().compareTo(otherColumn.getName());
	}

}
