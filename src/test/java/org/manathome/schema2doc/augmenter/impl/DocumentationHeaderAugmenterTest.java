package org.manathome.schema2doc.augmenter.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** tests. */
public class DocumentationHeaderAugmenterTest extends AugmenterTestBase{

	private static final Logger LOG = LoggerFactory.getLogger(DocumentationHeaderAugmenterTest.class);
	
	@Before
	public void setUp() throws Exception {
		this.internalSetUp();
	}
	
	
	@Test
	public void testRetrieveDocData() throws Exception {		
		DocumentHeaderAugmenter aug = new DocumentHeaderAugmenter();
		aug.loadConfiguration(s2d);
		
		assertNotNull(aug.getData());
		LOG.debug(aug.getData());
		assertThat(aug.getData(), containsString("hand written asciidoc"));
	}

}
