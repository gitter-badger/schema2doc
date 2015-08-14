package org.manathome.schema2doc.scanner;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/** a user or a role granted with access rights (privileges) like select or update, delete etc. */
public interface IDbPrivilege {

	/** user or group the privilege is granted to. */
	String getGrantee();

	/** privilege like 'select', 'update', 'delete'.
	 */
	Stream<String> getPrivileges();

	/** simple printable output. */
	default String display() {
		return getGrantee() + " (" + getPrivileges().collect(Collectors.joining(", ")) + ")";
	}

}
