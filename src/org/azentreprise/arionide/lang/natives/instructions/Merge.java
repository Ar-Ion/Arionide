package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class Merge implements NativeInstruction {
	
	private final Data source1;
	private final Data source2;
	private final Data destination;
	
	public Merge(Data source1, Data source2, Data destination) {
		this.source1 = source1;
		this.source2 = source2;
		this.destination = destination;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.destination.getValue().startsWith("var@")) {
			String destVar = this.destination.getValue().substring(4);
			
			String value1 = this.source1.getValue();
			String value2 = this.source2.getValue();

			if(value1.startsWith("var@")) {
				value1 = communicator.getVariable(value1).getValue();
			}
			
			if(value2.startsWith("var@")) {
				value2 = communicator.getVariable(value2).getValue();
			}
			
			communicator.setVariable(destVar, communicator.isDefined(destVar) && communicator.isLocal(destVar), new Data(destVar, value1 + value2, NativeTypes.TEXT));
		} else {
			communicator.exception("You can't use a direct for a destination variable");
		}
		
		return false;
	}
}