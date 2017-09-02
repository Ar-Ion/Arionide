package org.azentreprise.arionide.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.ui.menu.Coloring;

public class DataManager {
	
	private final Project project;
	private final Storage storage;
	
	public DataManager(Project project) {
		this.project = project;
		this.storage = this.project.getStorage();
	}
	
	public MessageEvent newStructure(String name, List<Integer> parents) {		
		long structureID = this.project.getProperty("structureGen", Coder.integerDecoder).intValue();
		this.project.setProperty("structureGen", structureID + 1, Coder.integerEncoder); // Increment generator
		this.project.invalidateCacheProperty("structureGen");
		this.project.save();
				
		StructureElement structure = new StructureElement((int) structureID, new ArrayList<>());
		
		List<StructureElement> brothers = this.getBrothers(this.storage.hierarchy, parents);
		
		if(brothers == null) {
			return new MessageEvent("Invalid parent hierarchy", MessageType.ERROR);
		}
		
		brothers.add(structure);
		this.storage.saveHierarchy();

		this.storage.inheritance.add(structure);
		this.storage.saveInheritance();
		
		this.storage.callGraph.add(structure);
		this.storage.saveCallGraph();
		
		this.storage.structMeta.put((int) structureID, new StructureMeta());
		
		MessageEvent message = this.setName((int) structureID, name, parents);
				
		return message.getMessageType() != MessageType.SUCCESS ? message : new MessageEvent("Structure created", MessageType.SUCCESS);
	}
	
	public MessageEvent deleteStructure(int id, List<Integer> parents) {
		Iterator<StructureElement> iterator = this.getBrothers(this.storage.hierarchy, parents).iterator();
		
		while(iterator.hasNext()) {
			StructureElement element = iterator.next();
			
			if(element.getID() == id) {
				iterator.remove();
				this.storage.saveHierarchy();
				
				this.deleteMeta(element);
				this.storage.saveStructureMeta();
				
				return new MessageEvent("Structure deleted", MessageType.SUCCESS);
			}
		}
		
		return new MessageEvent("This structure doesn't exist anymore", MessageType.ERROR);			
	}
	
	private void deleteMeta(StructureElement element) {
		this.storage.structMeta.remove(element.getID());
		element.getChildren().stream().forEach(this::deleteMeta);
	}
	
	/* Might need some optimization */
	private StructureElement find(List<StructureElement> elements, int id) {
		for(StructureElement element : elements) {
			if(element.getID() != id) {
				return this.find(element.getChildren(), id);
			} else {
				return element;
			}
		}
		
		return null;
	}
	
	private List<StructureElement> getBrothers(List<StructureElement> root, List<Integer> parents) {
		for(Integer id : parents) {
			boolean found = false;
			
			for(StructureElement bro : root) {				
				if(bro.getID() == id) {
					root = bro.getChildren0();
					found = true;
					break;
				}
			}
			
			if(!found) {
				return null;
			}
		}
		
		return root;
	}
	
	public MessageEvent setName(int id, String name, List<Integer> parents) {
		StructureMeta meta = this.storage.getStructureMeta().get(id);
		
		if(meta != null) {
			meta.setName(name);
			this.storage.saveStructureMeta();
			
			if(name.isEmpty()) {
				return new MessageEvent("Empty names are discouraged", MessageType.WARN);
			} else {
				return new MessageEvent("Name successfully updated", MessageType.SUCCESS);
			}
		} else {
			return new MessageEvent("Invalid structure id", MessageType.ERROR);
		}
	}
	
	public MessageEvent setColor(int id, int colorID) {
		if(Coloring.hasColor(colorID)) {
			StructureMeta meta = this.storage.getStructureMeta().get(id);
			
			if(meta != null) {
				meta.setColorID(colorID);
				
				this.storage.saveStructureMeta();
				
				return new MessageEvent("Color sucessfully changed", MessageType.SUCCESS);
			} else {
				return new MessageEvent("Invalid structure id", MessageType.ERROR);
			}
		} else {
			return new MessageEvent("Invalid color id", MessageType.ERROR);
		}
	}
}