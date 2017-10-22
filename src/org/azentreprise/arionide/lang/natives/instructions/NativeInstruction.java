package org.azentreprise.arionide.lang.natives.instructions;

import java.util.List;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;

public interface NativeInstruction {
	public void execute(NativeDataCommunicator communicator, List<Integer> references);
}