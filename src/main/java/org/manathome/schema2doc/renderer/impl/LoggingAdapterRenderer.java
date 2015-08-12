package org.manathome.schema2doc.renderer.impl;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * debug output during rendering phase..
 * @author man-at-home
 */
public final class LoggingAdapterRenderer implements IRenderer {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingAdapterRenderer.class);
	private IRenderer wrappedRenderer; 
	
	public LoggingAdapterRenderer(@NotNull final IRenderer wrappedRenderer) {
		this.wrappedRenderer = wrappedRenderer;
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#beginRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void beginRenderTable(@NotNull IDbTable table) {
		if (table == null) {
			LOG.error("table is null");
		} else {			
			LOG.debug("------------------------------------------");
			LOG.debug("Rendering table {}", table.getName());
			LOG.debug("          comment {}", table.getComment());
		}
		wrappedRenderer.beginRenderTable(table);
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#endRenderTable(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public void endRenderTable(@NotNull IDbTable table) {
		wrappedRenderer.endRenderTable(table);
		
		if (table != null) {
			LOG.debug("------------------------------------------");
		}
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.renderer.IRenderer#renderColumn(org.manathome.schema2doc.scanner.IDbColumn)
	 */
	@Override
	public void renderColumn(@NotNull IDbColumn column) {
		if (column != null) {
			LOG.debug("| {}: {}, {}", column.getName(), column.getTypename(), column.getSize());
		} else {
			LOG.error("column is null");
		}
		wrappedRenderer.renderColumn(column);
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public void beginRenderDocumentation() {
		LOG.debug("begin render documentation");
		wrappedRenderer.beginRenderDocumentation();		
	}

	@Override
	public void endRenderDocumentation() {
		LOG.debug("end render documentation");
		wrappedRenderer.endRenderDocumentation();				
	}

	@Override
	public void renderCatalog(String catalog) {
		LOG.debug("render catalog " + catalog);
		wrappedRenderer.renderCatalog(catalog);		
	}

	@Override
	public void renderSchema(String schema) {
		LOG.debug("render schema " + schema);
		wrappedRenderer.renderSchema(schema);		
	}

}
