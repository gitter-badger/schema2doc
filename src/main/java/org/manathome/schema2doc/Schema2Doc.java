package org.manathome.schema2doc;

import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/** entry point to the schema2doc tool.
 *  Binds scanner and renderer portion together. 
 *  
 *  @see Schema2DocCmd for command line use
 */
public class Schema2Doc {

	private static final Logger LOG = LoggerFactory.getLogger(Schema2Doc.class);

	private IScanner 	scanner;
	private IRenderer 	renderer;
	private int 		tableCnt = 0;
	private boolean 	isGroupedByCatalogAndSchema = true;

	public Schema2Doc(@NotNull final IScanner scanner, @NotNull final IRenderer renderer) {
		this.scanner 	= Require.notNull(scanner,  "scanner not set");
		this.renderer 	= Require.notNull(renderer, "renderer not set");
	}

	/** holds number of processed tables after processing. */
	public int getRenderedTables() {
		return this.tableCnt;
	}

	/** pump through scanner -> renderer pipeline. */
	public void process() throws Exception {
		LOG.info("running Schema2Doc ..");

		Require.notNull(renderer, "renderer not set, did you try to reuse this instance?");
		Require.notNull(scanner,  "scanner not set, did you try to reuse this instance?");
		
		renderer.beginRenderDocumentation();

		List<IDbTable> tables = null;
		if (isGroupedByCatalogAndSchema()) {
			tables = scanner.getTables().sorted((tbl1, tbl2) -> tbl1.fqnName().compareTo(tbl2.fqnName()))
					.collect(Collectors.toList());
		} else {
			tables = scanner.getTables().collect(Collectors.toList());
		}

		String currentCatalog = null;
		String currentSchema = null;

		for (IDbTable table : tables) {
			if (isGroupedByCatalogAndSchema() && currentCatalog != table.getCatalog()) {
				currentCatalog = table.getCatalog();
				currentSchema = null;
				renderer.renderCatalog(currentCatalog);
			}
			if (isGroupedByCatalogAndSchema() && currentSchema != table.getSchema()) {
				currentSchema = table.getSchema();
				renderer.renderSchema(currentSchema);
			}
			renderer.beginRenderTable(table);
			tableCnt++;
			scanner.getColumns(table).forEach(column -> renderer.renderColumn(column));
			renderer.endRenderTable(table);
		}
		renderer.endRenderDocumentation();

		renderer.close();
		
		this.renderer = null;
		this.scanner = null;

		LOG.info("running Schema2Doc done.");
	}

	/** should the documentation separate catalogs and schemas. */
	public boolean isGroupedByCatalogAndSchema() {
		return isGroupedByCatalogAndSchema;
	}

	/** should the documentation separate catalogs and schemas. */
	public void setGroupedByCatalogAndSchema(boolean isGroupedByCatalogAndSchema) {
		this.isGroupedByCatalogAndSchema = isGroupedByCatalogAndSchema;
	}
}
