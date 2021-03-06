package org.manathome.schema2doc.renderer.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbProcedure;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.impl.DbColumnDefaultData;
import org.manathome.schema2doc.scanner.impl.DbProcedureDefaultData;
import org.manathome.schema2doc.scanner.impl.DbTableDefaultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/** tests. */
public class AsciidocRendererTest {
	
    private static final Logger LOG = LoggerFactory.getLogger(AsciidocRendererTest.class);

	private IRenderer renderer = null;
	private ByteArrayOutputStream bout = null;
	
	private IDbTable  table   = null;
	private IDbColumn column  = null;
	
	@Before
	public void setUp() throws Exception {
		bout = new ByteArrayOutputStream();
		renderer = new AsciidocRenderer(new PrintWriter(new OutputStreamWriter(bout, "UTF-8"), true));
		
		table = new DbTableDefaultData(null, null, "dummyTableName", "dummy-comment");
		column = new DbColumnDefaultData("dummyColumn", "dummyType", "dummy-comment", 0, 0, null);

		List<IDbColumn> cl = new ArrayList<>();
		cl.add(column);
		table.setColumns(cl);
	}

	@Test
	public void tesCtor() {		
		assertNotNull(renderer);
	}


	@Test
	public void testRenderTable() throws Exception {
		renderer.beginRenderTable(table, null);
		renderer.endRenderTable(table, null);
		String result = bout.toString("UTF-8");
		
		assertThat(result, containsString("dummyTableName"));
		assertThat(result, containsString("dummy-comment"));
	}
	
	@Test 
	public void testRenderCatalog() throws Exception {
		renderer.renderCatalog("Sample Catalog");
		String result = bout.toString("UTF-8");
		assertThat(result, containsString("Sample Catalog"));
	}
	
	@Test 
	public void testRenderSchema() throws Exception {
		renderer.renderSchema("SampleSchema");
		String result = bout.toString("UTF-8");
		assertThat(result, containsString("SampleSchema"));
	}

	@Test
	public void testRenderColumn() throws Exception {
		renderer.renderColumn(column);
		String result = bout.toString("UTF-8");
		
		assertThat(result, containsString("dummyColumn"));
		assertThat(result, containsString("dummyType"));
		assertThat(result, containsString("dummy-comment"));
	}
	
	@Test
	public void testRenderToFile() throws Exception {
		File outFile = File.createTempFile("asciidoc.renderer.test.out-", ".adoc");
		try (AsciidocRenderer renderer = new AsciidocRenderer(new PrintWriter(new OutputStreamWriter(
					    new FileOutputStream(outFile), "UTF-8"), true))) {
						
			renderer.beginRenderTable(table, null);
			renderer.endRenderTable(table, null);
			
			
			assertTrue(outFile.exists());
			assertTrue(outFile.length() > 0);
		}
		LOG.debug("output asciidoc to file: " + outFile.getAbsolutePath());
		
		List<String> content = Files.readAllLines(Paths.get(outFile.getAbsolutePath()));
		assertTrue(content.size() > 0);
		content.stream().forEach(s -> LOG.debug("output: " + s));
		outFile.deleteOnExit();
	}
	
	@Test
	public  void testRenderProcedure() throws Exception {
		IDbProcedure proc = new DbProcedureDefaultData(null, "dummysch", "procname", "no comment");
		
		renderer.beginRenderCode();
		renderer.renderProcedure(proc);
		renderer.endRenderCode();
	
		String result = bout.toString("UTF-8");
		
		assertThat(result, containsString("procname"));
		assertThat(result, containsString("no comment"));
		assertThat(result, containsString("|==="));
	}

}
