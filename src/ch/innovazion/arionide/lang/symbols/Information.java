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
package ch.innovazion.arionide.lang.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Information implements ParameterValue {

	private static final long serialVersionUID = 4195840205787242630L;
	
	private final Map<String, Information> symbolic_map = new HashMap<>();
	private final List<Information> linear_map = new ArrayList<>();
	
	private int size = 0;
	private String name;
	private Information parent;
	
	public Information() {
		label(null);
	}

	public synchronized void connect(Information value, int position) throws SymbolResolutionException {
		if(value != null) {	
			if(!symbolic_map.containsKey(value.name)) {
				value.parent = this;
				
				linear_map.add(position, value);
				symbolic_map.put(value.name, value);

				int oldSize = size;
				size += value.size;
				notifySizeUpdate(oldSize);
			} else {
				throw new SymbolResolutionException("Information label '" + value.name + "' is already present in " + String.join("; ", getDisplayValue()));
			}
		} else {
			throw new SymbolResolutionException("Unable to connect an empty value to " + String.join("; ", getDisplayValue()));
		}
	}
	
	public synchronized void disconnect(Information value) throws SymbolResolutionException {
		if(value != null) {
			value.parent = null;
			
			linear_map.remove(value);
			symbolic_map.remove(value.name);
			
			int oldSize = size;
			size -= value.size;
			notifySizeUpdate(oldSize);
		} else {
			throw new SymbolResolutionException("Unable to disconnect an empty value from " + String.join("; ", getDisplayValue()));
		}
	}
	
	public synchronized Information resolve(int id) throws SymbolResolutionException {
		if(id >= 0 && id < linear_map.size()) {
			return linear_map.get(id);
		} else {
			throw new SymbolResolutionException("Symbol identifier '" + id + "' is out of range");
		}
	}
	
	public synchronized Information resolve(String label) throws SymbolResolutionException {
		Information info = symbolic_map.get(label);
		
		if(info != null) {
			return info;
		} else {
			throw new SymbolResolutionException("Symbol label '" + label + "' could not be resolved");
		}
	}
	
	
	private void notifySizeUpdate(int oldSize) {
		if(parent != null) {
			int parentOldSize = parent.size;
			
			parent.size -= oldSize;
			parent.size += size;
			
			parent.notifySizeUpdate(parentOldSize);
		}
	}
	
	private void notifyLabelUpdate(String oldLabel) {
		if(parent != null) {
			parent.symbolic_map.remove(oldLabel);
			parent.symbolic_map.put(name, this);
		}
	}
	
	public void label(String name) {
		String oldName = this.name;
		this.name = (name != null && !name.isEmpty()) ? name : "Lambda information";
		notifyLabelUpdate(oldName);
	}
	
	public int getSize() {
		return size;
	}

	public Collection<Information> getInformation() {
		return Collections.unmodifiableCollection(linear_map);
	}
	
	public Bit[] contract() {
		return getRawStream().toArray(Bit[]::new);
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList("Lambda Information");
	}
	
	protected Stream<Bit> getRawStream() {
		return linear_map.stream().map(Information::getRawStream).reduce(Stream::concat).orElse(Stream.of());
	}
	
	public Information clone() {
		Information clone = new Information();
		
		for(Information child : linear_map) {
			Information childClone = child.clone();
			
			clone.linear_map.add(childClone);
			clone.symbolic_map.put(childClone.name, childClone);
			childClone.parent = clone;
		}
		
		clone.size = size;
		clone.name = name;
		clone.parent = null;
		
		return clone;
	}
	
	public void parse(String value) throws InvalidValueException {
		throw new InvalidValueException("Cannot parse an input for an 'Information' object");
	}
}