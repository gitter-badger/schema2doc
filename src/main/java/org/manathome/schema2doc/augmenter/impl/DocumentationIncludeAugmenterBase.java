package org.manathome.schema2doc.augmenter.impl;

import org.manathome.schema2doc.augmenter.IDocumentationAugmenter;
import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/** base functionality includes asciidoc documentation from config path. */
public abstract class DocumentationIncludeAugmenterBase implements IDocumentationAugmenter {
	
	private static final Logger LOG = LoggerFactory.getLogger(DocumentationIncludeAugmenterBase.class);	

	protected static final String ASCIIDOC_SUFFIX = ".asciidoc";
	
	protected String additionalAsciidocToInclude = null;
	
	protected void loadConfiguration(File configFile) {
		if (configFile != null) {
			this.additionalAsciidocToInclude = FileHelper.readFileContent(configFile);
			if (additionalAsciidocToInclude != null) {
				LOG.debug("additional docs for from " + configFile.getAbsolutePath() 
							+ " will be included " 
							+ Convert.left(additionalAsciidocToInclude, 40) + "..");
			}
		}
	}

	@Override
	public String getData() {
		return this.additionalAsciidocToInclude;
	}

}
