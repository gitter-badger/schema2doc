package org.manathome.schema2doc.scanner;

import java.util.stream.Stream;

public interface IScanner {

	Stream<IDbTable> getTables();

	Stream<IDbColumn> getColumns(IDbTable table);

}