package org.manathome.schema2doc.renderer.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.impl.DbColumnDefaultData;
import org.manathome.schema2doc.scanner.impl.DbTableDefaultData;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * tests.
 * @author man-at-home
 *
 */
public class PlaintextRendererTest {
	
	private IRenderer renderer = null;
	private ByteArrayOutputStream out = null;
	
	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		renderer = new LoggingAdapterRenderer(
				new PlaintextRenderer(new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true)),
				true);
	}

	@Test
	public void tesCtor() {		
		assertNotNull(renderer);
	}

	@Test(expected = NullPointerException.class)
	public void tesCtorNullArgument() throws Exception {
		try (PlaintextRenderer nrenderer = new PlaintextRenderer(null)) {
			fail("exception expected, not " + nrenderer);
		}
	}

	@Test
	public void testRenderTable() throws Exception {
		IDbTable table = new DbTableDefaultData(null, "dummy", "dummyTableName", "dummy-comment");
		renderer.beginRenderTable(table, null);
		renderer.endRenderTable(table, null);
		String result = out.toString("UTF-8");
		
		assertThat(result, containsString("dummyTableName"));
		assertThat(result, containsString("dummy-comment"));
		assertThat(result, containsString("-------------"));
	}

	@Test
	public void testRenderColumn() throws Exception {
		IDbColumn column = new DbColumnDefaultData("dummyColumn", "dummyType", "dummy-comment", 0, 0, null);
		renderer.renderColumn(column);
		String result = out.toString("UTF-8");
		
		assertThat(result, containsString("dummyColumn"));
		assertThat(result, containsString("dummyType"));
		assertThat(result, containsString("dummy-comment"));
	}
	
	@Test 
	public void testRenderCatalog() throws Exception {
		renderer.renderCatalog("Sample Catalog");
		String result = out.toString("UTF-8");
		assertThat(result, containsString("Sample Catalog"));
	}
	
	@Test 
	public void testRenderSchema() throws Exception {
		renderer.renderSchema("SampleSchema");
		String result = out.toString("UTF-8");
		assertThat(result, containsString("SampleSchema"));
	}

	@Test 
	public void testRenderDocument() throws Exception {
		renderer.beginRenderDocumentation(null);
		renderer.endRenderDocumentation();
		String result = out.toString("UTF-8");
		assertThat(result, containsString("schema2doc plaintext documentation"));
		assertThat(result, containsString("created"));
	}

	@Test 
	public void testClose() throws Exception {
		renderer.close();
	}	
}
