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
		this.data = Bit.fromInteger(BigInteger.valueOf(num), 64);
	}
	
	public void parse(String value) throws InvalidValueException {
		this.value = value;
		
		char type = this.value.charAt(0);
		String realValue = this.value.substring(1);
		
		switch(type) {
			case 'b':
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(realValue, 2)), realValue.length());
			case 'd':
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(realValue)), 32);
			case 'h':
				data = Bit.fromInteger(BigInteger.valueOf(Long.parseLong(realValue, 16)), realValue.length() * 4);
		}
		
		data = new Bit[0];
	}
	
	public Stream<Bit> getRawStream() {
		return Stream.of(data);
	}

	public List<String> getDisplayValue() {
		return Arrays.asList(value, "(" + data.length + " bit(s))");
	}
	
	public Numeric clone() {
		Numeric clone = new Numeric();
		
		clone.data = new Bit[data.length];
		clone.value = value;
		
		System.arraycopy(data, 0, clone.data, 0, data.length);
		
		return clone;
	}
}