package org.manathome.schema2doc.scanner.impl;

import java.sql.Connection;


/**
 * retrieving relevant oracle schema objects (tables, columns..) from a oracle database.
 *
 * no practical implementation for now..
 * 
 * @author man-at-home
 *
 */
public class OracleScanner extends GenericDbScanner {
	
	public OracleScanner(Connection con) {
		super(con, true);
	}
	
}
