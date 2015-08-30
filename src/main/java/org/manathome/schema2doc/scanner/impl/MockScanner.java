package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import javax.sql.rowset.CachedRowSet;

/**
 * retrieving sample tables.
 * 
 * @author man-at-home
 */
public class MockScanner implements IScanner {
	
	public static final Set<IDbTable>  TABLES = new TreeSet<>();
	public static final Map<IDbTable,  Set<IDbColumn>> COLUMNS = new TreeMap<>();
	
	public MockScanner() {
		DbTableDefaultData personTable   = new DbTableDefaultData(null, "mock", "person" , "a person");
		DbTableDefaultData addressTable  = new DbTableDefaultData(null, "mock", "address", "a persons address");
	
		personTable.addPrivilege(new DbPrivilegeDefaultData("me", "select"));
		personTable.addPrivilege(new DbPrivilegeDefaultData("me", "update"));
		personTable.addPrivilege(new DbPrivilegeDefaultData("me", "delete"));
		personTable.addPrivilege(new DbPrivilegeDefaultData("you", "select"));
		addressTable.addPrivilege(new DbPrivilegeDefaultData("me", "select"));
		
		TABLES.add(personTable);
		TABLES.add(addressTable);
		
		Set<IDbColumn> tcolumns = new TreeSet<>();
		DbColumnDefaultData column = new DbColumnDefaultData("ID" , "Number", "my key", 22, 0, "NO");
		column.setPrimaryKey(1);
		tcolumns.add(column);
		tcolumns.add(new DbColumnDefaultData("Name" , "Varchar2", "name of person", 80, 0, null));
		COLUMNS.put(personTable, tcolumns);

		tcolumns = new TreeSet<>();
		column = new DbColumnDefaultData("ID" , "Number", "address-id", 22, 0, "NO");
		column.setPrimaryKey(1);
		tcolumns.add(column);
		tcolumns.add(new DbColumnDefaultData("street" , "Varchar2", "street", 80, 0, null));
		column = new DbColumnDefaultData("person_id" , "Number", "address-id", 22, 0, "NO");
		column.addForeignKeyReference(
					new DbForeignKeyReference("fk_adressOfPerson", column.getName(), 1, 
					personTable.getCatalog(), personTable.getSchema(), personTable.getName(), 
					"ID"));
		tcolumns.add(column);
		COLUMNS.put(addressTable, tcolumns);		
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

	@Override
	public void setSchemaFilter(String[] argSchema) {
		// nop, ignore here		
	}

	/** not implemented, returning null. */
	@Override
	public CachedRowSet getQueryData(IDbTable table, String sqlSelect) {
		return null;
	}
}
