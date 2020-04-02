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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;

public class Node implements ParameterValue {

	private static final long serialVersionUID = 4195840205787242630L;
	
	private final Map<String, Node> symbolicMap = new HashMap<>();
	private final List<Node> linearMap = new ArrayList<>();
		
	private int size = 0;
	private String name;
	private String path;
	private Node parent;
	
	public Node() {
		label(null);
	}

	public synchronized void connect(Node value) throws SymbolResolutionException {
		connect(value, linearMap.size());
	}
	
	public synchronized void connect(Node value, int position) throws SymbolResolutionException {
		if(value != null) {
			if(!symbolicMap.containsKey(value.name)) {
				value.parent = this;
				
				linearMap.add(position, value);
				symbolicMap.put(value.name, value);

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
	
	public synchronized void disconnect(Node value) throws SymbolResolutionException {
		if(value != null) {
			value.parent = null;
			
			linearMap.remove(value);
			symbolicMap.remove(value.name);
			
			int oldSize = size;
			size -= value.size;
			notifySizeUpdate(oldSize);
		} else {
			throw new SymbolResolutionException("Unable to disconnect an empty value from " + String.join("; ", getDisplayValue()));
		}
	}
	
	public synchronized Node resolve(int id) throws SymbolResolutionException {
		if(id >= 0 && id < linearMap.size()) {
			return linearMap.get(id);
		} else {
			throw new SymbolResolutionException("Symbol identifier '" + id + "' is out of range");
		}
	}
	
	public synchronized Node resolve(String label) throws SymbolResolutionException {
		Node info = symbolicMap.get(label);
		
		if(info != null) {
			return info;
		} else {
			throw new SymbolResolutionException("Symbol label '" + label + "' could not be resolved");
		}
	}
	
	public synchronized int indexOf(Node info) throws SymbolResolutionException {
		int index = linearMap.indexOf(info);
		
		if(index >= 0) {
			return index;
		} else {
			throw new SymbolResolutionException("Symbol '" + info.getPath() + "' could not be resolved from '" + path + "'");
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
			parent.symbolicMap.remove(oldLabel);
			parent.symbolicMap.put(name, this);
		}
	}
	
	private void notifyPathUpdate() {
		this.path = computePath();
		
		for(Node child : linearMap) {
			child.notifyPathUpdate();
		}
	}
	
	public void label(String name) {
		String oldName = this.name;
		this.name = (name != null && !name.isEmpty()) ? name : "lambda";
		this.path = computePath();
		notifyLabelUpdate(oldName);
		notifyPathUpdate();
	}
	
	private String computePath() {
		Stack<String> stack = new Stack<>();
		
		Node current = this;
		
		do {
			stack.add(current.name);
			current = current.parent;
		} while(current != null);
		
		return stack.stream().reduce((a, b) -> a + "->" + b).orElse("<mysterious information path>");
	}
	
	public String getLabel() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getNumElements() {
		return linearMap.size();
	}

	public List<Node> getNodes() {
		return Collections.unmodifiableList(linearMap);
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Bit[] contract() {
		return getRawStream().toArray(Bit[]::new);
	}
	
	public List<String> getDisplayValue() {
		return Arrays.asList(toString());
	}
	
	public String toString() {
		return getLabel() + " (" + getNumElements() + " element(s))";
	}
	 
	protected Stream<Bit> getRawStream() {
		return linearMap.stream().map(Node::getRawStream).reduce(Stream::concat).orElse(Stream.of());
	}
	
	public Node clone() {
		Node clone = new Node();
		
		for(Node child : linearMap) {
			Node childClone = child.clone();
			
			clone.linearMap.add(childClone);
			clone.symbolicMap.put(childClone.name, childClone);
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