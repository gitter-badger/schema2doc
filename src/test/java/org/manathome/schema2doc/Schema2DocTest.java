package org.manathome.schema2doc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.manathome.schema2doc.scanner.impl.H2ConnectTest;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;


/** test whole run. */
public class Schema2DocTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(Schema2DocTest.class);
	
	private IScanner scanner;

	@Before
	public void setUp() throws Exception {
		Class.forName(H2ConnectTest.H2_DRIVER_NAME);
		this.scanner = new GenericDbScanner(DriverManager.getConnection(H2ConnectTest.H2_TOTASK2_DB, "sa", ""));
	}

	/** writes asciidoc to temp file. */
	@Test
	public void testH2Process() throws Exception {
		File outFile = Files.createTempFile("schema2doc.junit.testdoc.", ".adoc").toFile();
		
		LOG.debug("writing test asciidoc to " + outFile.getAbsolutePath());
		
		IRenderer renderer = new AsciidocRenderer(new PrintWriter(new OutputStreamWriter(
			    new FileOutputStream(outFile), "UTF-8"), true));
		Schema2Doc s2d = new Schema2Doc(scanner, renderer);
		s2d.process();
		
		assertTrue("asciidoc not exists", outFile.exists());
		assertTrue("asciidoc not filled", outFile.length() > 10);
		
		outFile.deleteOnExit();
	}

	/** writes asciidoc example to schema2doc.example.h2.asciidoc file. */
	@Test
	public void testH2SampleAsciidocProcess() throws Exception {

		File outFile = new File("src/docs/examples/schema2doc.totask2.qa.db.h2.example.asciidoc");
		LOG.debug("writing test asciidoc to " + outFile.getAbsolutePath());
		
		IRenderer renderer = new AsciidocRenderer(new PrintWriter(new OutputStreamWriter(System.out, "UTF-8")));
		Schema2Doc s2d = new Schema2Doc(scanner, renderer);
		s2d.process();
		
		assertTrue("asciidoc not exists", outFile.exists());
		assertTrue("asciidoc not filled", outFile.length() > 10);
	}
	
	
	/** writes asciidoc to temp file. */
	@Test
	public void testMockProcess() throws Exception {
		Path currentPath = Paths.get("");
		File outFile = Files.createTempFile(currentPath, "junit.schema2doctest.", ".adoc").toFile();
		
		LOG.debug("writing mock data asciidoc to " + outFile.getAbsolutePath());
		
		IRenderer asciiDocRenderer = new AsciidocRenderer(
				new PrintWriter(new OutputStreamWriter(
					    new FileOutputStream(outFile), "UTF-8"), true));
		Schema2Doc s2d = new Schema2Doc(new MockScanner(), asciiDocRenderer);
		s2d.process();
		
		assertTrue("asciidoc not exists", outFile.exists());
		assertTrue("asciidoc not filled", outFile.length() > 10);
		
		outFile.deleteOnExit();
	}

}
