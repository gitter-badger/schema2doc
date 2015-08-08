package org.manathome.schema2doc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

/** tests. */
public class Schema2DocCmdTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSampleRun() throws Exception {
		Schema2DocCmd.main(null);
	}
	
	@Test
	public void testHelp() throws Exception {
		Schema2DocCmd.main(new String[] {"-help"});
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
