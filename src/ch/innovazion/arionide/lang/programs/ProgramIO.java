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
package ch.innovazion.arionide.lang.programs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ProgramIO {
	
	private final Map<Class<?>, Object> storage = new HashMap<>();
	private final List<BiConsumer<String, Integer>> consoles = new ArrayList<>();
	
	public void registerConsole(BiConsumer<String, Integer> output) {
		consoles.add(output);
	}
	
	void error(String message) {
		consoles.forEach(c -> c.accept("Error: " + message, 0xAA0000));
	}
	
	void fatal(String message) {
		consoles.forEach(c -> c.accept("Fatal: " + message, 0xFF0000));
	}
	
	void log(String message) {
		consoles.forEach(c -> c.accept(message, 0xFFFFFF));
	}
	
	@SuppressWarnings("unchecked")
	<T> T in(Class<T> clazz) {
		return (T) storage.get(clazz);
	}
	
	void out(Object obj) {
		storage.put(obj.getClass(), obj);
	}
}
