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
package org.azentreprise.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.azentreprise.Debug;

public abstract class Parseable {
	
	public Parseable(String serialized) {}
	
	protected abstract String serialize();
	
	public static <T extends Parseable> List<T> parse(List<String> source, Class<T> clazz) {	
		List<T> output = new ArrayList<>();
		
		for(String serialized : source) {
			try {
				output.add(clazz.getDeclaredConstructor(String.class).newInstance(serialized));
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
		
		return output;
	}
	
	public static <T extends Parseable> Map<String, T> parse(Map<String, String> source, Class<T> clazz) {
		Map<String, T> output = new HashMap<>();
		
		for(Entry<String, String> entry : source.entrySet()) {
			try {
				output.put(entry.getKey(), clazz.getDeclaredConstructor(String.class).newInstance(entry.getValue()));
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
		
		return output;
	}
	
	public static void serialize(List<? extends Parseable> source, List<String> target) {
		target.clear();
		
		for(Parseable parseable : source) {
			try {
				target.add(parseable.serialize());
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
	}
	
	public static void serialize(Map<String, ? extends Parseable> source, Map<String, String> target) {
		target.clear();
		
		for(Entry<String, ? extends Parseable> parseable : source.entrySet()) {
			try {
				target.put(parseable.getKey(), parseable.getValue().serialize());
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
	}
}
