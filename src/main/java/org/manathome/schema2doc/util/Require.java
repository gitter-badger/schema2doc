/**
 * 
 */
package org.manathome.schema2doc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * precondition assertion.
 * 
 * @author man-at-home.
 */
public abstract class Require {

	private static final Logger LOG = LoggerFactory.getLogger(Require.class);

	/**
	 * argument notNull checking.
	 * 
	 * @see #notNull(Object, String)
	 */ 
	public static <T> T notNull(T reference) {
		return notNull(reference, "required argument is null");
	}

	/**
	 * argument notNull checking.
	 * 
	 * @param expectedNotNullReference
	 *            the reference to be checked here
	 * @param msg
	 *            to be thrown as NullPointerException
	 * @return expectedNotNullReference, garantied to be not null
	 * @throws NullPointerException
	 */
	public static <T> T notNull(T expectedNotNullReference, @NotNull String msg) {
		if (expectedNotNullReference == null) {
			LOG.error("notNull(null) not satisfied ->" + msg);
			throw new NullPointerException(msg);
		}
		return expectedNotNullReference;
	}
}
