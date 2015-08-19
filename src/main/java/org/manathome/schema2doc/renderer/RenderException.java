package org.manathome.schema2doc.renderer;

/**
 * exceptions during render process of schema2doc.
 * @author man-at-home
 */
public class RenderException extends RuntimeException {

	private static final long serialVersionUID = 5069070453017480898L;

	/**
	 * @param msg error message
	 */
	public RenderException(String msg) {
		super(msg);
	}

	/**
	 * @param msg	error message
	 * @param ex	nested exception
	 */
	public RenderException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
