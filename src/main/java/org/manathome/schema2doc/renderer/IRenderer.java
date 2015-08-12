package org.manathome.schema2doc.renderer;

import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.NotNull;

/** output documentation. */
public interface IRenderer extends AutoCloseable {
	
	/** start of document creation. */
	void beginRenderDocumentation();

	/** document table (header). */
	void beginRenderTable(@NotNull IDbTable table);

	/** document table (footer). */
	void endRenderTable(@NotNull IDbTable table);

	/** document one column. */
	void renderColumn(@NotNull IDbColumn column);
	
	/** end of document creation. */
	void endRenderDocumentation();

	/** optional grouping. */
	void renderCatalog(@NotNull String catalog);

	/** optional grouping. */
	void renderSchema(@NotNull String schema);
}
