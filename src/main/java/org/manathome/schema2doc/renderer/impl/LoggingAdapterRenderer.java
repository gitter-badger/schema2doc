package org.manathome.schema2doc.renderer.impl;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * debug output during rendering phase..
 * @author man-at-home
 */
public final class LoggingAdapterRenderer implements IRenderer {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingAdapterRenderer.class);
	private IRenderer wrappedRenderer; 
	
	public LoggingAdapterRenderer(final IRenderer wrappedRenderer) {
		this.wrappedRenderer = wrappedRenderer;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#beginRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void beginRenderTable(IDbTable table) {
		LOG.debug("------------------------------------------");
		LOG.debug("Rendering table {}", table.getName());
		LOG.debug("          comment {}", table.getComment());
		wrappedRenderer.beginRenderTable(table);
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#endRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void endRenderTable(IDbTable table) {
		wrappedRenderer.endRenderTable(table);
		LOG.debug("------------------------------------------");
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#renderColumn(org.manathome.schema2doc.scanner.IDbColumn)
	 */
	@Override
	public void renderColumn(IDbColumn column) {
		LOG.debug("| {}: {}, {}", column.getName(), column.getTypename(), column.getLength());
		wrappedRenderer.renderColumn(column);
	}

}
