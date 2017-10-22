package org.azentreprise.arionide.lang.natives.instructions;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Debug implements NativeInstruction {
	
	private final String message;
	
	public Debug(String message) {
		this.message = message;
	}

	public void execute(NativeDataCommunicator communicator) {
		System.out.println(this.message);
	}
}