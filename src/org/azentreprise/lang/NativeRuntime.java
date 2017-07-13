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
