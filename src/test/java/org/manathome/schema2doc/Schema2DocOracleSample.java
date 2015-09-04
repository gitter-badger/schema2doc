package org.manathome.schema2doc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.DriverManager;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** test with larger oracle db. */
public class Schema2DocOracleSample {

	
	private static final Logger LOG = LoggerFactory.getLogger(Schema2DocOracleSample.class);
	
	private IScanner scanner;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEoxe() throws Exception {

			Class.forName("oracle.jdbc.driver.OracleDriver");
			this.scanner = new GenericDbScanner(DriverManager.getConnection(
					"jdbc:oracle:thin:@ebseoxdpc01:1521:eoxe", 
					"****", 
					"**"));
			
			this.scanner.setSchemaFilter(new String[] {"EOX"});
			
			File outFile = new File("src/docs/examples/schema2doc.eoxe.db.oracle.example.asciidoc");
			LOG.debug("writing test asciidoc to " + outFile.getAbsolutePath());
			
			IRenderer renderer = new AsciidocRenderer(
					new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"), true)
					);
			Schema2Doc s2d = new Schema2Doc(scanner, renderer);
			s2d.process();
			
			assertTrue("asciidoc not exists", outFile.exists());
			assertTrue("asciidoc not filled", outFile.length() > 10);
	}

}
