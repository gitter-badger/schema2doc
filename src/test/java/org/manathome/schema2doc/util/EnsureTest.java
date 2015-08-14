package org.manathome.schema2doc.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** tests. */
public class EnsureTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRequire() {
		assertEquals("not null", Ensure.notNull("not null"));
		assertEquals("", Ensure.notNull(""));
		assertEquals("x", Ensure.notNull("x", "x is set"));
	}

	@Test(expected = NullPointerException.class)
	public void testRequireIsNull() {
		Ensure.notNull(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testRequireIsNullWithMsg() {
		Ensure.notNull(null, "s not satisfied");
	}
}
