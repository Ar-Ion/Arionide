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
import java.util.stream.Stream;

public class Node implements ParameterValue {

	private static final long serialVersionUID = 4195840205787242630L;
	
	private final Map<String, Node> symbolicMap = new HashMap<>();
	private final List<Node> linearMap = new ArrayList<>();
		
	private int size = 0;
	private String name;
	private String path;
	private Node parent;

	public Node(String name) {
		label(name);
	}
	
	protected Node(Node parent) {		
		for(Node child : parent.linearMap) {
			Node childClone = child.clone();
			
			linearMap.add(childClone);
			symbolicMap.put(childClone.name, childClone);
			childClone.parent = this;
		}
		
		this.size = parent.size;
		this.name = parent.name;
		this.path = parent.path;
		this.parent = null;
	}
	
	public synchronized Node connect(Node value) throws SymbolResolutionException {
		connect(value, linearMap.size());
		return this;
	}
	
	public synchronized Node connect(Node value, int position) throws SymbolResolutionException {
		if(value != null) {
			if(!symbolicMap.containsKey(value.name)) {
				value.parent = this;
				
				linearMap.add(position, value);
				symbolicMap.put(value.name, value);

				int oldSize = size;
				size += value.getSize();
				notifySizeUpdate(oldSize);
				
				value.notifyPathUpdate();
			} else {
				throw new SymbolResolutionException("Information label '" + value.name + "' is already present in " + String.join("; ", getDisplayValue()));
			}
		} else {
			throw new SymbolResolutionException("Unable to connect an empty value to " + String.join("; ", getDisplayValue()));
		}
		
		return this;
	}
	
	public synchronized Node disconnect(Node value) throws SymbolResolutionException {
		if(value != null) {
			value.parent = null;
			
			linearMap.remove(value);
			symbolicMap.remove(value.name);
			
			int oldSize = size;
			size -= value.getSize();
			
			notifySizeUpdate(oldSize);
		} else {
			throw new SymbolResolutionException("Unable to disconnect an empty value from " + String.join("; ", getDisplayValue()));
		}
		
		return this;
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
	
	public synchronized boolean has(String label) {
		return symbolicMap.containsKey(label);
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
	
	public Node label(String name) {
		String oldName = this.name;
		this.name = (name != null && !name.isEmpty()) ? name : "lambda";
		this.path = computePath();
		notifyLabelUpdate(oldName);
		notifyPathUpdate();
		
		return this;
	}
	
	private String computePath() {
		List<String> path = new ArrayList<>();
		
		Node current = this;
		
		do {
			path.add(current.name);
			current = current.parent;
		} while(current != null);
		
		Collections.reverse(path);
		
		return path.stream().reduce((a, b) -> a + " -> " + b).orElse("<mysterious information path>");
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
		return Arrays.asList(getLabel() + " (" + getNumElements() + " element(s), " + size + " bit(s))");
	}
	
	public String toString() {
		return String.join(", ", getDisplayValue());
	}
	 
	protected Stream<Bit> getRawStream() {
		return linearMap.stream().map(Node::getRawStream).reduce(Stream::concat).orElse(Stream.of());
	}
	
	public Node clone() {
		return new Node(this);
	}
	
	public void parse(String value) throws InvalidValueException {
		throw new InvalidValueException("Cannot parse an input for an 'Information' object");
	}
}