package org.manathome.schema2doc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.apache.commons.cli.CommandLine;
import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.manathome.schema2doc.scanner.impl.H2ConnectTest;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

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
				};
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		LOG.debug("cmd=" + cmd.toString());
		assertNotNull(cmd);
		assertTrue("connection arg found", cmd.hasOption("connection"));
		assertTrue("scanner arg found", cmd.hasOption("scanner"));
		assertEquals("scanner is Mock", "Mock", cmd.getOptionValue("scanner"));
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
		LOG.debug("cmd=" + cmd.toString());
		assertNotNull(cmd);
		assertTrue("connection arg found", cmd.hasOption("connection"));
		assertTrue("scanner arg found", cmd.hasOption("scanner"));
		assertTrue("user arg found", cmd.hasOption("user"));
		assertEquals("user name expected", "unusedUser" , cmd.getOptionValue("user"));
		assertTrue("pw arg found", cmd.hasOption("password"));
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
				"-user", "sa"
				};
		
		CommandLine cmd = Schema2DocCmd.parseArguments(args);
		IScanner scanner = Schema2DocCmd.prepareScanner(cmd);
		assertNotNull(scanner);
		GenericDbScanner h2Scanner = (GenericDbScanner) scanner;	// no class cast here! 
		LOG.debug(h2Scanner.toString());
	}	
	
	
	@Test
	public void testHelp() throws Exception {
		Schema2DocCmd.main(new String[] {"-help"});
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

}
