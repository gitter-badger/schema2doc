package org.manathome.schema2doc.augmenter;

import org.manathome.schema2doc.scanner.IDbTable;
import java.io.File;


/** configuration information for augmenter. */
public interface IAugmenterConfiguration {
	public File getConfigFile(IDbTable table, String string);
	public File getConfigFile(String catalog, String schema, String table, String fileName);
}
