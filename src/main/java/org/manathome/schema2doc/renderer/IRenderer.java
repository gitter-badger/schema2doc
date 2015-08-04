package org.manathome.schema2doc.renderer;

import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;

/** output documentation. */
public interface IRenderer extends AutoCloseable {

	/** document table (header). */
	void beginRenderTable(IDbTable table);

	/** document table (footer). */
	void endRenderTable(IDbTable table);

	/** document one column. */
	void renderColumn(IDbColumn column);
}
