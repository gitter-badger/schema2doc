/**
 * 
 */
package org.manathome.schema2doc.renderer.impl;


import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/**
 * asciidoc output.
 * 
 * @author man-at-home
 *
 */
public class AsciidocRenderer implements IRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(AsciidocRenderer.class);
    
    private PrintStream out = null;

    /** out stream to render to. */
	public AsciidocRenderer(@NotNull final PrintStream out) {
		LOG.debug("using asciidoc renderer..");
		this.out = Require.notNull(out, "required: out");
	}
	
	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#beginRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void beginRenderTable(@NotNull IDbTable table) {
		out.println("=== " + 
					Convert.nvl2(Require.notNull(table, "table").getCatalog(), table.getCatalog() + ".", "") +
					Convert.nvl2(table.getSchema(), table.getSchema() + "." , "") +
					table.getName());
		out.println(Convert.nvl(table.getComment(), ""));
		
		// begin table columns..
		out.println("|===");
		out.println("|Column | PK | Type | Comment | Size | Constraints");
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#endRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void endRenderTable(@NotNull final IDbTable table) {
		out.println("|==="); // end columns table
		out.println();
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#renderColumn(org.manathome.schema2doc.scanner.IDbColumn)
	 */
	@Override
	public void renderColumn(@NotNull final IDbColumn column) {
		out.println("| " + Require.notNull(column, "column").getName()); 
		out.println("| " + (column.isPrimaryKey() ? "PK " + column.getPrimaryKeyIndex() : ""));
		out.println("| " + column.getTypename()); 
		out.println("| " + Convert.nvl(column.getComment(), ""));
		out.println("| " + (column.getSize() != null ? column.getSize().toString() : ""));
		out.println("| " + (column.getForeignKeyReferences().findAny().isPresent() ? 
				column.getForeignKeyReferences().findAny().get().getName() 
				: ""));
	}

	@Override
	public void close() throws Exception {
		out.flush();
		out = null;
	}
}
