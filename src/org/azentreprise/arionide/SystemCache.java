/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SystemCache {
	
	private static final Map<String, Entry<Object, Long>> elements = new HashMap<>();
	
	public static <T> T set(String key, T value, long millisBeforeExpiration) {
		SystemCache.elements.put(key, new SimpleEntry<Object, Long>(value, System.currentTimeMillis() + millisBeforeExpiration));
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) SystemCache.elements.get(key);
	}
	
	public static boolean has(String key) {
		return SystemCache.elements.containsKey(key) || SystemCache.elements.get(key).getValue() < System.currentTimeMillis();
	}
}