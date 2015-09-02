package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IForeignKeyReference;
import org.manathome.schema2doc.scanner.IReference;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

/** reference from column to referenced table. */
public class DbForeignKeyReference implements IForeignKeyReference {
	
	private String name;
	private String column;
	private int    index = 0;

	private String referencedCatalog;
	private String referencedSchema;
	private String referencedTable;
	private String referencedColumn;

	public DbForeignKeyReference(@NotNull String name, String column, int index,
			String referencedCatalog, String referencedSchema,
			@NotNull String referencedTable, @NotNull String referencedColumn) {
		
		this.name = Require.notNull(name, "fk name");
		this.column = column;
		this.index = index;
		
		this.referencedCatalog = referencedCatalog;
		this.referencedSchema = referencedSchema;
		this.referencedTable = Require.notNull(referencedTable, "ref table");
		this.referencedColumn = Require.notNull(referencedColumn, "ref column");
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


	@Override
	public int compareTo(IReference o) {
		return this.display().compareTo(o.display());
	}

}
