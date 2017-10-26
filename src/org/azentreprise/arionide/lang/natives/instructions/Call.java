package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;
import java.util.Stack;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Call implements NativeInstruction {
	
	private final int reference;
	
	public Call(int reference) {
		this.reference = reference;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		if(references.contains(this.reference)) {
			Stack<Integer> theStack = communicator.getStack();
			
			if(theStack.size() > 100) {
				communicator.exception("Stack overflow");
				return false;
			}
			
			theStack.push(this.reference);
			communicator.exec(references.indexOf(this.reference));
			theStack.pop();
			
			return true;
		} else {
			communicator.exception("Dead reference error");
			return false;
		}
	}
}