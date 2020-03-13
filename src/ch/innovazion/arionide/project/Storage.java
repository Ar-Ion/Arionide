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
package ch.innovazion.arionide.project;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableHistoryElement;
import ch.innovazion.arionide.project.mutables.MutableInheritanceElement;
import ch.innovazion.arionide.project.mutables.MutableStructure;

public abstract class Storage {
	
	private List<MutableHierarchyElement> hierarchy;
	private Map<Integer, MutableInheritanceElement> inheritance;
	private List<MutableHierarchyElement> callGraph;
	private Map<Integer, MutableStructure> structMeta;
	private List<MutableHistoryElement> history;
	private Map<Integer, MutableCodeChain> code;
	
	
	public List<HierarchyElement> getHierarchy() {
		return Collections.unmodifiableList(this.hierarchy);
	}
	
	public Map<Integer, InheritanceElement> getInheritance() {
		return Collections.unmodifiableMap(this.inheritance);
	}
	
	public List<HierarchyElement> getCallGraph() {
		return Collections.unmodifiableList(this.callGraph);
	}
	
	public Map<Integer, Structure> getStructureMeta() {
		return Collections.unmodifiableMap(this.structMeta);
	}
	
	public List<HistoryElement> getHistory() {
		return Collections.unmodifiableList(this.history);
	}
	
	public Map<Integer, CodeChain> getCode() {
		return Collections.unmodifiableMap(this.code);
	}
	
	
	protected List<MutableHierarchyElement> getMutableHierarchy() {
		return this.hierarchy;
	}
	
	protected Map<Integer, MutableInheritanceElement> getMutableInheritance() {
		return this.inheritance;
	}
	
	protected List<MutableHierarchyElement> getMutableCallGraph() {
		return this.callGraph;
	}
	
	protected Map<Integer, MutableStructure> getMutableStructureMeta() {
		return this.structMeta;
	}
	
	protected List<MutableHistoryElement> getMutableHistory() {
		return this.history;
	}

	protected Map<Integer, MutableCodeChain> getMutableCode() {
		return this.code;
	}
	
	
	protected void setMutableHierarchy(List<MutableHierarchyElement> hierarchy) {
		this.hierarchy = hierarchy;
	}
	
	protected void setMutableInheritance(Map<Integer, MutableInheritanceElement> inheritance) {
		this.inheritance = inheritance;
	}
	
	protected void setMutableCallGraph(List<MutableHierarchyElement> callGraph) {
		this.callGraph = callGraph;
	}
	
	protected void setMutableStructureMeta(Map<Integer, MutableStructure> structMeta) {
		this.structMeta = structMeta;
	}
	
	protected void setMutableHistory(List<MutableHistoryElement> history) {
		this.history = history;
	}
	
	protected void setMutableCode(Map<Integer, MutableCodeChain> code) {
		this.code = code;
	}
	
	
	@IAm("loading the hierarchy")
	public abstract void loadHierarchy() throws StorageException;
	@IAm("saving the hierarchy")
	public abstract void saveHierarchy() throws StorageException;
	
	@IAm("loading the inheritance")
	public abstract void loadInheritance() throws StorageException;
	@IAm("saving the inheritance")
	public abstract void saveInheritance() throws StorageException;
	
	@IAm("loading the call graph")
	public abstract void loadCallGraph() throws StorageException;
	@IAm("saving the call graph")
	public abstract void saveCallGraph() throws StorageException;
	
	@IAm("loading the history")
	public abstract void loadHistory() throws StorageException;
	@IAm("saving the history")
	public abstract void saveHistory() throws StorageException;
	
	@IAm("loading the structures meta data")
	public abstract void loadStructureMeta() throws StorageException;
	@IAm("saving the structures meta data")
	public abstract void saveStructureMeta() throws StorageException;
	
	@IAm("loading data")
	public abstract void loadCode() throws StorageException;
	@IAm("saving data")
	public abstract void saveCode() throws StorageException;
}