package org.manathome.schema2doc.augmenter.impl;

import org.manathome.schema2doc.augmenter.IAugmenterConfiguration;
import org.manathome.schema2doc.scanner.IDbTable;

import java.io.File;


/** weave in additional asciidoc documentation per table. 
 * 
 * @author man-at-home
 * */
public class TableDocumentationAugmenter extends DocumentationIncludeAugmenterBase {

	public void loadConfiguration(IAugmenterConfiguration config, IDbTable table) {
		File configFile = config.getConfigFile(table, table.getName() + ASCIIDOC_SUFFIX);
		super.loadConfiguration(configFile);
	}	
}
