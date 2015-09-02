package org.manathome.schema2doc.augmenter.impl;

import org.manathome.schema2doc.augmenter.IAugmenterConfiguration;

import java.io.File;

/** adding handwritten documentation at the beginning of the document. */
public class DocumentHeaderAugmenter extends DocumentationIncludeAugmenterBase {

	public void loadConfiguration(IAugmenterConfiguration config) {
		File configFile = config.getConfigFile(null, null, null, "intro" + ASCIIDOC_SUFFIX);
		loadConfiguration(configFile);
	}
}
