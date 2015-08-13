package org.manathome.schema2doc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.MockScanner;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;


/** tests. */
public class MockTest {

	private IScanner scanner = null;
	private IRenderer renderer = null;

	@Before
	public void setUp() throws Exception {
		scanner =  new MockScanner();
		renderer = new AsciidocRenderer(new PrintWriter(new OutputStreamWriter(System.out, "UTF-8")));
	}

	@Test
	public void testMockRun() throws Exception {
		Schema2Doc s2d = new Schema2Doc(scanner, renderer);
		s2d.process();
		assertEquals("2 tables", 2, s2d.getRenderedTables());
	}

}
