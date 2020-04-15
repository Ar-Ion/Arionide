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
package ch.innovazion.arionide.lang.peripherals;

import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Peripheral;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class SRAM implements Peripheral {
	
	private final byte[] memory;
	
	public SRAM(int size) {
		this.memory = new byte[size];
	}
	
	public int getUnsigned(int address) throws EvaluationException {
		if(address >= 0 && address < memory.length) {
			return Byte.toUnsignedInt(memory[address]);
		} else {
			throw new EvaluationException("Attempt to read from an invalid SRAM memory location: " + address);
		}
	}
	
	public void set(int address, byte value) throws EvaluationException {
		if(address >= 0 && address < memory.length) {
			memory[address] = value;
		} else {
			throw new EvaluationException("Attempt to write to an invalid SRAM memory location: " + address);
		}
	}

	public String getUID() {
		return "SRAM";
	}

	public String toString() {
		return (memory.length / 1024) + "KB SRAM memory";
	}
	
	public void sample() {
		
	}

	public void createDisplay(Container display) {
		display.add(new Label(display, "This SRAM memory has no structure"), 0.0f, 0.0f, 1.0f, 1.0f);
	}
}
