package ch.innovazion.arionide.project;

import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.lang.symbols.Parameter;

public class StructureModel {
						
	private final String name;
	private final List<Parameter> parameters;
	private final List<String> comment;
	private final int colorID;
	private final int spotColorID;

	StructureModel(String name, List<Parameter> parameters, List<String> comment, int colorID, int spotColorID) {
		this.name = name;
		this.parameters = parameters;
		this.comment = comment;
		this.colorID = colorID;
		this.spotColorID = spotColorID;
	}

	public String getName() {
		return name;
	}
	
	public List<Parameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public List<String> getComment() {
		return Collections.unmodifiableList(comment);
	}
	
	public int getColorID() {
		return colorID;
	}
	
	public int getSpotColorID() {
		return spotColorID;
	}
	
	public String toString() {
		return "[Name: " + name + "; Comment: " + comment + "; Parameters: [" + parameters + "]]";
	}
}