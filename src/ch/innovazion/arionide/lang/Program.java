package ch.innovazion.arionide.lang;

import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.InheritanceElement;
import ch.innovazion.arionide.project.Storage;

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
	
	protected List<Callable> getInstructions(int id) {
		return storage.getCode().get(id).list().stream().map(HierarchyElement::getID).map(this::getCallable).collect(Collectors.toList());
	}

	public abstract T run(int rootStructure);
}
