package org.manathome.schema2doc;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** entry point to tool. */
public class Schema2Doc {
	
	private static final Logger LOG = LoggerFactory.getLogger(Schema2Doc.class);
	
	private IScanner  scanner;
	private IRenderer renderer;
	private int tableCnt = 0;
	  
	public Schema2Doc(@NotNull final IScanner scanner, @NotNull final IRenderer renderer) {
		this.scanner = Require.notNull(scanner);
		this.renderer = Require.notNull(renderer);
	}
	
	public int getRenderedTables() { 
		return this.tableCnt; 
	}
	
	/** pump through scanner -> renderer pipeline. */
	public void process() throws Exception {
		LOG.info("running Schema2Doc ..");
		
			scanner.getTables().forEach(table -> 
			{
				renderer.beginRenderTable(table);
				tableCnt++;
				scanner.getColumns(table).forEach(column -> renderer.renderColumn(column));
				renderer.endRenderTable(table);
			});
		
		LOG.info("running Schema2Doc done.");	
	}
}
