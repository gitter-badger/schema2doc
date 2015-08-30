package org.manathome.schema2doc.augmenter;

/** error during augmentation. */
public class AugmenterException extends RuntimeException {

	private static final long serialVersionUID = -1215735516762717018L;

	public AugmenterException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
