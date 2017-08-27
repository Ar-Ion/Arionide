package org.azentreprise.arionide.project;

import java.io.Serializable;

public class StructureMeta implements Serializable {
	private static final long serialVersionUID = 5582101421258702064L;
	
	private String name = "Lambda";
	private int colorID = 0;
	
	public String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public int getColorID() {
		return this.colorID;
	}
	
	protected void setColorID(int colorID) {
		this.colorID = colorID;
	}
}