package org.manathome.schema2doc.augmenter.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** common augmenting tests. */
public class TableDocumentationAugmenterTest extends AugmenterTestBase {
	
	private static final Logger LOG = LoggerFactory.getLogger(TableDocumentationAugmenterTest.class);
	
	@Before
	public void setUp() throws Exception {
		this.internalSetUp();
	}
	
	
	@Test
	public void testRetrieveDocData_TT_TASK() throws Exception {		
		TableDocumentationAugmenter aug = new TableDocumentationAugmenter();
		aug.loadConfiguration(s2d, taskTable);
		
		assertNotNull(aug.getData());
		LOG.debug(aug.getData());
		assertThat(aug.getData(), containsString("should be integrated to schema2doc generated stuff."));
	}

}
