package org.manathome.schema2doc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.renderer.impl.PlaintextRenderer;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.MockScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;


/** tests. */
public class MockTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(MockTest.class);


	private IScanner scanner = null;
	private IRenderer renderer = null;
	private StringWriter output = new StringWriter();

	@Before
	public void setUp() throws Exception {
		scanner =  new MockScanner();
		renderer = new AsciidocRenderer(new PrintWriter(output));
	}

	@Test
	public void testAsciidocMockRun() throws Exception {
		Schema2Doc s2d = new Schema2Doc(scanner, renderer);
		s2d.process();
		assertEquals("2 tables", 2, s2d.getRenderedTables());
		assertTrue("output exists", output.getBuffer().toString().length() > 10);
		LOG.debug("output:" + output.getBuffer().toString());
		String s = output.getBuffer().toString();
		assertThat(s, containsString("*address*"));
		assertThat(s, containsString("*person*"));
		assertThat(s, containsString("Table"));
	}
	
	@Test
	public void testPlaintextMockRun() throws Exception {
		IRenderer plainRenderer = new PlaintextRenderer(new PrintWriter(new OutputStreamWriter(System.out, "UTF-8")));
		Schema2Doc s2d = new Schema2Doc(scanner, plainRenderer);
		s2d.process();
		assertEquals("2 tables", 2, s2d.getRenderedTables());
	}
	
	@Test
	public void testMockData() throws Exception {
		assertEquals("2 tables", 2L, scanner.getTables().count());
		assertEquals("2 tables granted to me", 2L,
				scanner.getTables()
		       .filter(tbl -> tbl.getPrivileges()
		    		             .anyMatch(priv -> priv.getGrantee().equals("me"))
		    		  )
		       .count());
		       
		assertTrue("column person_id", 
					scanner.getTables().anyMatch(tbl -> 
						scanner.getColumns(tbl).anyMatch(clmn -> clmn.getName().equals("person_id"))
					)
				  );
	}

}
