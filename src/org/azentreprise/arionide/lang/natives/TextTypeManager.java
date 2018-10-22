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

import org.azentreprise.arionide.lang.UserHelper;
import org.azentreprise.arionide.lang.TypeManager;

public class TextTypeManager implements TypeManager {

	public List<String> getSuggestions(UserHelper cdm) {
		return Arrays.asList();
	}

	public List<String> getActionLabels() {
		return Arrays.asList("Custom text");
	}

	public List<BiConsumer<String, Consumer<String>>> getActions() {
		return Arrays.asList(this::edit);
	}
	
	private void edit(String current, Consumer<String> callback) {		
		new Thread(() -> {
			Object output = JOptionPane.showInputDialog(null, "Enter the new text:", "Custom text", JOptionPane.PLAIN_MESSAGE, null, null, current);
			
			if(output != null) {
				callback.accept((String) output);
			}
		}).start();
	}
	
	public String toString() {
		return "Text";
	}
}