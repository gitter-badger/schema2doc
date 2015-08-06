package org.manathome.schema2doc.scanner.impl;

import static org.junit.Assert.*;

import org.h2.tools.DeleteDbFiles;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** test connect with h2 database. */
public class H2ConnectTest {

	@Test
	public void testOpenConnection() throws Exception {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:./totask2.qa.db", "sa", "");
		assertTrue("connetion is open", !conn.isClosed());
		conn.close();
		assertTrue("connetion is closed", conn.isClosed());
	}

	@Test
	public void testSqlStatements() throws Exception {

		String dbName = "schema2doc.h2connecttest.h2.db";

		DeleteDbFiles.execute("~", dbName, true);

		Class.forName("org.h2.Driver");
		try (Connection conn = DriverManager.getConnection("jdbc:h2:~/" + dbName)) {

			try (Statement stmt = conn.createStatement()) {

				stmt.execute("create table schema2doc_test(id int primary key, name varchar(10))");  // ddl
				stmt.execute("insert into schema2doc_test values(1, 'X333')");						 // dml
				ResultSet rs;
				rs = stmt.executeQuery("select * from schema2doc_test");
				while (rs.next()) {
					assertEquals("wrong data retrieved", "X333", rs.getString("name"));
				}
				stmt.close();
			}
			conn.close();
		}
	}

}
