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
import java.io.PrintStream;

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
		renderer = new LoggingAdapterRenderer(new PlaintextRenderer(new PrintStream(out)));
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
	public void testRenderTable() {
		IDbTable table = new DbTableDefaultData("dummyTableName", "dummy-comment");
		renderer.beginRenderTable(table);
		renderer.endRenderTable(table);
		String result = out.toString();
		
		assertThat(result, containsString("dummyTableName"));
		assertThat(result, containsString("dummy-comment"));
		assertThat(result, containsString("-------------"));
	}

	@Test
	public void testRenderColumn() {
		IDbColumn column = new DbColumnDefaultData("dummyColumn", "dummyType", "dummy-comment", 0, 0, null);
		renderer.renderColumn(column);
		String result = out.toString();
		
		assertThat(result, containsString("dummyColumn"));
		assertThat(result, containsString("dummyType"));
		assertThat(result, containsString("dummy-comment"));
	}
	
}
