package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * retrieving sample tables.
 * 
 * @author man-at-home
 */
public class MockScanner implements IScanner {
	
	public static final Set<IDbTable>  TABLES = new TreeSet<>();
	public static final Map<IDbTable,  Set<IDbColumn>> COLUMNS = new TreeMap<>();
	
	public MockScanner() {
		TABLES.add(new DbTableDefaultData("dummy1tbl" , "dummy1 table documentation"));
		TABLES.add(new DbTableDefaultData("another_table" , "another table documentation"));
		
		TABLES.forEach(table -> {
			Set<IDbColumn> tcolumns = new TreeSet<>();
			tcolumns.add(new DbColumnDefaultData("ID" , "Number", "my key", 0, 0, null));
			tcolumns.add(new DbColumnDefaultData("Name" , "Varchar2", "name of entity", 0, 0, null));
			COLUMNS.put(table, tcolumns);
		});
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IScanner#getTables()
	 */
	@Override
	public Stream<IDbTable> getTables() {
		return TABLES.stream();
	}
	
	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IScanner#getColumns(org.manathome.schema2doc.scanner.IDbTable)
	 */
	@Override
	public Stream<IDbColumn> getColumns(final IDbTable table) {
		return COLUMNS.get(table).stream();
	}
}
