package org.azentreprise.arionide.project;

import java.util.ArrayList;
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
		List<StructureElement> brothers = this.storage.getHierarchy0();
		
		for(Integer id : parents) {
			boolean found = false;

			for(StructureElement bro : brothers) {				
				if(bro.getID() == id) {
					brothers = bro.getChildren0();
					found = true;
					break;
				}
			}
			
			if(!found) {
				return new MessageEvent("The structure you are in is invalid", MessageType.ERROR);
			}
		}
		
		long structureID = this.project.getProperty("structureGen", Coder.integerDecoder).intValue();
		this.project.setProperty("structureGen", structureID + 1, Coder.integerEncoder); // Increment generator
		this.project.save();
		
		brothers.add(new StructureElement((int) structureID, new ArrayList<>()));
		this.storage.getStructureMeta0().put((int) structureID, new StructureMeta());

		this.storage.saveHierarchy();
		
		return this.setName((int) structureID, name);
	}
	
	public MessageEvent setName(int id, String name) {
		if(this.storage.getStructureMeta().containsKey(id)) {
			this.storage.getStructureMeta().get(id).setName(name);
			
			this.storage.saveStructureMeta();
			
			if(name.isEmpty()) {
				return new MessageEvent("Empty names are discouraged", MessageType.WARN);
			} else {
				return new MessageEvent("Structure successfully created", MessageType.SUCCESS);
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