package org.manathome.schema2doc.util;

/** utility converter. */
public abstract class Convert {

	/** replace null values with defaults. */
	public static <T> T nvl(T nullableReference, @NotNull T nullReplacementValue) {
		return nullableReference == null ? nullReplacementValue : nullableReference;
	}
}
