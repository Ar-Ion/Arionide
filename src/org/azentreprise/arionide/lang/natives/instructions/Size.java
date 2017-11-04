package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Object;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class Size implements NativeInstruction {
	
	private final Data object;
	private final Data result;
	
	public Size(Data object, Data result) {
		this.object = object;
		this.result = result;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(this.object.getValue().startsWith("var@")) {
			SpecificationElement element = communicator.getVariable(this.object.getValue().substring(4));
			
			if(element != null) {
				String result = this.result.getValue();
				
				if(result.startsWith("var@")) {
					result = result.substring(4);
					
					Object object = communicator.getObject(element.getValue());
					
					if(object != null) {
						communicator.setVariable(result, communicator.isDefined(element.getValue()) && communicator.isLocal(element.getValue()), new Data(result, "d" + object.getSize(), NativeTypes.INTEGER));
						return true;
					} else {
						communicator.exception("Invalid object: " + result);
					}
				} else {
					communicator.exception("You can't use a direct value for this variable");
				}
			} else {
				communicator.exception("Dead variable: " + this.object.getValue());
			}
		} else {
			communicator.exception("You can't use a direct value for an object instance");
		}
		
		return false;
	}
}