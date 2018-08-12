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
package org.azentreprise.arionide.lang.natives;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.lang.TypeManager;

public class IntegerTypeManager implements TypeManager {
	
	public static final String ZERO = "0" + SpecificationElement.ALIAS + "b0";
	public static final String ONE = "1" + SpecificationElement.ALIAS + "b1";
	public static final String MINUS_ONE = "-1" + SpecificationElement.ALIAS + "d-1";
	public static final String TRUE = "True" + SpecificationElement.ALIAS + "b1";
	public static final String FALSE = "False" + SpecificationElement.ALIAS + "b0";
	public static final String ERROR = "Error" + SpecificationElement.ALIAS + "d-1";
	public static final String MAX_INTEGER = "Max Integer" + SpecificationElement.ALIAS + "h7FFFFFFF";
	public static final String MIN_INTEGER = "Min Integer" + SpecificationElement.ALIAS + "h-7FFFFFFF";
	public static final String MAX_BYTE = "Max Byte" + SpecificationElement.ALIAS + "h7F";
	public static final String MIN_BYTE = "Min Byte" + SpecificationElement.ALIAS + "h-7F";
	public static final String MAX_SHORT = "Max Short" + SpecificationElement.ALIAS + "h7FFF";
	public static final String MIN_SHORT = "Min Short" + SpecificationElement.ALIAS + "h-7FFF";
	
	private static final String decimal = "Decimal";
	private static final String binary = "Binary";
	private static final String hexadecimal = "Hexadecimal";
	
	public List<String> getSuggestions(CoreDataManager cdm) {
		return Arrays.asList(MIN_SHORT, MAX_SHORT, MIN_BYTE, MAX_BYTE, MIN_INTEGER, MAX_INTEGER, ERROR, FALSE, TRUE, MINUS_ONE, ONE, ZERO);
	}

	public List<String> getActionLabels() {
		return Arrays.asList(decimal, binary, hexadecimal);
	}

	public List<BiConsumer<String, Consumer<String>>> getActions() {
		return Arrays.asList(this::editDecimal, this::editBinary, this::editHexadecimal);
	}
	
	private void editDecimal(String current, Consumer<String> callback) {
		this.edit(current, "decimal", 'd', callback);
	}
	
	private void editBinary(String current, Consumer<String> callback) {
		this.edit(current, "binary", 'b', callback);
	}
	
	private void editHexadecimal(String current, Consumer<String> callback) {
		this.edit(current, "hexadecimal", 'h', callback);
	}
	
	private void edit(String current, String type, char id, Consumer<String> callback) {
		String initial;
		
		if(current == null) {
			initial = new String();
		} else if(current.length() > 0) {
			initial = current.substring(1);
		} else {
			initial = current;
		}
						
		new Thread(() -> {
			Object output = JOptionPane.showInputDialog(null, "Enter the new " + type + " value:", "Custom integer", JOptionPane.PLAIN_MESSAGE, null, null, initial);
			
			if(output != null) {
				callback.accept(id + (String) output);
			}
		}).start();
	}
	
	public String toString() {
		return "Integer";
	}
}