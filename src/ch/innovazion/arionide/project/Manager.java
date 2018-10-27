package ch.innovazion.arionide.project;

import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableHistoryElement;
import ch.innovazion.arionide.project.mutables.MutableInheritanceElement;
import ch.innovazion.arionide.project.mutables.MutableStructureMeta;

public abstract class Manager {
	
	private final Storage storage;
	
	protected Manager(Storage storage) {
		this.storage = storage;
	}
	
	protected List<MutableHierarchyElement> getHierarchy() {
		return this.storage.getMutableHierarchy();
	}
	
	protected Map<Integer, MutableInheritanceElement> getInheritance() {
		return this.storage.getMutableInheritance();
	}
	
	protected List<MutableHierarchyElement> getCallGraph() {
		return this.storage.getMutableCallGraph();
	}
	
	protected Map<Integer, MutableStructureMeta> getMeta() {
		return this.storage.getMutableStructureMeta();
	}
	
	protected List<MutableHistoryElement> getHistory() {
		return this.storage.getMutableHistory();
	}
	
	protected Map<Integer, MutableCodeChain> getCode() {
		return this.storage.getMutableCode();
	}
	
	protected void saveHierarchy() {
		try {
			this.storage.saveHierarchy();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveInheritance() {
		try {
			this.storage.saveInheritance();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}

	protected void saveCallGraph() {
		try {
			this.storage.saveCallGraph();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveMeta() {
		try {
			this.storage.saveStructureMeta();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveHistory() {
		try {
			this.storage.saveHistory();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveCode() {
		try {
			this.storage.saveCode();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected Storage getStorage() {
		return this.storage;
	}
}