package org.manathome.schema2doc.renderer.impl;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Date;

/** render output to console (out).. */
public class PlaintextRenderer implements IRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(PlaintextRenderer.class);
   
    private PrintWriter out = null;

    /** out stream to render to. */
	public PlaintextRenderer(@NotNull final PrintWriter out) {
		LOG.debug("using console renderer..");
		this.out = Require.notNull(out, "required: out");
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.impl.IRenderer#beginRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void beginRenderTable(@NotNull IDbTable table) {
		out.println("---------------------------------------------------------------------");
		out.println("- " + Require.notNull(table).getName() + " .. " + Convert.nvl(table.getComment(), ""));
		out.println("---------------------------------------------------------------------");		
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.impl.IRenderer#endRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */	
	@Override
	public void endRenderTable(@NotNull IDbTable table) {
		out.println("---------------------------------------------------------------------");		
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.impl.IRenderer#renderColumn(org.manathome.schema2doc.scanner.IDbColumn)
	 */
	@Override
	public void renderColumn(@NotNull IDbColumn column) {
		out.println(
				"- " 
				+ Require.notNull(column).getName() 
				+ "(" 
				+ column.getTypename() + ": " 
				+ Convert.nvl(column.getComment(), ""));
	}

	@Override
	public void close() throws Exception {
		out.flush();
		out = null;
	}

	@Override
	public void beginRenderDocumentation() {
		out.println("*********************************************************************");
		out.println("** schema2doc plaintext documentation                              **");
		out.println("*********************************************************************");
	}

	@Override
	public void endRenderDocumentation() {
		out.println("");
		out.println("created at " + new Date());		
	}

	@Override
	public void renderCatalog(String catalog) {
		out.println("=====================================================================");
		out.println("== Catalog: " + catalog);
		out.println("=====================================================================");
	}

	@Override
	public void renderSchema(String schema) {
		out.println("=====================================================================");
		out.println("== Schema: " + schema);
		out.println("=====================================================================");
	}

}
