package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public class Print implements NativeInstruction {
	
	private final String message;
	
	public Print(String message) {
		this.message = message;
	}

	public boolean execute(NativeDataCommunicator communicator, List<Integer> references) {
		communicator.info(this.message, 0xFFFFFF);
		return true;
	}
}