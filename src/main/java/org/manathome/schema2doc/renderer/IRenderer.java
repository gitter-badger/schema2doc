package org.manathome.schema2doc.renderer;

import org.manathome.schema2doc.augmenter.IDocumentationAugmenter;
import org.manathome.schema2doc.augmenter.ITableDataAugmenter;
import org.manathome.schema2doc.scanner.IDbColumn;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.NotNull;

import java.io.PrintWriter;

/** output generator for schema documentation. 
 * 
 * @author man-from-home
 */
public interface IRenderer extends AutoCloseable {
	
	/** 
	 * start of document creation. 
	 * 
	 * @param docAugmenter 			optional additional documentation header text, user provided.
	 * */
	void beginRenderDocumentation(IDocumentationAugmenter docAugmenter);

	/** 
	 * document table (header). 
	 * 
	 * @param table					current table
	 * @param tableDocAugmenter		optional additional table description, user provided.
	 * */
	void beginRenderTable(@NotNull IDbTable table, IDocumentationAugmenter tableDocAugmenter);

	/** 
	 * document table (footer).
	 *  
	 * @param table                 current table
	 * @param tableDataAugmenter 	optional additional table data (content) to add.
	 */
	void endRenderTable(@NotNull IDbTable table, ITableDataAugmenter tableDataAugmenter);

	/** document one table column. */
	void renderColumn(@NotNull IDbColumn column);
	
	/** end of document creation. */
	void endRenderDocumentation();

	/** 
	 * optional grouping by catalog
	 * 
	 *  only called if grouping by catalog is required.
	 */
	void renderCatalog(@NotNull String catalog);

	/** 
	 * optional grouping by schema (withing catalog)
	 * 
	 *  only called if grouping by shema is required.
	 * */
	void renderSchema(@NotNull String schema);

	/** 
	 * suggest a suitable filename for this type of renderer. 
	 * 
	 * @return a filenae suitable for this renderer.
	 */
	String getSuggestedFilename();

	/** current out writer to render into. */
	void setOut(@NotNull PrintWriter out);
}
