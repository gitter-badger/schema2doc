package org.manathome.schema2doc.scanner.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.ScannerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.RowSet;


/** tests. */
public class GenericDbScannerTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericDbScannerTest.class);

	Connection conn;
	
	@Before
	public void setUp() throws Exception {
		Class.forName(H2ConnectTest.H2_DRIVER_NAME);
		conn = DriverManager.getConnection(H2ConnectTest.H2_TOTASK2_DB, "sa", "");
		assertTrue("connetion is open", !conn.isClosed());
	}
	
	@After
	public void tearDown() throws Exception {
		if (conn != null) {
			conn.close();
		}
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
	
	
	/** test parallel streams reading of database metaddata. */
	@Test
	public void testGetTablesWithParallelStream() {
		IScanner scanner = new GenericDbScanner(conn, true);
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
	

	@Test
	public void testGetColumns_TT_PROJECT() {
		IScanner scanner = new GenericDbScanner(conn);
	    scanner.getTables()
	           .filter(tbl -> tbl.getName().equalsIgnoreCase("TT_PROJECT"))
	           .forEach(tbl -> // tt_project only
	    		{
	    			scanner.getColumns(tbl).forEach(clmn ->
	    			{
	    				assertNotNull(clmn);
		    			LOG.debug("column: " + clmn + ", " + clmn.getComment());
		    			assertTrue("ID is pk", !clmn.getName().equals("ID") || clmn.isPrimaryKey());
	    			}
	    			);
	    		});
	}
	
	/** restrict output to one schema. */
	@Test
	public void testPublicSchemaFilter() {
		IScanner scanner = new GenericDbScanner(conn);
		scanner.setSchemaFilter(new String[] { "PUBLIC" });
		scanner.getTables().forEach(tbl -> LOG.debug("TABLE: " + tbl.fqnName()));
		scanner.getTables().forEach(tbl -> assertThat("only table from public", tbl.getSchema(), is("PUBLIC")));
	}

	/** PROJECT_ID is foreign key. */
	@Test
	public void testForeingKey_TT_TASK() {
		IScanner scanner = new GenericDbScanner(conn);
	    IDbTable tbl = scanner.getTables()
	           .filter(t -> t.getName().equalsIgnoreCase("TT_TASK"))
	           .findFirst()
	           .get();
	    
	    assertNotNull(tbl);
	    
	    IDbColumn col = scanner.getColumns(tbl)
	    			           .filter(clmn -> clmn.getName().equals("PROJECT_ID"))
	    		               .findFirst()
	    		               .get();
	    assertNotNull(col.getForeignKeyReferences());
	    assertEquals("1 fk in project_id column", 1, col.getForeignKeyReferences().count());
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
	
	/** test actual select queries against connection. */
	@Test
	public void testGetQueryData() throws Exception {
		IScanner scanner = new GenericDbScanner(conn);
		IDbTable table   = scanner.getTables().filter(tbl -> tbl.getName().equalsIgnoreCase("TT_PROJECT")).findFirst().get();
		
		RowSet rs = scanner.getQueryData(table, "select * from TT_PROJECT");
		conn.close();	// prove it! should be disconnected!
		conn = null;
		
		assertNotNull(rs);
		assertTrue("has columns", rs.getMetaData().getColumnCount() >= 1);
		
		int rowCounter = 0;
		
		while (rs.next()) {
		     String name = rs.getString(2);
		     long id = rs.getLong("ID");
		     rowCounter++;
		     LOG.debug("retrieved " + rowCounter + " offline row: " + name + ", " + id);
		}
		rs.beforeFirst();
		while (rs.next()) {
		     String val = rs.getString(2);
		     String columnName = rs.getMetaData().getColumnLabel(2);
		     assertNotNull("columnName", columnName);
		     long id = rs.getLong("ID");
		     LOG.debug("retrieve again offline row: " + columnName + "=" + val + ", ID=" + id);
		     rowCounter--;
		}
		rs.close();
		assertEquals("both iterations with same rowcount", 0, rowCounter);
	}
}
