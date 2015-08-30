package org.manathome.schema2doc.augmenter.impl;


import org.manathome.schema2doc.augmenter.IAugmenterConfiguration;
import org.manathome.schema2doc.augmenter.ITableDataAugmenter;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import javax.sql.rowset.CachedRowSet;

/** retrieve table data (optional). */
public class TableDataAugmenter implements ITableDataAugmenter {
	
	public static final String DATA_QUERY_FILENAME = ".data.sample.sql";
	
	private static final Logger LOG = LoggerFactory.getLogger(TableDataAugmenter.class);

	private CachedRowSet rowSet = null;
	

	/** load (optional) sql statement from configuration directory. */
	@Override
	public void loadConfiguration(IAugmenterConfiguration config, IDbTable table, IScanner scanner) {
		File path = config.getConfigFile(table, table.getName() + DATA_QUERY_FILENAME);
		if (path != null) {
			String sqlSelect = FileHelper.readFileContent(path);
			LOG.debug("data query for augmenting table " + table.fqnName() + " will be " + sqlSelect);
			if (sqlSelect != null) {
				this.rowSet = scanner.getQueryData(table, sqlSelect);
			}
		}
	}


	@Override
	public CachedRowSet getData() {
		return rowSet;
	}

}
