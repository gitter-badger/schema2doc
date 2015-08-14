package org.manathome.schema2doc.scanner.impl;


import org.manathome.schema2doc.scanner.IDbPrivilege;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/** grant metadata. */
public class DbPrivilegeDefaultData implements IDbPrivilege {

	private Set<String> privileges 	= new TreeSet<String>(); 
	private String 		grantee 	= null;

	public DbPrivilegeDefaultData(@NotNull String grantee, @NotNull String privilege) {
		this.grantee = Require.notNull(grantee, "grantee");
		this.privileges.add(Require.notNull(privilege));
	}

	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IPrivilege#getGrantee()
	 */
	@Override
	public String getGrantee() {
		return grantee;
	}
	
	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.scanner.impl.IPrivilege#getPrivileges()
	 */
	@Override
	public Stream<String> getPrivileges() {
		return privileges.stream();
	}


}
