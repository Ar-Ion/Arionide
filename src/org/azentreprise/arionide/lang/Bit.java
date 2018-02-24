/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.lang;

public class Bit {
	
	private final int bit;
	
	public Bit(int bit) {
		this.bit = bit != 0 ? 1 : 0;
	}
	
	public int getBit() {
		return bit;
	}
	
	public String toString() {
		return String.valueOf(this.bit);
	}
	
	public static Bit[] fromByteArray(byte[] bytes) {
		Bit[] data = new Bit[8 * bytes.length];
		
		for(int i = 0; i < bytes.length; i++) {
			System.arraycopy(fromInteger(bytes[i], 8), 0, data, 8 * i, 8);
		}
		
		return data;
	}
	
	public static Bit[] fromInteger(int integer, int bits) {
		Bit[] data = new Bit[bits];

		for(int i = 0; i < bits; i++) {
			data[i] = new Bit(integer & (0x1 << (bits - i - 1)));
		}
				
		return data;
	}
}