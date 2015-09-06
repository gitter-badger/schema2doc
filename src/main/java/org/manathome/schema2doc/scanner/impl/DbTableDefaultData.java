package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.IDbPrivilege;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IReference;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/** test data. */
public class DbTableDefaultData implements IDbTable {


	private String catalog;
	private String schema;
	private String name;
	private String comment;
	private List<IDbPrivilege> privileges = new ArrayList<>();
	private Set<IReference>    referrer   = new TreeSet<IReference>();

	public DbTableDefaultData(final String catalog, final String schema, @NotNull final String name, final String comment) {
		this.catalog = catalog;
		this.schema = schema;
		this.name = Require.notNull(name, "tableName");
		this.comment = comment;
	}

	@Override
	public String getName() {
		return name;
	}

	/** table comment. */
	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public int compareTo(IDbTable otherTable) {
		return this.getName().compareTo(otherTable.getName());
	}

	@Override
	public String getCatalog() {
		return this.catalog;
	}

	@Override
	public String getSchema() {
		return this.schema;
	}

	@Override
	public void addPrivilege(IDbPrivilege dbPrivilege) {
		privileges.add(dbPrivilege);
	}

	@Override
	public Stream<IDbPrivilege> getPrivileges() {
		return privileges.stream();
	}

	
	@Override
	public String toString() {
		return "Table[" + name + "]";
	}

	@Override
	public void addReferrer(@NotNull IReference tableReference) {
		referrer.add(Require.notNull(tableReference));		
	}

	@Override
	@NotNull public Stream<IReference> getReferrer() {
		return referrer.stream();
	}	
}
