/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.lang;

import java.util.Arrays;

public class Structure {
	
	protected final String[] descriptors;
	protected final int[] structure;
	
	public Structure(String format) {
		String[] array = format.split("|");
	
		this.descriptors = new String[array.length];
		this.structure = new int[array.length];
		
		for(int i = 0; i < array.length; i++) {
			String struct = null;
			
			if(array[i].indexOf(':') > -1) {
				String[] splitted = array[i].split(":");
				this.descriptors[i] = splitted[0];
				struct = splitted[1];
			} else {
				struct = array[i];
			}
				
			this.structure[i] = Integer.parseInt(struct);
		}
	}
	
	public int indexOf(String name) {
		return Arrays.binarySearch(this.descriptors, name);
	}
	
	public int getStructureElement(int index) {
		return this.structure[index];
	}
}
