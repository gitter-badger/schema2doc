package org.manathome.schema2doc.renderer.impl;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** render output to console.. */
public class ConsoleRenderer implements IRenderer {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConsoleRenderer.class);
	  
	public ConsoleRenderer() {
		LOG.debug("using Console Renderer..");
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.impl.IRenderer#beginRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void beginRenderTable(IDbTable table) {
		System.out.println("---------------------------------------------------------------------");
		System.out.println("- " + table.getName());
		System.out.println("---------------------------------------------------------------------");		
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.impl.IRenderer#endRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */	
	@Override
	public void endRenderTable(IDbTable table) {
		System.out.println("---------------------------------------------------------------------");		
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.impl.IRenderer#renderColumn(org.manathome.schema2doc.scanner.IDbColumn)
	 */
	@Override
	public void renderColumn(IDbColumn column) {
		System.out.println("- " + column.getName() + ": " + column.getName());
	}

}
