/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.lang;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.configuration.Definitions;
import org.azentreprise.lang.Executable.CodeDescriptor;
import org.azentreprise.lang.Executable.FunctionDescriptor;

public abstract class Runtime {
	
	private final File executableFile;
	private final Definitions definitions;
	
	private final Map<Integer, FunctionDescriptor> functionDescriptors = new HashMap<>();
	private final Map<Integer, CodeDescriptor> codeDescriptors = new HashMap<>();
	private final Stack<Integer> stack = new Stack<>();
	private final Map<String, String> variables = new HashMap<>();
	
	private int instructionPointer = 0;
	
	public Runtime(String executableName, Definitions definitions) {
		File buildDir = new File(Arionide.getSystemConfiguration().workspaceLocation, "build");
		buildDir.mkdirs();
		
		this.executableFile = new File(Arionide.getSystemConfiguration().workspaceLocation, executableName + ".langarion");
		this.definitions = definitions;
	}
	
	public int alloc(String key, String value) {
		if(this.variables.size() <= this.getMaximalMemorySize()) {
			this.variables.put(key, value);
			return 0;
		} else {
			return this.getMemoryOverflowErrorCode();
		}
	}
	
	public int free(String key) {
		this.variables.remove(key);
		return 0;
	}
	
	public int call(int uid) throws LangarionError {
		FunctionDescriptor fd = this.functionDescriptors.get(uid);
		CodeDescriptor cd = this.codeDescriptors.get(uid);
		
		if(fd != null && cd != null) {
			int code = this.checkInternalCoherance(fd);
			
			if(code != 0) {
				return code;
			} else if(this.stack.size() <= this.getMaximalStackSize() || this.getMaximalStackSize() < 0) {
				String[] constantPool = cd.getConstantPool();
				byte[] byteCode = cd.getCode();
				
				this.stack.push(uid);
				this.instructionPointer = 0;
				
				for(int i = 0; i < byteCode.length; this.instructionPointer++) {
					String definition = this.definitions.objects.get(byteCode[i]);
					int length = definition.split(" ").length;
					
					byte[] arguments = new byte[length];
					System.arraycopy(byteCode, i + 1, arguments, 0, length);
					
					int returnCode = this.dispatchInstruction(byteCode[i], arguments, constantPool);
					
					if(returnCode != 0) {
						return returnCode;
					}
					
					i += length + 1;
				}
				
				this.stack.pop();
				
				return 0;
			} else {
				return this.getStackOverflowErrorCode();
			}
		} else {
			return this.getUndefinedSymbolErrorCode();
		}
	}
	
	public int run() {
		Debug.taskBegin("running"); try {
			Executable executable = new Executable(new FileInputStream(this.executableFile));
			
			this.functionDescriptors.clear();
			this.codeDescriptors.clear();
			
			for(FunctionDescriptor descriptor : executable.getFunctionDescriptors()) {
				this.functionDescriptors.put(descriptor.getUID(), descriptor);
			}
			
			for(CodeDescriptor descriptor : executable.getCodeDescriptors()) {
				this.codeDescriptors.put(descriptor.getUID(), descriptor);
			}
			
			String entrySymbol = this.definitions.transcriptions.get("entry");
			
			if(entrySymbol != null) {
				int symbolUID = Compiler.getHashForElement(entrySymbol);
				return this.call(symbolUID);
			} else {
				throw new LangarionError("Crash: No entry symbol defined");
			}
		} catch(Exception exception) {
			Debug.exception(exception);
		} Debug.taskEnd();
		
		return -1;
	}
	
	public int[] getStacktrace() {
		int[] stacktrace = new int[this.stack.size() + 1];
		
		stacktrace[0] = this.instructionPointer;
		
		while(this.stack.size() > 0) {
			stacktrace[this.instructionPointer++] = this.stack.pop();
		}
		
		return stacktrace;
	}
	
	public abstract int checkInternalCoherance(FunctionDescriptor descriptor);
	public abstract int dispatchInstruction(byte id, byte[] arguments, String[] constantPool);
	
	public abstract long getMaximalMemorySize();
	public abstract long getMaximalStackSize();
	
	public abstract int getUndefinedSymbolErrorCode();
	public abstract int getMemoryOverflowErrorCode();
	public abstract int getStackOverflowErrorCode();
 }