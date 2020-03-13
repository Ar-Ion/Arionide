package ch.innovazion.arionide.project.managers.specification;

import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.project.Storage;

public class ParameterValueManager<T extends ParameterValue> extends ContextualManager<T> {

	protected ParameterValueManager(Storage storage) {
		super(storage);
	}

}
