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
package ch.innovazion.arionide.lang.natives;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ch.innovazion.arionide.lang.TypeManager;
import ch.innovazion.arionide.lang.UserHelper;

public class StructureTypeManager implements TypeManager {
		
	public List<String> getSuggestions(UserHelper cdm) {
		return Arrays.asList();
	}

	public List<String> getActionLabels() {
		return Arrays.asList("Add structure", "Add integer", "Add text", "Delete last");
	}

	public List<BiConsumer<String, Consumer<String>>> getActions() {
		return Arrays.asList(this::addStructure, this::addInteger, this::addText, this::deleteLast);
	}
	
	private void addStructure(String current, Consumer<String> callback) {
		this.add(NativeTypes.STRUCTURE, current, callback);
	}
	
	private void addInteger(String current, Consumer<String> callback) {
		this.add(NativeTypes.INTEGER, current, callback);
	}

	private void addText(String current, Consumer<String> callback) {
		this.add(NativeTypes.TEXT, current, callback);
	}
	
	private void add(int element, String current, Consumer<String> callback) {
		if(current != null && !current.isEmpty()) {
			current += "; ";
		} else {
			current = new String();
		}
		
		callback.accept(current + element);
	}
	
	private void deleteLast(String current, Consumer<String> callback) {
		int index = current.lastIndexOf(';');
		
		if(index > -1) {
			callback.accept(current.substring(0, index));
		} else {
			callback.accept(new String());
		}
	}
}