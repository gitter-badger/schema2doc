package org.manathome.schema2doc.scanner.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/** tests. */
public class MockScannerTest {
	
	private MockScanner scanner;
	
	@Before
	public void setUp() throws Exception {
		scanner = new MockScanner();
	}

	@Test
	public void testGetTables() {
		assertEquals("2 tables expected", 2, scanner.getTables().count());
	}

	@Test
	public void testGetColumns() {
		assertEquals("2 columns expected", 2, scanner.getColumns(scanner.getTables().findFirst().get()).count());
	}

}
