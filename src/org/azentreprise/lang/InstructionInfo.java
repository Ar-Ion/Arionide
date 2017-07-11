/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.lang;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.azentreprise.lang.isa.FuncParamType;
import org.azentreprise.lang.isa.Instruction;

public class InstructionInfo {
	
	private final Class<? extends Instruction> clazz;
	private final FuncParamType[] types;
	
	protected InstructionInfo(Class<? extends Instruction> clazz, FuncParamType... types) {
		this.clazz = clazz;
		this.types = types;
	}
	
	public Constructor<? extends Instruction> getConstructor() throws Exception {
		Class<?>[] classes = new Class<?>[this.types.length];
		Arrays.fill(classes, String.class);
		return this.clazz.getDeclaredConstructor(classes);
	}
	
	public FuncParamType[] getParamTypes() {
		return this.types;
	}
}
