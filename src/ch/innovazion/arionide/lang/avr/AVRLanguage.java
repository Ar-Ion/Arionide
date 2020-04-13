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
package ch.innovazion.arionide.lang.avr;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.programs.Debugger;
import ch.innovazion.arionide.lang.programs.Relocator;
import ch.innovazion.arionide.lang.programs.SkeletonBuilder;
import ch.innovazion.arionide.project.Storage;

public class AVRLanguage extends Language {

	private static final long serialVersionUID = -962242275743950263L;

	private final Environment env = new AVREnvironment(this);
	private Instruction entryPoint = new AVREntryPoint();
		
	protected void registerPrograms(Storage storage) {
		registerProgram(new SkeletonBuilder(storage));
		registerProgram(new Relocator(storage));
		registerProgram(new Debugger(storage, env));
	}
	
	protected void registerInstructions() {
		registerShadowInstruction(this.entryPoint = new AVREntryPoint());
		registerInstruction(new NoOperation());
		registerInstruction(new RelativeJump());
	}

	protected short getVersionMajor() {
		return 1;
	}

	protected short getVersionMinor() {
		return 0;
	}

	public String getVendorUID() {
		return "Atmel Corp. (Microchip Technology)";
	}

	public Instruction getEntryPoint() {
		return entryPoint;
	}

	public Environment getEnvironment() {
		return env;
	}
}
