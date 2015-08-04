package org.manathome.schema2doc;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.MockScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * command line interface.
 * 
 * @author mat-at-home
 * @since  2015
 */
public class Schema2DocCmd {

   private static final Logger LOG = LoggerFactory.getLogger(Schema2DocCmd.class);
 
   /**
    * command line entry point.
    *  
	* @param args
    * @throws Exception 
	*/
	public static void main(String[] args) throws Exception {
		System.out.println("processing oracle schema");
		LOG.debug("running commandline");
		final IScanner  scanner =  new MockScanner();					// TBD: fill with commandline
		final IRenderer renderer = new AsciidocRenderer(System.out);	// TBD: fill with commandline
		Schema2Doc s2d = new Schema2Doc(scanner, renderer);
		s2d.process();
		System.out.println("processed " + s2d.getRenderedTables() + " tables.");
	}

}
