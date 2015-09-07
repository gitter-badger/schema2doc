package org.manathome.schema2doc;

import org.manathome.schema2doc.augmenter.IAugmenterConfiguration;
import org.manathome.schema2doc.augmenter.ITableDataAugmenter;
import org.manathome.schema2doc.augmenter.impl.DocumentHeaderAugmenter;
import org.manathome.schema2doc.augmenter.impl.TableDataAugmenter;
import org.manathome.schema2doc.augmenter.impl.TableDocumentationAugmenter;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/** entry point to the schema2doc tool.
 *  Binds scanner and renderer portion together. 
 *  
 *  @see Schema2DocCmd for command line use
 */
public class Schema2Doc implements IAugmenterConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(Schema2Doc.class);

	private IScanner 	scanner;
	private IRenderer 	renderer;
	private int 		tableCnt = 0;
	private boolean 	isGroupedByCatalogAndSchema = true;

	private String 		configPath = "config/schema2doc";

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
		
		DocumentHeaderAugmenter docAugmenter = new DocumentHeaderAugmenter();
		docAugmenter.loadConfiguration(this);
		renderer.beginRenderDocumentation(docAugmenter);

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
			
			table.setColumns(scanner.getColumns(table));
			
			if (isGroupedByCatalogAndSchema() && currentCatalog != table.getCatalog()) {
				currentCatalog = table.getCatalog();
				currentSchema = null;
				renderer.renderCatalog(currentCatalog);
			}
			if (isGroupedByCatalogAndSchema() && currentSchema != table.getSchema()) {
				currentSchema = table.getSchema();
				renderer.renderSchema(currentSchema);
			}
			
			TableDocumentationAugmenter tableDocAugmenter = new TableDocumentationAugmenter();
			tableDocAugmenter.loadConfiguration(this, table);
			
			renderer.beginRenderTable(table, tableDocAugmenter);
			tableCnt++;
			table.getColumns().forEach(column -> renderer.renderColumn(column));
			
			ITableDataAugmenter tableDataAugmenter = new TableDataAugmenter();
			tableDataAugmenter.loadConfiguration(this, table, scanner);
			
			renderer.endRenderTable(table, tableDataAugmenter);
		}
		renderer.endRenderDocumentation();

		renderer.close();
		scanner.close();
		
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
	
	/** return configuration file, null if not existing. */
	@Override
	public File getConfigFile(String catalog, String schema, String table, String fileName) {

		Path path = Paths.get(getConfigPath());
		if (Files.exists(path) && Files.isDirectory(path)) {
			LOG.debug("using configuration path " + path.toAbsolutePath());
			
			if (catalog != null) {
				path = path.resolve(catalog);
				if (Files.exists(path) && Files.isDirectory(path) && schema != null) {
					path = path.resolve(schema);
				}
			}
			
			path = path.resolve(Require.notNull(fileName, "fileName"));			
			LOG.debug("search for config file: " + path.toString() + " exists: " + Files.exists(path));
			
			if (Files.exists(path) && !Files.isDirectory(path)) {
				return path.toFile();
			}
		}
		return null;		
	}

	/** return configuration file, null if not existing. */
	@Override
	public File getConfigFile(IDbTable table, String fileName) {
		return getConfigFile(table.getCatalog(), table.getSchema(), table.getName(), fileName);
	}

	/** path to augmenting data. */
	private String getConfigPath() {
		return this.configPath;
	}

	/** path to augmenting data. */
	public void setConfigPath(@NotNull String configPath) {
		this.configPath = configPath;	
	}
}
