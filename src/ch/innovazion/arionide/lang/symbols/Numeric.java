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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Numeric extends AtomicValue {
	
	private static final long serialVersionUID = -7392411725390812250L;
	
	private Bit[] data = new Bit[0];
	private String value = "NaN";
	
	public Numeric() {
		this(0L);
	}
	
	public Numeric(long num) {
		super("number");
		this.data = Bit.fromInteger(BigInteger.valueOf(num), 64);
	}
	
	private Numeric(Numeric parent) {
		super(parent);
		
		this.data = new Bit[parent.data.length];
		this.value = parent.value;
		
		System.arraycopy(parent.data, 0, data, 0, data.length);
	}
	
	public void parse(String value) throws InvalidValueException {
		this.value = value;
		
		String type = value.length() >= 2 ? value.substring(0, 2) : "decimal";
		int separator = value.indexOf(":");
		int numBits = 0;
				
		if(separator > 0) {
			numBits = Integer.parseInt(value.substring(separator));
		} else {
			separator = value.length();
		}
		
		String rawValue;
		
		switch(type) {
			case "0b":
				rawValue = value.substring(2, separator);
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(rawValue, 2)), numBits > 0 ? numBits : rawValue.length());
				break;
			case "0x":
				rawValue = value.substring(2, separator);
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(rawValue, 16)), numBits > 0 ? numBits : rawValue.length() * 4);
				break;
			case "$":
				rawValue = value.substring(1, separator);
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(rawValue, 16)), numBits > 0 ? numBits : rawValue.length() * 4);
				break;
			default:
				rawValue = value.substring(0, separator);
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(rawValue)), numBits > 0 ? numBits : 32);
		}
	}
	
	public int getSize() {
		return data.length;
	}
	
	public Stream<Bit> getRawStream() {
		return Stream.of(data);
	}

	public List<String> getDisplayValue() {
		return Arrays.asList(value + " (" + data.length + " bit(s))");
	}
	
	public Numeric clone() {
		return new Numeric(this);
	}
}