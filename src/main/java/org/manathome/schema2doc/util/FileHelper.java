package org.manathome.schema2doc.util;

import org.manathome.schema2doc.augmenter.AugmenterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/** file based utilities. */
public class FileHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);

	/** reading file content into string (UTF-8). */
	public static String readFileContent(File file) {
		try {
			return new String(Files.readAllBytes(Require.notNull(file, "file").toPath()), StandardCharsets.UTF_8);
		} catch (IOException ioex) {		
			LOG.error("error reading file " + file + ":" + ioex.getMessage(), ioex);
			throw new AugmenterException("error reading " + file.getName() + ": " + ioex.getMessage(), ioex);
		}
	}
}
