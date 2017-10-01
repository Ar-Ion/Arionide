package org.azentreprise.arionide.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.Project;

public class NativeInstructionSet extends InstructionSet implements Serializable {
	private static final long serialVersionUID = 4171728714732308283L;
	
	private final Map<String, Integer> instructionSet = new HashMap<>();
	
	public NativeInstructionSet(Project project) {
		super(project);
		
		int structID = project.getProperty("structureGen", Coder.integerDecoder).intValue();
		
		if(project.getDataManager().newStructure("compiler", Arrays.asList()).getMessageType().equals(MessageType.SUCCESS)) {
			List<Integer> parents = Arrays.asList(structID);
			
			this.install("init", 0, parents);
			this.install("nop", 15, parents);
		} else {
			this.retrieve("init");
		}
	}
	
	private void install(String name, int color, List<Integer> parents) {
		this.instructionSet.put(name, this.installInstruction(name, color, parents));
	}
	
	private void retrieve(String name) {
		this.instructionSet.put(name, this.retrieveInstruction(name));
	}

	public int getStructureEntry() {
		return this.instructionSet.get("init");
	}
	
	public int getInstructionID(String name) {
		return this.instructionSet.get(name);
	}
	
	public List<String> getInstructions() {
		List<String> list = new ArrayList<>(this.instructionSet.keySet());
		list.remove("init");
		return list;
	}
}