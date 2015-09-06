package org.manathome.schema2doc.util;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/** Logging Print Writer (writes output to logging library). */
public class LogWriter extends PrintWriter {

	/** create a writer that redirects to sl4j logging (debug). */
	public LogWriter(@NotNull Logger logger) {
		super(new InternalLoggerWriter(logger), true);
	}
	
	
	static class InternalLoggerWriter extends Writer {
		 
	      private Logger logger;
	      private boolean closed;

	      public InternalLoggerWriter(final Logger logger) {
	         lock = logger;
	         //synchronize on this logger
	         this.logger = logger;
	      }

	      public void write(char[] cbuf, int off, int len) throws IOException {
	         if (closed) {
	            throw new IOException("called write on closed Writer");
	         }
	         // Remove the end of line chars
	         while (len > 0 && (cbuf[len - 1] == '\n' || cbuf[len - 1] == '\r')) {
	            len--;
	         }
	         if (len > 0) {
	            logger.debug(String.copyValueOf(cbuf, off, len));
	         }
	      }


	      public void flush()
	         throws IOException {
	         if (closed) {
	            throw new IOException("called flush on closed Writer");
	         }
	      }

	      public void close() {
	         closed = true;
	      }
	   }	

}
