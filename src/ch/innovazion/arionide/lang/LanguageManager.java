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
package ch.innovazion.arionide.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.lang.avr.AVRLanguage;
import ch.innovazion.arionide.lang.notek.NotekLanguage;

public class LanguageManager {
	
	private static final List<String> names = new ArrayList<>();
	private static final Map<String, Language> languages = new HashMap<>();
	
	
	static {
		register("AVR", new AVRLanguage());
		register("Notek", new NotekLanguage());
	}
	
	
	private static void register(String name, Language lang) {
		names.add(name);
		languages.put(name, lang);
	}
	
	public static List<String> getAvailableLanguages() {
		return names;
	}
	
	public static Language get(String name) {
		return languages.get(name);
	}
}
