package org.manathome.schema2doc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** check post conditions. */
public abstract class Ensure {

	private static final Logger LOG = LoggerFactory.getLogger(Ensure.class);

	/**
	 * requirement notNull checking.
	 * 
	 * @see #notNull(Object, String)
	 */
	public static <T> T notNull(T reference) {
		return notNull(reference, "requirenment not met (null)");
	}

	/**
	 * requirement notNull checking.
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
			LOG.error("notNull not ensured ->" + msg);
			throw new NullPointerException(msg);
		}
		return expectedNotNullReference;
	}

}
