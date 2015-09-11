package org.manathome.schema2doc;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.RenderException;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.renderer.impl.LoggingAdapterRenderer;
import org.manathome.schema2doc.renderer.impl.PlaintextRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.ScannerFactory;
import org.manathome.schema2doc.util.Ensure;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * basic command line interface for schema2doc tool.
 * 
 * @author man-at-home
 * @since 2015
 * 
 * @see #main(String[])
 * @see Schema2Doc wrapped tool
 */
public class Schema2DocCmd {
	
	// TBD: remove static from isVerbose et.al., change all to instance methods.. 

	private static final Logger LOG = LoggerFactory.getLogger(Schema2DocCmd.class);
	
	/** verbosity of output, set by -verbose flag. */
	static boolean isVerbose = false;

	/**
	 * command line entry point.
	 * 
	 * @param  args see -help command
	 * @throws Exception
	 * 
	 * @see    Schema2DocCmd#printHelp()
	 * @see    #createCommandLineOptions()
	 */
	public static void main(String[] args) throws Exception {

		LOG.debug("schema2doc, running commandline");

		final CommandLine cmdLine = parseArguments(args);
		
		isVerbose = cmdLine.hasOption("verbose");
		
		if (isVerbose) {
			System.out.println("schema2doc processing db at " + cmdLine.getOptionValue("connection", "?"));			
		}
		
		if (cmdLine.hasOption("help")) {
			printHelp();
		} else {

			LOG.debug("prepare schema2doc..");
			final IScanner scanner   = prepareScanner(cmdLine);
			final IRenderer renderer = prepareRenderer(cmdLine);

			LOG.debug("scanning database and render docs..");
			Schema2Doc s2d = new Schema2Doc(scanner, renderer);
			s2d.setConfigPath(cmdLine.getOptionValue("configPath", "config/schema2doc"));
			
			s2d.process();
			LOG.debug("processing done, " + s2d.getRenderedTables() + " tables ..");
			if (isVerbose) {
				System.out.println("schema2doc processed " + s2d.getRenderedTables() + " tables.");
			}
		}
	}

	/** parse command line arguments with apache cli. 
	 *  @see #main(String[])
	 *  @see #createCommandLineOptions()
	 */
	@NotNull public static CommandLine parseArguments(@NotNull final String[] args) throws ParseException {
		try {
			LOG.debug("parse command line");
			final CommandLineParser parser = new DefaultParser();
			final CommandLine      cmdLine = parser.parse(createCommandLineOptions(), args);

			return Ensure.notNull(cmdLine, "cmdLine");
		} catch (ParseException exp) {
			LOG.error("error parsing", exp);
			throw exp;
		}
	}

	/** create suitable renderer from command line args. */
	@NotNull static IRenderer prepareRenderer(@NotNull final CommandLine cmdLine) throws Exception {
		
		final String  argOutDir   = Require.notNull(cmdLine).getOptionValue("out", null);
		final String  argRenderer = cmdLine.getOptionValue("renderer", "Asciidoc");
		final boolean argGraphics = cmdLine.hasOption("graphics");
		
		return prepareRenderer(argRenderer, argOutDir, argGraphics);
	}

	/** create renderer, plaintext or asciidoc. */
	@NotNull public static IRenderer prepareRenderer(
			@NotNull final String rendererImplementation, 
			@NotNull final String outDir,
			final boolean includeGraphics
			) throws Exception {

		LOG.debug("preparing renderer for " + rendererImplementation + ", " + outDir);

		IRenderer   renderer = null;
		PrintWriter out      = null;
		
		// find suitable renderer
		if (Require.notNull(rendererImplementation, "renderer").toLowerCase().contains("asciidoc")) {
			renderer = new AsciidocRenderer(includeGraphics); 			
		} else if (rendererImplementation.toLowerCase().contains("plaintext")) {
			renderer = new PlaintextRenderer(); 						
		} else {
			throw new RenderException("unknown rendering engine: " + rendererImplementation);
		}
		
		try {
		
		// logging
		if (isVerbose) {
			renderer = new LoggingAdapterRenderer(renderer, true);
		}		
		
		// determine output target (file/stdout).
		if (outDir == null || outDir.length() == 0) {
			out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8"));
		} else {
		
			File parentDir = new File(outDir);
			if (!parentDir.exists() || !parentDir.isDirectory()) {
				throw new RenderException("output directory not valid: " + parentDir.getAbsolutePath());	
			}			
			out = new PrintWriter(
					new OutputStreamWriter(
							new FileOutputStream(
									new File(parentDir.getAbsolutePath() + 
											File.separator + 
											renderer.getSuggestedFilename())
							) , "UTF-8") 
							);
		}		
		renderer.setOut(out);
		
		return renderer;
		
		} catch (final Exception ex) {
			LOG.error("error preparing renderer " + rendererImplementation, ex);
			if (renderer != null) {
				renderer.close();	// close resources.
				renderer = null;
			}
			throw ex;
		}
	}
	
