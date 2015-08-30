package org.manathome.schema2doc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.RenderException;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.manathome.schema2doc.scanner.impl.H2ConnectTest;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/** tests. */
public class Schema2DocCmdTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(Schema2DocCmdTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMockArguments() throws Exception {
		String[] args = new String[] { 
				"-connection" , "egal",
				"-scanner",  "Mock",
				"-verbose"
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		LOG.debug("cmd=" + cmd.toString());
		assertNotNull(cmd);
		assertTrue("connection arg found", cmd.hasOption("connection"));
		assertTrue("scanner arg found", cmd.hasOption("scanner"));
		assertEquals("scanner is Mock", "Mock", cmd.getOptionValue("scanner"));
	}
	
	
	@Test
	public void testCommandLineOptions() throws Exception {
		Options options = Schema2DocCmd.createCommandLineOptions();
		assertThat(options.getOptions().size(), is(11));
		assertTrue(options.hasOption("schema"));
		assertTrue(options.hasOption("connection"));
	}

	
	@Test
	public void testPrepareMockScanner() throws Exception {
		
		IScanner scanner = Schema2DocCmd.prepareScanner("Mock", null, null, null, null, null);
		assertNotNull(scanner);
		assertTrue(scanner.getClass().getName().contains("Mock"));
	}
		
	@Test
	public void testMockScannerFromArgs() throws Exception {
		String[] args = new String[] { 
				"-connection" , "egal",
				"-scanner",  "Mock",
				"-verbose"
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		IRenderer renderer = Schema2DocCmd.prepareRenderer(cmd);
		assertNotNull(renderer);
	}	

	@Test
	public void testPrepareAsciidocRenderer() throws Exception {
		
		IRenderer renderer = Schema2DocCmd.prepareRenderer("asciidoc", null);
		assertNotNull(renderer);
		assertTrue(renderer.getClass().getName().contains("Ascii"));
	}

	@Test
	public void testPreparePlaintextRenderer() throws Exception {
		
		Schema2DocCmd.isVerbose = false;
		IRenderer renderer = Schema2DocCmd.prepareRenderer("Plaintext", null);
		assertNotNull(renderer);
		assertTrue("renderer not expected: " + renderer.getClass().getName(), renderer.getClass().getName().contains("Plaintext"));
	}
	
	@Test(expected = RenderException.class)
	public void testPrepareUnknownRenderer() throws Exception {		
		Schema2DocCmd.prepareRenderer("UnknownRenderer", null);
	}
		
	@Test
	public void testMockShortArguments() throws Exception {
		String[] args = new String[] { 
				"-c" , "egal",
				"-scanner",  "Mock",
				"-u" , "unusedUser",
				"-p" , "unusedPw"
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		LOG.debug("cmd=" + cmd);
		
		assertNotNull(cmd);
		assertTrue("connection arg found", cmd.hasOption("connection"));
		assertTrue("scanner arg found", cmd.hasOption("scanner"));
		assertTrue("user arg found", cmd.hasOption("user"));
		assertTrue("pw arg found", cmd.hasOption("password"));
		assertEquals("user name expected", "unusedUser" , cmd.getOptionValue("user"));
	}	
	

	/** test -schema argument list. */
	@Test
	public void testSchemaArgument() throws Exception {
		String[] args = new String[] { 
				"-schema" , "eins", "zwei.1", "drei"
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		LOG.debug("cmd=" + cmd);
		
		assertNotNull(cmd);
		assertTrue("schema arg found", cmd.hasOption("schema"));
		assertThat("inv. number of -schema arguments", cmd.getOptionValues("schema").length, is(3));
		assertThat(Arrays.asList(cmd.getOptionValues("schema")), hasItem("eins"));
		assertThat(Arrays.asList(cmd.getOptionValues("schema")), hasItem("zwei.1"));
	}	

	/** test omitted -schema argument. */
	@Test
	public void testOmittedSchemaArgument() throws Exception {
		String[] args = new String[] { 
				"-h"
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		LOG.debug("cmd=" + cmd);
		
		assertNotNull(cmd);
		assertTrue("no schema arg",  !cmd.hasOption("schema"));
	}	
	
	@Test(expected = Exception.class)
	public void testMissingConnectionArgument() throws Exception {
		String[] args = new String[] { 
			// NO	"-connection" , "egal",
				"-scanner",  "DbGeneric",
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		Schema2DocCmd.prepareScanner(cmd); // exception expected here..
	}
		
	
	@Test(expected = Exception.class)
	public void testInvalidJdbcDriverArgument() throws Exception {
		String[] args = new String[] { 
				"-connection" , "egal",
				"-scanner",  "DbGeneric",
				"-driver", "not.a.valid.driverClass"
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		Schema2DocCmd.prepareScanner(cmd); // exception expected here..
	}

	@Test(expected = RenderException.class)
	public void testInvalidOutputDirArgument() throws Exception {
		String[] args = new String[] { 
				"-renderer" , "asciidoc",
				"-out",  "NOT_EXISTING_DIRECTORY",
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		Schema2DocCmd.prepareRenderer(cmd); // exception expected here..
	}
	
	@Test
	public void testValidOutputDirArgument() throws Exception {
		String[] args = new String[] { 
				"-renderer" , "asciidoc",
				"-out",  ".",
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		assertTrue(cmd.hasOption("o"));
		assertEquals(".", cmd.getOptionValue("out"));
		Schema2DocCmd.prepareRenderer(cmd); // no exception expected here..
	}
	
	
	@Test
	public void testMockPrepareRun() throws Exception {
		String[] args = new String[] { 
				"-connection" , "egal",
				"-scanner",  "Mock",
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		IScanner scanner = Schema2DocCmd.prepareScanner(cmd);
		assertNotNull(scanner);
		MockScanner mscanner = (MockScanner) scanner;	// no class cast here! 
		LOG.debug(mscanner.toString());
	}
	
	@Test
	public void testH2DbPrepareRun() throws Exception {
		String[] args = new String[] { 
				"-connection", H2ConnectTest.H2_TOTASK2_DB,
				"-driver" , H2ConnectTest.H2_DRIVER_NAME,
				"-scanner",  "GenericDb",
				"-user", "sa",
				"-verbose"
				};
		
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		IScanner scanner = Schema2DocCmd.prepareScanner(cmd);
		assertNotNull(scanner);
		GenericDbScanner h2Scanner = (GenericDbScanner) scanner;	// no class cast here! 
		LOG.debug(h2Scanner.toString());
	}	
	
	
	// ---- help -------
	
	@Test
	public void testHelp() throws Exception {
		Schema2DocCmd.main(new String[] {"-help"});
		Schema2DocCmd.printHelp();
		// no actual asserts possible
	}

	@Test
	public void testHelpContent() throws Exception {
		StringWriter sw = new StringWriter();
		Schema2DocCmd.printHelp(new PrintWriter(sw));
		String helpContent = sw.toString();
		
		assertThat(helpContent, containsString("schema2doc"));
		assertThat(helpContent, containsString("-help"));
		assertThat(helpContent, containsString("-driver"));
		assertThat(helpContent, containsString("-connection"));
	}
	
	// ---- test main -----
	
	@Test
	public void testMain() throws Exception {
		
		String[] args = new String[] { 
				"-connection", H2ConnectTest.H2_TOTASK2_DB,
				"-driver" , H2ConnectTest.H2_DRIVER_NAME,
				"-scanner",  "GenericDb",
				"-user", "sa",
				"-renderer", "asciidoc",
				"-verbose"
				};
		
		Schema2DocCmd.main(args);	
	}
	
}
