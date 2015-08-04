package org.manathome.schema2doc.renderer.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.impl.MockDbColumn;
import org.manathome.schema2doc.scanner.impl.MockDbTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/** tests. */
public class AsciidocRendererTest {
	
    private static final Logger LOG = LoggerFactory.getLogger(AsciidocRendererTest.class);


	private IRenderer renderer = null;
	private ByteArrayOutputStream out = null;
	
	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		renderer = new AsciidocRenderer(new PrintStream(out));
	}

	@Test
	public void tesCtor() {		
		assertNotNull(renderer);
	}


	@Test
	public void testRenderTable() {
		IDbTable table = new MockDbTable("dummyTableName", "dummy-comment");
		renderer.beginRenderTable(table);
		renderer.endRenderTable(table);
		String result = out.toString();
		
		assertThat(result, containsString("dummyTableName"));
		assertThat(result, containsString("dummy-comment"));
	}

	@Test
	public void testRenderColumn() {
		IDbColumn column = new MockDbColumn("dummyColumn", "dummyType", "dummy-comment");
		renderer.renderColumn(column);
		String result = out.toString();
		
		assertThat(result, containsString("dummyColumn"));
		assertThat(result, containsString("dummyType"));
		assertThat(result, containsString("dummy-comment"));
	}
	
	@Test
	public void testRenderToFile() throws Exception {
		File outFile = File.createTempFile("asciidoc.renderer.test.out-", ".adoc");
		try (AsciidocRenderer renderer = new AsciidocRenderer(new PrintStream(new FileOutputStream(outFile)))) {
			IDbTable table = new MockDbTable("dummyTableName", "dummy-comment");
			renderer.beginRenderTable(table);
			renderer.endRenderTable(table);
			
			
			assertTrue(outFile.exists());
			assertTrue(outFile.length() > 0);
		}
		LOG.debug("output asciidoc to file: " + outFile.getAbsolutePath());
		
		List<String> content = Files.readAllLines(Paths.get(outFile.getAbsolutePath()));
		assertTrue(content.size() > 0);
		content.stream().forEach(s -> LOG.debug("output: " + s));
		outFile.deleteOnExit();
	}
	
}
