package org.manathome.schema2doc.util;

/** utility converter. */
public abstract class Convert {

	/** replace null values with defaults. */
	public static <T> T nvl(T nullableReference, @NotNull T nullReplacementValue) {
		return nullableReference == null ? nullReplacementValue : nullableReference;
	}

	/** nvl2. */
	public static <T> T nvl2(T nullableReference, T returnIfNotNull, T returnIfNull) {
		return nullableReference == null ? returnIfNull : returnIfNotNull;
	}
}
