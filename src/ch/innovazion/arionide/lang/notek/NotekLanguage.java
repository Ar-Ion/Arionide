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
package ch.innovazion.arionide.lang.notek;

import ch.innovazion.arionide.lang.Block;
import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.Spacer;
import ch.innovazion.arionide.lang.notek.logical.Assume;
import ch.innovazion.arionide.project.Storage;

public class NotekLanguage extends Language {

	private static final long serialVersionUID = -8537613908838039246L;
	
	private final Environment env = new NotekEnvironment(this);
	private Instruction entryPoint = new NotekEntryPoint();
		
	protected void registerPrograms(Storage storage) {
		
	}
	
	protected void registerInstructions() {
		registerShadowInstruction(this.entryPoint = new NotekEntryPoint());
		
		registerOperator(new Spacer());
		registerOperator(new Block());
		
		registerInstruction(new Assume());
	}

	protected short getVersionMajor() {
		return 1;
	}

	protected short getVersionMinor() {
		return 0;
	}

	public String getVendorUID() {
		return "Innovazion";
	}
	
	public String getName() {
		return "Notek";
	}

	public Instruction getEntryPoint() {
		return entryPoint;
	}

	public Environment getEnvironment() {
		return env;
	}
}
