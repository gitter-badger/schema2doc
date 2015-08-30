package org.manathome.schema2doc.augmenter;

import org.manathome.schema2doc.scanner.IDbTable;

/** add additional documentation. */
public interface ITableDocumentationAugmenter {

	/** load (optional) sql statement from configuration directory. */
	void loadConfiguration(IAugmenterConfiguration config, IDbTable table);

	String getData();

}
