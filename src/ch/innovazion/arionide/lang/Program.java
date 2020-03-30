package ch.innovazion.arionide.lang;

import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.InheritanceElement;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.mutables.MutableActor;

public abstract class Program<T> {
	
	private final Storage storage;
	
	public Program(Storage storage) {
		this.storage = storage;
	}
	
	protected InheritanceElement getInheritance(int id) {
		return storage.getInheritance().get(id);
	}
	
	protected Callable getCallable(int id) {
		return storage.getStructures().get(id);
	}
	
	protected Information getState(int id) {
		Callable callable = getCallable(id);
		
		if(callable != null && callable instanceof MutableActor) {
			return ((MutableActor) callable).getWrapper().getState();
		} else {
			return new Information();
		}
	}
	
	protected Information getProperties(int id) {
		Callable callable = getCallable(id);
		
		if(callable != null && callable instanceof MutableActor) {
			return ((MutableActor) callable).getWrapper().getProperties();
		} else {
			return new Information();
		}
	}
	
	protected Information getConstants(int id) {
		Callable callable = getCallable(id);
		
		if(callable != null && callable instanceof MutableActor) {
			return ((MutableActor) callable).getWrapper().getConstants();
		} else {
			return new Information();
		}
	}
	
	protected List<Callable> getInstructions(int id) {
		return storage.getCode().get(id).list().stream().map(HierarchyElement::getID).map(this::getCallable).collect(Collectors.toList());
	}

	public abstract T run(int rootStructure);
}
