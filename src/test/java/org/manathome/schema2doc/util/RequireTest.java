package org.manathome.schema2doc.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** tests. */
public class RequireTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRequire() {
		assertEquals("not null", Require.notNull("not null"));
		assertEquals("", Require.notNull(""));
	}

	@Test(expected = NullPointerException.class)
	public void testRequireIsNull() {
		Require.notNull(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testRequireIsNullWithMsg() {
		Require.notNull(null, "require not satisfied");
	}
}
