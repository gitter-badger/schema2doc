package org.manathome.schema2doc.scanner.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;

/** tests. */
public class MockScannerTest {
	
	private IScanner scanner;
	
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
	    IDbTable personTable = scanner.getTables().filter(tbl -> tbl.getName().equalsIgnoreCase("person")).findFirst().get();
		assertEquals("2 columns in person table expected", 
				2, 
				scanner.getColumns(personTable).stream().count()
						);
	}

}
