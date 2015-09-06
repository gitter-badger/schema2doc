package org.manathome.schema2doc.scanner.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.ScannerException;
import org.manathome.schema2doc.scanner.ScannerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import javax.sql.RowSet;


/** tests. */
public class GenericDbScannerTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(GenericDbScannerTest.class);

	GenericDbScanner scanner;
	
	@Before
	public void setUp() throws Exception {
		
		this.scanner  =  (GenericDbScanner) ScannerFactory
				.getInstance()
				.getScanner("Generic", H2ConnectTest.H2_DRIVER_NAME, H2ConnectTest.H2_TOTASK2_DB, "sa", "", true);
	}
	
	@After
	public void tearDown() throws Exception {
		if (scanner != null) {
			// scanner.close();
		}
	}

	@Test
	public void testGenericDbScanner() {
		assertNotNull(scanner);
	}

	@Test
	public void testGetTables() {
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
		
		GenericDbScanner parallelScanner  = (GenericDbScanner)  ScannerFactory
				.getInstance()
				.getScanner("Generic", H2ConnectTest.H2_DRIVER_NAME, H2ConnectTest.H2_TOTASK2_DB, "sa", "");
		
		parallelScanner.setUseParallelStream(true);
		parallelScanner.getTables().forEach(tbl ->
	    		{
	    			LOG.debug("table: " + tbl.getName() + ", " + tbl.getComment());
	    			assertNotNull(tbl.getName());
	    		});
	}
	
	
	/** test parallel streams.  */
	@Test
	public void testParallelStreamsDemoOnly() throws Exception {
				
		LOG.debug("test thread pool");
		
		// parallel (6 threads?) processing
		ForkJoinPool forkJoinPool = new ForkJoinPool(6);
		
		LOG.debug("forkJoinPool parallel: " + forkJoinPool.getParallelism() + ", pool: " + 
				  forkJoinPool.getPoolSize() + ", acive thread: " + 
				  forkJoinPool.getActiveThreadCount());

		
		IntStream stream = forkJoinPool.submit(() ->
			
					  IntStream.range(1, 100)
					 .parallel()
					 .filter(i -> i <= 30)
					 .sorted()
					 .map(i -> { 
						 LOG.debug("map i " + i); 
						 try {
							 Thread.sleep(15); 
						 } catch (Exception ex) { }
						 return i; })

				).get();
		
		LOG.debug("forkJoinPool parallel: " + forkJoinPool.getParallelism() + ", pool: " + 
											  forkJoinPool.getPoolSize() + ", acive thread: " + 
											  forkJoinPool.getActiveThreadCount());

		int sum = stream.sum();
		
		LOG.debug("forkJoinPool parallel: " + forkJoinPool.getParallelism() + ", pool: " + 
				  forkJoinPool.getPoolSize() + ", acive thread: " + 
				  forkJoinPool.getActiveThreadCount());
		
		LOG.debug("result=" + sum);
		assertEquals("result sum", 465 , sum);
	}		

	@Test
	public void testGetColumns() {
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
		scanner.setSchemaFilter(new String[] { "PUBLIC" });
		scanner.getTables().forEach(tbl -> LOG.debug("TABLE: " + tbl.fqnName()));
		scanner.getTables().forEach(tbl -> assertThat("only table from public", tbl.getSchema(), is("PUBLIC")));
	}

	/** PROJECT_ID is foreign key. */
	@Test
	public void testForeingKey_TT_TASK() {
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
	
	@Ignore
	@Test(expected = ScannerException.class) 
	public void testGetTablesOnClosedConnection() throws Exception {
		scanner.close();
	    assertTrue(scanner.getTables().count() == 0);
	}
	

	@Ignore
	@Test(expected = ScannerException.class) 
	public void testGetColumnsOnClosedConnection() throws Exception {
		IDbTable table   = scanner.getTables().findFirst().get();
		scanner.close();
	    assertTrue(scanner.getColumns(table).count() == 0);
	}
	
	/** test actual select queries against connection. */
	@Test
	public void testGetQueryData() throws Exception {
		IDbTable table   = scanner.getTables().filter(tbl -> tbl.getName().equalsIgnoreCase("TT_PROJECT")).findFirst().get();
		
		RowSet rs = scanner.getQueryData(table, "select * from TT_PROJECT");
		
		scanner.close();
		scanner = null;
		
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
