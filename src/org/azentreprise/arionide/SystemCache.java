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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SystemCache {
	
	public static final long NEVER = Long.MAX_VALUE;
	
	private static final Map<String, Entry<Object, Long>> elements = Collections.synchronizedMap(new HashMap<>());
	
	public static synchronized <T> T set(String key, T value, long millisBeforeExpiration) {
		elements.put(key, new SimpleEntry<Object, Long>(value, System.currentTimeMillis() + millisBeforeExpiration));
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) elements.get(key);
	}
	
	public static boolean has(String key) {
		return elements.containsKey(key);
	}
	
	public static synchronized void clean() {
		Iterator<Entry<Object, Long>> values = elements.values().iterator();
		
		while(values.hasNext()) {
			if(values.next().getValue() <= System.currentTimeMillis()) {
				values.remove();
			}
		}
	}
}