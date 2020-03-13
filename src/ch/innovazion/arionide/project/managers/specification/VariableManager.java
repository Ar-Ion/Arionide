package ch.innovazion.arionide.project.managers.specification;

import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.project.Storage;

public class VariableManager extends ContextualManager<Variable> {
	protected VariableManager(Storage storage) {
		super(storage);
	}
}
