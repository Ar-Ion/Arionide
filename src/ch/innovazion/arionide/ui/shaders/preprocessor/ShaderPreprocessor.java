/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.shaders.preprocessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShaderPreprocessor {
	
	private static final Map<String, Integer[]> argumentCount = new HashMap<>(); // Sorted assumption
	
	static {
		argumentCount.put("config", new Integer[1]);
		argumentCount.put("import", new Integer[1]);
	}
	
	
	private final ShaderSettings settings;
	
	public ShaderPreprocessor(ShaderSettings settings) {
		this.settings = settings;
	}
	
	public String processCommand(String command, String[] args) throws PreprocessorException {
		this.checkCommand(command, args.length);
		
		switch(command) {
			case "config":
				return this.settings.resolveConstant(args[0]);	
			case "import":
				return this.settings.resolveFunction(args[0]);
			default:
				throw new PreprocessorException();
		}
	}
	
	private void checkCommand(String command, int count) throws PreprocessorException {
		Integer[] argumentCounts = argumentCount.get(command);
		
		if(argumentCounts != null) {
			if(Arrays.binarySearch(argumentCounts, Integer.valueOf(count)) < 0) {
				throw new PreprocessorException("Preprocessor command signature mismatch");
			}
		} else {
			throw new PreprocessorException("Preprocessor command not found");
		}
	}
}
