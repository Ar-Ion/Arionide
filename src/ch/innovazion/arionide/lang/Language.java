/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.project.Storage;

public abstract class Language implements Serializable {

	private static final long serialVersionUID = 3353249857547492745L;
	
	private final short major;
	private final short minor;
	
	private transient final List<Program> programs = new ArrayList<>();
	private transient final List<Instruction> instructions = new ArrayList<>();
	private transient final List<Instruction> operators = new ArrayList<>();
	private transient final Map<String, Instruction> instructionSet = new HashMap<>();
	
	public Language() {
		this.major = getVersionMajor();
		this.minor = getVersionMinor();
		
		registerInstructions();
	}
	
	public void loadPrograms(Storage storage) {
		programs.clear();
		registerPrograms(storage);
	}
	
	protected void registerProgram(Program program) {
		programs.add(program);
	}

	protected void registerInstruction(Instruction instruction) {
		instructions.add(instruction);
		instructionSet.put(instruction.toString(), instruction);
	}
	
	protected void registerShadowInstruction(Instruction instruction) {
		instructionSet.put(instruction.toString(), instruction);
	}
	
	protected void registerOperator(Instruction instruction) {
		operators.add(instruction);
		instructionSet.put(instruction.toString(), instruction);
	}
	
	protected void checkCompatibility() throws CompatibilityException {
		if(major < getVersionMajor()) {
			throw new CompatibilityException("Langugage version may be incompatible (backward compatibility not ensured: " + major + " < " + getVersionMajor() + ")");
		}
		
		if(major > getVersionMajor() || minor > getVersionMinor()) {
			throw new CompatibilityException("Language version  (" + getVersion() + ") is probably incompatible with the current language implementation");
		}
	}
	
	public List<Instruction> getStandardInstructions() {
		return Collections.unmodifiableList(instructions);
	}
	
	public List<Instruction> getOperators() {
		return Collections.unmodifiableList(operators);
	}
	
	public Map<String, Instruction> getInstructionSet() {
		return Collections.unmodifiableMap(instructionSet);
	}
	
	public List<Program> getPrograms() {
		return Collections.unmodifiableList(programs);
	}
	
	protected String getVersion() {
		return major + "." + minor;
	}
	
	protected abstract void registerInstructions();
	protected abstract void registerPrograms(Storage storage);

	protected abstract short getVersionMajor();
	protected abstract short getVersionMinor();
	public abstract String getVendorUID();
	public abstract String getName();
	
	public abstract Instruction getEntryPoint();
	
	public abstract Environment getEnvironment();
}
