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

import java.util.Map;

import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Node;

public class ApplicationMemory {
	
	private final long size;
	private final Map<Long, Callable> text;
	private final Map<Long, Node> data;

	public ApplicationMemory(long size, Map<Long, Callable> text, Map<Long, Node> data) {
		this.size = size;
		this.text = text;
		this.data = data;
		
		// dump();
	}
	
	public Callable textAt(long address) throws EvaluationException {
		if(text.containsKey(address)) {
			return text.get(address);
		} else {
			throw new EvaluationException("Text segmentation fault at 0x" + Long.toHexString(address));
		}
	}
	
	public Node dataAt(long address) throws EvaluationException {
		if(data.containsKey(address)) {
			return data.get(address);
		} else {
			throw new EvaluationException("Data segmentation fault at 0x" + Long.toHexString(address));	
		}
	}
	
	public void dump() {
		for(long i = 0; i < size; i++) {
			Object obj = text.get(i);
			
			if(obj == null) {
				obj = data.get(i);
			}
			
			if(obj != null) {
				System.out.println(obj);
			}
		}
	}
}
