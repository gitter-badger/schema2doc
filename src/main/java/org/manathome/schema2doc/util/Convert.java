package org.manathome.schema2doc.util;

/** 
 * converter utility. 
 * 
 * @author man-at-home
 * */
public abstract class Convert {

	/** 
	 * replace null values with defaults. 
	 * 
	 * @return nullableReference == null ? nullReplacementValue : nullableReference;
	 * */
	public static <T> T nvl(T nullableReference, @NotNull T nullReplacementValue) {
		return nullableReference == null ? nullReplacementValue : nullableReference;
	}

	/** nvl2, change values on null/not null conditions. 
	 * 
	 * @param  nullableReference object that will be tested for null. 
	 * @return nullableReference == null ? returnIfNull : returnIfNotNull
	 * */
	public static <T> T nvl2(T nullableReference, T returnIfNotNull, T returnIfNull) {
		return nullableReference == null ? returnIfNull : returnIfNotNull;
	}
	
	/** 
	 * return left portion of the string s. 
	 * very lenient, no exceptions!
	 *  
	 * @return left part of s or null, never more than maxLength letters. 
	 * */
	public static String left(final String s, int maxLength) {
		if (s == null) {
			return null;
		} else {
			return s.substring(0, Math.min(maxLength, s.length()));
		}
	}
}
