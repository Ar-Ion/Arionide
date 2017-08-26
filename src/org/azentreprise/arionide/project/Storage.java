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
package org.azentreprise.arionide.project;

import java.util.List;

import org.azentreprise.arionide.debugging.IAm;

public abstract class Storage {
	
	protected List<StructureElement> hierarchy;
	protected List<StructureElement> inheritance;
	protected List<StructureElement> callGraph;
	protected List<HistoryElement> history;
	protected List<DataElement> currentData;
	
	public List<StructureElement> getHierarchy() {
		return this.hierarchy;
	}
	
	public List<StructureElement> getInheritance() {
		return this.inheritance;
	}
	
	public List<StructureElement> getCallGraph() {
		return this.callGraph;
	}
	
	public List<HistoryElement> getHistory() {
		return this.history;
	}
	
	public List<DataElement> getCurrentData() {
		return this.currentData;
	}
	
	@IAm("loading the hierarchy")
	public abstract void loadHierarchy();
	@IAm("saving the hierarchy")
	public abstract void saveHierarchy();
	
	@IAm("loading the inheritance")
	public abstract void loadInheritance();
	@IAm("saving the inheritance")
	public abstract void saveInheritance();
	
	@IAm("loading the call graph")
	public abstract void loadCallGraph();
	@IAm("saving the call graph")
	public abstract void saveCallGraph();
	
	@IAm("loading the history")
	public abstract void loadHistory();
	@IAm("saving the history")
	public abstract void saveHistory();
	
	@IAm("loading data")
	public abstract void loadData(int id);
	@IAm("saving data")
	public abstract void saveData();
}