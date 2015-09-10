package org.manathome.schema2doc.scanner.impl;

import javax.sql.DataSource;


/**
 * retrieving relevant oracle schema objects (tables, columns..) from a oracle database.
 *
 * no practical implementation for now..
 * 
 * @author man-at-home
 *
 */
public class OracleScanner extends GenericDbScanner {
	
	public OracleScanner(DataSource ds) {
		super(ds);
		this.setUseParallelStream(true);
	}
	
}
