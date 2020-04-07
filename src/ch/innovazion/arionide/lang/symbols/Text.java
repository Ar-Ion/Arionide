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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Text extends Node {

	private static final long serialVersionUID = -4429297123778703745L;
	
	private String textValue = String.valueOf('\0');
	private Bit[] realValue = new Bit[8];

	public Text() {
		super("text");
	}
	
	private Text(Text parent) {
		super(parent);
		
		this.textValue = parent.textValue;
		this.realValue = new Bit[parent.realValue.length];

		System.arraycopy(parent.realValue, 0, realValue, 0, realValue.length);
	}
	
	public void parse(String value) throws InvalidValueException {
		if(value != null) {
			this.textValue = value;
			this.realValue = Bit.fromByteArray(value.getBytes(Charset.forName("utf8")));
		} else {
			throw new InvalidValueException("Entered text cannot be null");
		}
	}
	
	public int getSize() {
		return realValue.length;
	}

	protected Stream<Bit> getRawStream() {
		return Stream.of(realValue);
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList("'" + textValue + "'");
	}
	
	public Text clone() {
		return new Text(this);
	}
}