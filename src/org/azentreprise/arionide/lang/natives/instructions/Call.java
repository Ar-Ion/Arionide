package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Call implements NativeInstruction {
	
	private final int reference;
	
	public Call(int reference) {
		this.reference = reference;
	}
	
	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		return true;
	}
}