	/** create suitable scanner from command line args. */
	@NotNull static IScanner prepareScanner(@NotNull final CommandLine cmdLine) throws Exception {

		String argJdbcUrl     = null;
		String argDriverClass = null;
		String argUser        = null;
		String argPw          = null;
		String argSchema[]    = null;
		
		final String argScanner = Require.notNull(cmdLine, "cmdLine").getOptionValue("scanner", "H2");
		
		if (!"Mock".equalsIgnoreCase(argScanner)) {
			if (!cmdLine.hasOption("connection")) {
				throw new Exception("required jdbc <connection> argument missing");
			}
			argJdbcUrl     = cmdLine.getOptionValue("connection");
			argDriverClass = cmdLine.getOptionValue("driver", "org.h2.Driver");
			argUser        = cmdLine.getOptionValue("user");
			argPw          = cmdLine.getOptionValue("password");
			argSchema      = cmdLine.hasOption("schema") ? cmdLine.getOptionValues("schema") : null;
		}

		return prepareScanner(argScanner, argJdbcUrl, argDriverClass, argUser, argPw, argSchema);
	}
	
	/** create suitable scanner. */
	@NotNull public static IScanner prepareScanner(
			@NotNull String argScanner,
			@NotNull String argJdbcUrl,
			@NotNull String argDriverClass,
			         String argUser,
			         String argPw,
			         String argSchema[]
			) throws Exception {

		final IScanner scanner =  ScannerFactory
				.getInstance()
				.getScanner(argScanner, argDriverClass, argJdbcUrl, argUser, argPw, isVerbose);

		scanner.setSchemaFilter(argSchema);
			
		return scanner;
	}

	/** define command line options with apache commons-cli. 
	 *  @see #main 
	 */
	@NotNull public static Options createCommandLineOptions() {

		Options cmdOptions = new Options();

		cmdOptions.addOption(Option.builder("scanner").desc("one of (currently): Oracle, GenericDb or Mock")
				.argName("implementation").numberOfArgs(1).build());
		cmdOptions.addOption(org.apache.commons.cli.Option.builder("c").desc("jdbc connection string for the db to be documented.")
				.argName("url").longOpt("connection").numberOfArgs(1).build());
		cmdOptions.addOption(org.apache.commons.cli.Option.builder("u").desc("database user used for connect").argName("dbuser")
				.longOpt("user").numberOfArgs(1).optionalArg(true).build());
		cmdOptions.addOption(Option.builder("p").desc("database password used for connect").argName("dbpw")
				.longOpt("password").numberOfArgs(1).optionalArg(true).build());
		cmdOptions.addOption(Option.builder("driver").desc("jdbc driver class (fqn) to use").argName("className")
				.numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("schema").desc("one or more schema to document, all if empty")
				.argName("schemalist").hasArgs().optionalArg(true).build());
		
		cmdOptions.addOption(Option.builder("configPath")
							       .desc("config path, defaults to config/schema2doc")
							       .argName("path")
							       .numberOfArgs(1)
							       .build());
		
		cmdOptions.addOption(Option.builder("renderer").desc("rendering engine, currently one of: asciidoc or plaintext")
				.argName("implementation").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("o")
				.desc("output directory for asciidoc output (default is stdout/console)")
				.longOpt("out").numberOfArgs(1).optionalArg(true).build());
		
		cmdOptions.addOption(Option.builder("h").desc("command line help").longOpt("help").optionalArg(true).build());
		cmdOptions.addOption(Option.builder("v").desc("verbose output").longOpt("verbose").optionalArg(true).build());
		cmdOptions.addOption(Option.builder("g")
				.desc("include graphics in documentation (asciidoc only)")
				.longOpt("graphics").optionalArg(true).build());
	
		return Ensure.notNull(cmdOptions, "cmdOptions");
	}
	
	/** print help (on -help command) to stdout. */
	public static void printHelp() throws Exception {
		printHelp(new PrintWriter(new OutputStreamWriter(System.out, "UTF-8")));
	}

	/** print help (on -help command). */
	public static void printHelp(PrintWriter pw) {
		LOG.debug("printing help");

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(pw, 80, "schema2doc",
				"\nschema2doc tool, retrieves database metadata from a db an writes asciidoc docs with it.\n\n",
				createCommandLineOptions(), 3, 5, "\nasciidoc documenation generator for your database", true);

		pw.flush();
	}

}
