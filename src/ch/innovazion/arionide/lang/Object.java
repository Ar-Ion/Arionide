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
package ch.innovazion.arionide.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import ch.innovazion.arionide.lang.natives.NativeDataCommunicator;
import ch.innovazion.arionide.lang.natives.NativeTypes;

public class Object {
	
	private final List<Integer> structure;
	private final List<Object> data = new ArrayList<>();
	private final String id;
	private int index = 0;
	
	public Object(List<Integer> structure, String id) {
		this.structure = structure;
		this.id = id;
	}
	
	public void add(String value, NativeDataCommunicator communicator) {
		switch(this.structure.get(this.index++ % this.structure.size())) {
			case NativeTypes.INTEGER:
				this.data.add(new IntegerObject(value, value));
				break;
			case NativeTypes.TEXT:
				this.data.add(new TextObject(value, value));
				break;
			case NativeTypes.STRUCTURE:
				Object obj = communicator.getObject(value);
				
				if(obj != null) {
					this.data.add(obj);
				} else {
					communicator.exception("The object " + value + " is not valid anymore.");
				}
		}
	}
	
	public void setValue(String value) {
		;
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getValue() {
		return this.getID();
	}
	
	public boolean isConsistent() {
		return this.index % this.structure.size() == 0;
	}
	
	public Bit[] getData() {
		if(this.data.size() > 0) {
			return this.data.stream().map(Object::getDataStream).reduce(Stream::concat).orElse(Stream.of()).toArray(Bit[]::new);
		} else {
			return new Bit[0];
		}
	}
	
	private Stream<Bit> getDataStream() {
		System.out.println(this.data + " -> " + this.getData());
		return Arrays.stream(this.getData());
	}
	
	public int getSize() {
		return this.getData().length;
	}

	public List<Object> getObjects() {
		return this.data;
	}
}