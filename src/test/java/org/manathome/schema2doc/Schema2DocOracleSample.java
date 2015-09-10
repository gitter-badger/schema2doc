package org.manathome.schema2doc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.ScannerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * test with larger oracle db.
 * 
 * 
 */
public class Schema2DocOracleSample {

	private static final Logger LOG = LoggerFactory.getLogger(Schema2DocOracleSample.class);

	private IScanner scanner;

	@Before
	public void setUp() throws Exception {
	}

	/** optional test: using oracle. */
	@Test
	public void testEoxe() throws Exception {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException cnfe) {
			LOG.info("omitting oracle test eoxe, no suitable driver.. " + cnfe.getMessage());
			return;
		}

		this.scanner = ScannerFactory.getInstance().getScanner("Oracle", "oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@ebseoxdpc01:1521:eoxe", System.getenv("EOXE_USER"), System.getenv("EOXE_PW"));

		this.scanner.setSchemaFilter(new String[] { "EOX" });

		File outFile = new File("src/docs/examples/schema2doc.eoxe.db.oracle.example.asciidoc");
		LOG.debug("writing test asciidoc to " + outFile.getAbsolutePath());

		IRenderer renderer = new AsciidocRenderer(
				new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"), true));
		Schema2Doc s2d = new Schema2Doc(scanner, renderer);
		s2d.process();

		assertTrue("asciidoc not exists", outFile.exists());
		assertTrue("asciidoc not filled", outFile.length() > 10);
	}

}
