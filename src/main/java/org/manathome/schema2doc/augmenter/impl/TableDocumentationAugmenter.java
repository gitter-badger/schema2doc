package org.manathome.schema2doc.augmenter.impl;

import org.manathome.schema2doc.augmenter.IAugmenterConfiguration;
import org.manathome.schema2doc.augmenter.ITableDocumentationAugmenter;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.util.Convert;
import org.manathome.schema2doc.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/** weave in additional asciidoc documentation per table. 
 * 
 * @author man-at-home
 * */
public class TableDocumentationAugmenter implements ITableDocumentationAugmenter {

	private static final String TABLE_DOC_FILENAME = ".asciidoc";	
	private static final Logger LOG = LoggerFactory.getLogger(TableDocumentationAugmenter.class);
	
	private String additionalAsciidocToInclude = null;
	
	@Override
	public void loadConfiguration(IAugmenterConfiguration config, IDbTable table) {
		File path = config.getConfigFile(table, table.getName() + TABLE_DOC_FILENAME);
		if (path != null) {
			this.additionalAsciidocToInclude = FileHelper.readFileContent(path);
			if (additionalAsciidocToInclude != null) {
				LOG.debug("additional docs for table " 
							+ table.fqnName() + " will be " 
							+ Convert.left(additionalAsciidocToInclude, 80));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.manathome.schema2doc.augmenter.impl.ITableDocumentationAugmenter#getData()
	 */
	@Override
	public String getData() {
		return this.additionalAsciidocToInclude;
	}	
}
