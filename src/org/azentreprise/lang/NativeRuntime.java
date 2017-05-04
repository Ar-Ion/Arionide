package org.azentreprise.lang;

import org.azentreprise.configuration.Definitions;
import org.azentreprise.lang.Executable.FunctionDescriptor;

public class NativeRuntime extends Runtime {

	public NativeRuntime(String executableName, Definitions definitions) {
		super(executableName, definitions);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int checkInternalCoherance(FunctionDescriptor descriptor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int dispatchInstruction(byte id, byte[] arguments, String[] constantPool) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaximalMemorySize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaximalStackSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUndefinedSymbolErrorCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMemoryOverflowErrorCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStackOverflowErrorCode() {
		// TODO Auto-generated method stub
		return 0;
	}

}
