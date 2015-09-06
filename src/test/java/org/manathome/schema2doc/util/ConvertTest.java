package org.manathome.schema2doc.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** tests. */
public class ConvertTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNvlNotNull() {
		assertEquals("keep x", "x", Convert.nvl("x", "y"));
		assertEquals("keep x", "yx", Convert.nvl("yx", ""));
		assertEquals("keep empty", "", Convert.nvl("", "y"));
	}

	@Test
	public void testNvlNull() {
		assertEquals("use x", "x", Convert.nvl(null, "x"));
		assertEquals("use empty", "", Convert.nvl(null, ""));
		assertEquals("use another one", "another one", Convert.nvl(null, "another one"));
		assertEquals("null", null, (String) Convert.nvl(null, null));
	}
}
