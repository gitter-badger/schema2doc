package org.manathome.schema2doc.renderer;

import org.manathome.schema2doc.augmenter.ITableDataAugmenter;
import org.manathome.schema2doc.augmenter.ITableDocumentationAugmenter;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.NotNull;

import java.io.PrintWriter;

/** output generator for schema documentation. */
public interface IRenderer extends AutoCloseable {
	
	/** start of document creation. */
	void beginRenderDocumentation();

	/** document table (header). */
	void beginRenderTable(@NotNull IDbTable table, ITableDocumentationAugmenter tableDocAugmenter);

	/** document table (footer). 
	 * @param tableDataAugmenter */
	void endRenderTable(@NotNull IDbTable table, ITableDataAugmenter tableDataAugmenter);

	/** document one column. */
	void renderColumn(@NotNull IDbColumn column);
	
	/** end of document creation. */
	void endRenderDocumentation();

	/** optional grouping. */
	void renderCatalog(@NotNull String catalog);

	/** optional grouping. */
	void renderSchema(@NotNull String schema);

	/** suggest a suitable filename for this type of renderer. */
	String getSuggestedFilename();

	/** current out writer to render into. */
	void setOut(@NotNull PrintWriter out);
}
