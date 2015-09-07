package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.NotNull;

import java.util.List;
import java.util.stream.Stream;

/** meta data for database table. */
public interface IDbTable extends Comparable<IDbTable>{
	
	/** table name. */
	@NotNull public String getName();
	
	/** get documentation for table. */
	public String getComment();

	/** optional catalog this table belongs to. */
	public String getCatalog();

	/** optional schema this table belongs to. */
	public String getSchema();

	/** default implementation of full qualified table name, giving string in "catalog.schema.name" form. */
	@NotNull public default String fqnName() {
		return Convert.nvl2(getCatalog(), getCatalog() + ".", "")  +
		       Convert.nvl2(getSchema(), getSchema() + ".", "") +
		       getName();
	}

	/** table privileges like select and update or delete (grants). */
	public void addPrivilege(@NotNull IDbPrivilege dbPrivilege);
	
	/** list of users with grants on this table. */
	@NotNull public Stream<IDbPrivilege> getPrivileges();

	/** add a referrer this table is referencing. */
	public void addReferrer(@NotNull IReference tableReference);
	
	/** list with incoming references (other db constructs referencing this table). */
	@NotNull public Stream<IReference> getReferrer();

	public void setColumns(List<IDbColumn> columns);
	
	@NotNull public Stream<IDbColumn> getColumns();
}
