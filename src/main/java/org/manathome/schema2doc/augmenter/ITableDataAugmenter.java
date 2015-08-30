package org.manathome.schema2doc.augmenter;

import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.util.NotNull;

import java.io.IOException;

import javax.sql.rowset.CachedRowSet;

/** getting data from tables. */
public interface ITableDataAugmenter {
	public void loadConfiguration(
			@NotNull IAugmenterConfiguration config, 
			@NotNull IDbTable table, 
			@NotNull IScanner scanner) throws IOException;
	
	public CachedRowSet getData();
}
