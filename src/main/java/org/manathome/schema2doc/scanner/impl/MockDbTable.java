package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbTable;

/** test data. */
public class MockDbTable implements IDbTable {
	
	private String name;
	private String comment;

	public MockDbTable(final String name, final String comment) {
		this.name = name;
		this.comment = comment;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public int compareTo(IDbTable otherTable) {
		return this.getName().compareTo(otherTable.getName());
	}

}
