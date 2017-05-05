package org.azentreprise.lang;

import java.io.IOException;

import org.azentreprise.configuration.Definitions;
import org.azentreprise.lang.Executable.FunctionDescriptor;

public class NativeRuntime extends Runtime {

	public NativeRuntime(String executableName, Definitions definitions) throws SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
		super(executableName, new NativeDefinitions());
	}

	public int checkInternalCoherance(FunctionDescriptor descriptor) {
		return 0;
	}

	public int dispatchInstruction(byte id, byte[] arguments, String[] constantPool) {
		System.out.println(id);
		return 0;
	}

	// 64-bits compatible
	public long getMaximalMemorySize() {
		return Integer.MAX_VALUE;
	}

	public long getMaximalStackSize() {
		return 1024;
	}

	public int getUndefinedSymbolErrorCode() {
		return 0x1;
	}

	public int getMemoryOverflowErrorCode() {
		return 0x2;
	}

	public int getStackOverflowErrorCode() {
		return 0x3;
	}

}
