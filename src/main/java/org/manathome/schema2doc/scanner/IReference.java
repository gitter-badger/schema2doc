package org.manathome.schema2doc.scanner;

import org.manathome.schema2doc.util.NotNull;

/** reference. */
public interface IReference extends Comparable<IReference>{
	@NotNull String display();
}
