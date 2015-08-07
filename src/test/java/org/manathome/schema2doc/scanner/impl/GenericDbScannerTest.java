package org.manathome.schema2doc.scanner.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.ScannerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;


/** tests. */
public class GenericDbScannerTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericDbScannerTest.class);

	Connection conn;
	
	@Before
	public void setUp() throws Exception {
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:./totask2.qa.db", "sa", "");
		assertTrue("connetion is open", !conn.isClosed());
	}
	
	@After
	public void tearDown() throws Exception {
		conn.close();
	}

	@Test
	public void testGenericDbScanner() {
		IScanner scanner = new GenericDbScanner(conn);
		assertNotNull(scanner);
	}

	@Test
	public void testGetTables() {
		IScanner scanner = new GenericDbScanner(conn);
	    assertTrue(scanner.getTables().count() > 0);
	    scanner.getTables().forEach(tbl ->
	    		{
	    			LOG.debug("table: " + tbl.getName() + ", " + tbl.getComment());
	    			assertNotNull(tbl.getName());
	    		});
	}

	@Test
	public void testGetColumns() {
		IScanner scanner = new GenericDbScanner(conn);
	    assertTrue(scanner.getTables().count() > 0);
	    scanner.getTables().forEach(tbl ->
	    		{
	    			assertTrue("has columns", scanner.getColumns(tbl).count() > 0);
	    			scanner.getColumns(tbl).forEach(clmn ->
	    			{
	    				assertNotNull(clmn);
		    			LOG.debug("column: " + clmn + ", " + clmn.getComment());
		    			assertNotNull("column name required", clmn.getName());
		    			assertNotNull("column type required", clmn.getTypename());
	    			}
	    			);
	    		});
	}

	
	@Test(expected = ScannerException.class) 
	public void testGetTablesOnClosedConnection() throws Exception {
		IScanner scanner = new GenericDbScanner(conn);
		conn.close();
	    assertTrue(scanner.getTables().count() == 0);
	}
	

	@Test(expected = ScannerException.class) 
	public void testGetColumnsOnClosedConnection() throws Exception {
		IScanner scanner = new GenericDbScanner(conn);
		IDbTable table   = scanner.getTables().findFirst().get();
		conn.close();
	    assertTrue(scanner.getColumns(table).count() == 0);
	}
}
