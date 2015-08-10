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
import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.manathome.schema2doc.scanner.impl.OracleScanner;
import org.manathome.schema2doc.util.Ensure;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * command line interface.
 * 
 * @author mat-at-home
 * @since 2015
 */
public class Schema2DocCmd {

	private static final Logger LOG = LoggerFactory.getLogger(Schema2DocCmd.class);

	/**
	 * command line entry point.
	 * 
	 * @param args
	 *            see -help command
	 * @see    Schema2DocCmd#printHelp()
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LOG.debug("schema2doc, running commandline");

		final CommandLine cmdLine = parseArguments(args);

		if (cmdLine.hasOption("help")) {
			printHelp(new PrintWriter(System.out));
		} else {

			LOG.debug("prepare schema2doc..");
			final IScanner scanner = prepareScanner(cmdLine);
			final IRenderer renderer = prepareRenderer(cmdLine);

			LOG.debug("scanning database and render docs..");
			Schema2Doc s2d = new Schema2Doc(scanner, renderer);
			s2d.process();
			LOG.debug("processing done..");
			System.out.println("processed " + s2d.getRenderedTables() + " tables.");
		}
	}

	@NotNull public static CommandLine parseArguments(@NotNull final String[] args) throws ParseException {
		try {
			LOG.debug("parse command line");
			final CommandLineParser parser = new DefaultParser();
			final CommandLine cmdLine = parser.parse(createCommandLineOptions(), args);

			return Ensure.notNull(cmdLine, "cmdLine");
		} catch (ParseException exp) {
			LOG.error("error parsing", exp);
			throw exp;
		}
	}

	/** create suitable renderer from command line args. */
	@NotNull public static IRenderer prepareRenderer(@NotNull final CommandLine cmdLine) throws Exception {
		return new AsciidocRenderer(System.out); // TBD: fill with commandline
	}

	/** create suitable scanner from command line args. */
	@NotNull public static IScanner prepareScanner(@NotNull final CommandLine cmdLine) throws Exception {

		LOG.debug("create scanner from commandline arguments..");

		Connection conn = null;
		IScanner scanner = null;

		try {

			final String argScanner = Require.notNull(cmdLine).getOptionValue("scanner", "H2");
			if (!"Mock".equalsIgnoreCase(argScanner)) {
				if (!cmdLine.hasOption("connection")) {
					throw new Exception("required jdbc <connection> argument missing");
				}
				final String argJdbcUrl = cmdLine.getOptionValue("connection");
				final String argDriverClass = cmdLine.getOptionValue("driver", "org.h2.Driver");

				Class<?> clazz = Class.forName(argDriverClass); // load database
																// driver..
				LOG.debug("using jdbc driver: " + clazz.getName());

				final String argUser = cmdLine.getOptionValue("user");
				final String argPw = cmdLine.getOptionValue("password");
				LOG.debug("connecting to " + argJdbcUrl + " as " + argUser + " "
						+ (argPw == null ? "without pw" : " with pw"));

				conn = DriverManager.getConnection(argJdbcUrl, argUser, argPw);
				scanner = "Oracle".equalsIgnoreCase(argScanner) ? new OracleScanner(conn) : new GenericDbScanner(conn);
				LOG.debug("using scanner: " + scanner.getClass().getName());
			} else {
				scanner = new MockScanner();
			}

			return Ensure.notNull(scanner, "scanner");
		} catch (Exception ex) {
			LOG.error("error creating db scanner", ex);
			throw ex;
		}
	}

	/** define command line options with commons-cli. */
	@NotNull public static Options createCommandLineOptions() {

		Options cmdOptions = new Options();

		cmdOptions.addOption(Option.builder("c").desc("jdbc connection string for the db to be documented.")
				.argName("url").longOpt("connection").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("u").desc("database user used for connect").argName("dbuser")
				.longOpt("user").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("p").desc("database password used for connect").argName("dbpw")
				.longOpt("password").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("driver").desc("jdbc driver class (fqn) to use").argName("className")
				.numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("scanner").desc("one of (currently): Oracle, GenericDb or Mock")
				.argName("implementation").numberOfArgs(1).build());
		cmdOptions.addOption(Option.builder("o")
				.desc("output directory for asciidoc output (default is stdout/console)").longOpt("out").build());
		cmdOptions.addOption(Option.builder("h").desc("command line help").longOpt("help").build());

		return Ensure.notNull(cmdOptions, "cmdOptions");
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
