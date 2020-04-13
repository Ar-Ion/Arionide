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
package ch.innovazion.arionide.lang.symbols;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.stream.Stream;

public class Bit implements Serializable {

	private static final long serialVersionUID = 3232953289986528460L;
	
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
			System.arraycopy(fromInteger(BigInteger.valueOf(bytes[i]), 8), 0, data, 8 * i, 8);
		}
		
		return data;
	}
	
	public static Bit[] fromInteger(BigInteger integer, int bits) {
		Bit[] data = new Bit[bits];

		for(int i = 0; i < bits; i++) {
			data[i] = new Bit(integer.and(BigInteger.ONE.shiftLeft(bits - i - 1)).intValue());
		}
				
		return data;
	}
	
	public static long toInteger(Bit[] bits) {
		long output = 0L;
		
		for(Bit bit : bits) {
			output <<= 1;
			output |= bit.bit;
		}
		
		return output;
	}
	
	public static long toInteger(Stream<Bit> bits) {
		return bits.mapToLong(Bit::getBit).reduce(0, (a, b) -> (a << 1) | b);
	}
}