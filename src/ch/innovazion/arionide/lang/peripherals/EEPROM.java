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

import java.util.function.BinaryOperator;

import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Peripheral;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class EEPROM implements Peripheral {
	
	private final int sectorSize;
	private final int subSectorSize;
	private final EEPROMType type;
	
	private final byte[] memory;
	
	public EEPROM(int size, int numSectors, int numSubsectors, EEPROMType type) {
		this.sectorSize = size / numSectors;
		this.subSectorSize = size / numSubsectors;
		this.type = type;
		this.memory = new byte[size];
	}
	
	public byte get(int address) throws EvaluationException {
		if(address >= 0 && address < memory.length) {
			return memory[address];
		} else {
			throw new EvaluationException("Attempt to read from an invalid EEPROM memory location: " + address);
		}
	}
	
	public void set(int address, byte value) throws EvaluationException {
		if(address >= 0 && address < memory.length) {
			memory[address] = type.reducer.apply(memory[address], value);
		} else {
			throw new EvaluationException("Attempt to write to an invalid EEPROM memory location: " + address);
		}
	}
	
	public void eraseAll() {
		for(int i = 0; i < memory.length; i++) {
			memory[i] = type.identity;
		}
	}
	
	public void eraseSector(int address) throws EvaluationException {
		if(address >= 0 && address < memory.length) {
			for(int i = address - address % sectorSize; i < sectorSize; i++) {
				memory[i] = type.identity;
			}
		} else {
			throw new EvaluationException("Attempt to erase the sector of an invalid EEPROM memory location: " + address);
		}
	}
	
	public void eraseSubsector(int address) throws EvaluationException {
		if(address >= 0 && address < memory.length) {
			for(int i = address - address % subSectorSize; i < subSectorSize; i++) {
				memory[i] = type.identity;
			}
		} else {
			throw new EvaluationException("Attempt to erase the subsector of an invalid EEPROM memory location: " + address);
		}
	}

	public String toString() {
		return (memory.length / 1024) + "KB EEPROM memory";
	}
	
	public String getUID() {
		return "EEPROM";
	}

	public void sample() {
		
	}

	public void createDisplay(Container display) {
		display.add(new Label(display, "This EEPROM memory has no structure"), 0.0f, 0.0f, 1.0f, 1.0f);
	}
	
	public static enum EEPROMType {
		NAND((byte) 1, (a, b) -> (byte) (a & b)),
		NOR((byte) 0, (a, b) -> (byte) (a | b));

		private byte identity;
		private BinaryOperator<Byte> reducer;
		
		private EEPROMType(byte identity, BinaryOperator<Byte> reducer) {
			this.identity = identity;
			this.reducer = reducer;
		}
	}
}
