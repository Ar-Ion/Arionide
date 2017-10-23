package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Debug implements NativeInstruction {
	
	private final String message;
	
	public Debug(String message) {
		this.message = message;
	}

	public void execute(NativeDataCommunicator communicator, List<Integer> references) {
		System.out.println(this.message);
	}
}