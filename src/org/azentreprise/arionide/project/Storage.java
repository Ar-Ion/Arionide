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