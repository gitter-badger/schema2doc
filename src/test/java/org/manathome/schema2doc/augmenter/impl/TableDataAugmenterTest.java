package org.manathome.schema2doc.augmenter.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.manathome.schema2doc.augmenter.IAugmenterConfiguration;
import org.manathome.schema2doc.augmenter.ITableDataAugmenter;
import org.manathome.schema2doc.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/** common augmenting tests. */
public class TableDataAugmenterTest extends AugmenterTestBase {
	
	private static final Logger LOG = LoggerFactory.getLogger(TableDataAugmenterTest.class);
	
	@Before
	public void setUp() throws Exception {
		this.internalSetUp();
	}
	
	@Test
	public void testConfigFileFind() {
		
		IAugmenterConfiguration config = s2d;
		
		File configFileWithSelect = config.getConfigFile(taskTable, taskTable.getName() + TableDataAugmenter.DATA_QUERY_FILENAME);
		assertNotNull(configFileWithSelect);
		
		LOG.debug(configFileWithSelect.getAbsolutePath());
		
		assertTrue(configFileWithSelect.exists());
		
		String content = FileHelper.readFileContent(configFileWithSelect);
		assertThat(content, containsString("select * from"));
	}
	
	@Test
	public void testRetrieveTableData_TT_TASK() throws Exception {		
		ITableDataAugmenter aug = new TableDataAugmenter();
		aug.loadConfiguration(s2d, taskTable, scanner);
		
		assertNotNull(aug.getData());
		assertTrue(aug.getData().next());
	}

}
