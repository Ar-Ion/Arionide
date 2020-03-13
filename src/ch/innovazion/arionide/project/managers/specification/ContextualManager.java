package ch.innovazion.arionide.project.managers.specification;

import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Storage;

public abstract class ContextualManager<T> extends Manager {
	
	private T context;
	
	protected ContextualManager(Storage storage) {
		super(storage);
	}
	
	public void setContext(T context) {
		this.context = context;
	}
	
	protected T getContext() {
		if(context != null) {
			return context;
		} else {
			throw new IllegalStateException("Contextual managers must have a defined context before they can be used");
		}
	}
}
