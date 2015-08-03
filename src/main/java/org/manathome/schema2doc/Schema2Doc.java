package org.manathome.schema2doc;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.ConsoleRenderer;
import org.manathome.schema2doc.renderer.impl.LoggingAdapterRenderer;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * command line interface.
 * 
 * @author mat-at-home
 * @since  2015
 */
public class Schema2Doc {

   private static final Logger LOG = LoggerFactory.getLogger(Schema2Doc.class);
 
   /**
    * command line entry point.
    *  
	* @param args
	*/
	public static void main(String[] args) {
		LOG.info("running Schema2Doc ..");
		
		MockScanner scanner = new MockScanner();
		IRenderer   renderer = new LoggingAdapterRenderer(new ConsoleRenderer());
		
		scanner.getTables().forEach(table -> 
		{
			renderer.beginRenderTable(table);
			scanner.getColumns(table).forEach(column -> renderer.renderColumn(column));
			renderer.endRenderTable(table);
		});
		
		LOG.info("running Schema2Doc done.");
	}

}
