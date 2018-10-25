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
package ch.innovazion.arionide.lang;

import java.util.Arrays;

import ch.innovazion.arionide.lang.natives.NativeTypes;

public class IntegerObject extends Object {

	private String value;
	
	protected IntegerObject(String value, String id) {
		super(Arrays.asList(NativeTypes.INTEGER), id);
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public Bit[] getData() {
		char type = this.value.charAt(0);
		String realValue = this.value.substring(1);
		
		switch(type) {
			case 'b':
				return Bit.fromInteger(Integer.parseInt(realValue, 2), realValue.length());
			case 'd':
				return Bit.fromInteger(Integer.parseInt(realValue), 32);
			case 'h':
				return Bit.fromInteger(Integer.parseInt(realValue, 16), realValue.length() * 4);
		}
		
		return new Bit[0];
	}
}