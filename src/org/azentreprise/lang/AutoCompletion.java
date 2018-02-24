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
package org.azentreprise.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AutoCompletion {
	
	private static final List<String> nativeNames = new ArrayList<>(ISAMapping.getNames());
	private static final List<String> functionNames = Collections.synchronizedList(new ArrayList<String>());
	private static final List<String> variableNames = Collections.synchronizedList(new ArrayList<String>());
	private static final List<String> emptyList = Arrays.asList("No proposal");
	
	private static final AutoCompletionComparator theComparator = new AutoCompletionComparator();
	
	public static void initAutoCompletion() {
		functionNames.clear();
		variableNames.clear();
	}
	
	public static synchronized void defineFunction(String name) {
		functionNames.add(name);
	}
	
	public static synchronized void defineVariable(String name) {
		variableNames.add(name);
	}
	
	public static synchronized List<String> autoComplete(String name, AutoCompletionType type) {
				
		theComparator.update(name);
				
		switch(type) {
			case FUNCTION:
				functionNames.sort(theComparator);
				return functionNames.size() != 0 ? functionNames : emptyList;
			case NATIVE:
				nativeNames.sort(theComparator);
				return nativeNames.size() != 0 ? nativeNames : emptyList;
			case VARIABLE:
				variableNames.sort(theComparator);
				return variableNames.size() != 0 ? variableNames : emptyList;
			default:
				return emptyList;
		}
	}
}
