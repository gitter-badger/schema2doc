package org.manathome.schema2doc.augmenter.impl;

import org.manathome.schema2doc.Schema2Doc;
import org.manathome.schema2doc.renderer.IRenderer;
import org.manathome.schema2doc.renderer.impl.AsciidocRenderer;
import org.manathome.schema2doc.scanner.IDbTable;
import org.manathome.schema2doc.scanner.IScanner;
import org.manathome.schema2doc.scanner.impl.GenericDbScanner;
import org.manathome.schema2doc.scanner.impl.H2ConnectTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.DriverManager;


/** test base. */
public abstract class AugmenterTestBase {

	protected Schema2Doc 	s2d 		= null;
	protected IScanner 		scanner 	= null;
	protected IDbTable 		taskTable 	= null;

	public AugmenterTestBase() {
		super();
	}

	
	public void internalSetUp() throws Exception {
		this.scanner = new GenericDbScanner(DriverManager.getConnection(H2ConnectTest.H2_TOTASK2_DB, "sa", ""));
		File outFile = Files.createTempFile("schema2doc.junit.aug.testdoc.", ".adoc").toFile();
		IRenderer renderer = new AsciidocRenderer(new PrintWriter(new OutputStreamWriter(
			    new FileOutputStream(outFile), "UTF-8"), true));
		this.s2d = new Schema2Doc(scanner, renderer);
		this.taskTable = scanner.getTables().filter(tbl-> tbl.getName().equals("TT_TASK")).findFirst().get();
	}

}
