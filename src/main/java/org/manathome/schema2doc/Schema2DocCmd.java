package org.manathome.schema2doc;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.MockScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

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
	* @param  args see -help command
	* @see    Schema2DocCmd#printHelp()
    * @throws Exception 
	*/
	public static void main(String[] args) throws Exception {
		System.out.println("processing oracle schema");
		LOG.debug("running commandline");
		
		
	    CommandLineParser parser = new DefaultParser();
	    try {
	        final CommandLine cmdLine = parser.parse(createCommandLineOptions(), args);
	    
	        if (cmdLine.hasOption("help")) {
	        	printHelp(new PrintWriter(System.out));
	        } else {
				final IScanner  scanner =  new MockScanner();					// TBD: fill with commandline
				final IRenderer renderer = new AsciidocRenderer(System.out);	// TBD: fill with commandline
				Schema2Doc s2d = new Schema2Doc(scanner, renderer);
				s2d.process();
				System.out.println("processed " + s2d.getRenderedTables() + " tables.");
	        }
			
	    } catch (ParseException exp) {
	        System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	    }
	}
	
	
	
	/** define command line options with commons-cli. */ 
	public static Options createCommandLineOptions() {
	
		Options cmdOptions = new Options();
		
		cmdOptions.addOption(Option.builder("c").desc("jdbc connection string for the db to be documented.")
				.argName("url").longOpt("connection").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("driver").desc("jdbc driver class (fqn) to use, default is ..")
				.argName("className").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("o")
				.desc("output directory for asciidoc output (default is stdout/console)")
				.longOpt("out").build());
		cmdOptions.addOption(Option.builder("h").desc("command line help").longOpt("help").build());
		
		return cmdOptions;
	}
	
	/** print help (on -help command). */
	public static void printHelp(PrintWriter pw) {
		LOG.debug("printing help");
		
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(
				pw, 80, "schema2doc", 
				"\nschema2doc tool, retrieves database metadata from a db an writes asciidoc docs with it.\n\n", 
				createCommandLineOptions(), 3, 5,
				"\nasciidoc documenation generator for your database", true
				);
		
		pw.flush();
	}

}
