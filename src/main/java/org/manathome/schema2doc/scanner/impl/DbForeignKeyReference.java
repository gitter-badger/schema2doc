package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IForeignKeyReference;

/** reference from column to referenced table. */
public class DbForeignKeyReference implements IForeignKeyReference {
	
	private String name;
	private String column;
	private int    index = 0;

	private String referencedCatalog;
	private String referencedSchema;
	private String referencedTable;
	private String referencedColumn;

	public DbForeignKeyReference(String name, String column, int index,
			String referencedCatalog, String referencedSchema,
			String referencedTable, String referencedColumn) {
		this.name = name;
		this.column = column;
		this.index = index;
		this.referencedCatalog = referencedCatalog;
		this.referencedTable = referencedTable;
		this.referencedSchema = referencedSchema;
	}
	
	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getColumn()
	 */
	@Override
	public String getColumn() {
		return column;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getIndex()
	 */
	@Override
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getReferencedCatalog()
	 */
	@Override
	public String getReferencedCatalog() {
		return referencedCatalog;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getReferencedSchema()
	 */
	@Override
	public String getReferencedSchema() {
		return referencedSchema;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getReferencedTable()
	 */
	@Override
	public String getReferencedTable() {
		return referencedTable;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IForeignKeyReference#getReferencedColumn()
	 */
	@Override
	public String getReferencedColumn() {
		return referencedColumn;
	}

}
