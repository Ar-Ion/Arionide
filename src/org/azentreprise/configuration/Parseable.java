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
package org.azentreprise.configuration;

import java.util.ArrayList;
import java.util.List;

import org.azentreprise.Debug;

public abstract class Parseable {
	
	public Parseable(String serialized) {}
	
	protected abstract String serialize();
	
	public static <T> List<T> parse(List<String> source, Class<T> clazz) {	
		List<T> output = new ArrayList<T>();
		
		for(String serialized : source) {
			try {
				output.add(clazz.getDeclaredConstructor(String.class).newInstance(serialized));
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
}
