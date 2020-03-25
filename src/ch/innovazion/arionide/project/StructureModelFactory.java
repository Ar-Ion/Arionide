package ch.innovazion.arionide.project;

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.ui.ApplicationTints;

public class StructureModelFactory {
	
	public static IncompleteModel draft(String name) {
		return new IncompleteModel(name);
		
	}
	
	public static class IncompleteModel {
		
		private final String name;
		
		private List<Parameter> parameters = new ArrayList<>();
		private List<String> comment = new ArrayList<>();
		private int colorID = ApplicationTints.getColorIDByName("White");
		private int spotColorID = ApplicationTints.getColorIDByName("White");
		
		private IncompleteModel(String name) {
			this.name = name;
		}
		
		public void withParameter(Parameter param) {
			parameters.add(param);
		}
		
		public void withComment(String line) {
			comment.add(line);
		}
		
		public void withColor(int id) {
			this.colorID = id;
		}
		
		public void withColor(float familyFactor) {
			this.colorID = (int) ((ApplicationTints.getColorNames().size() - 1) * familyFactor);
		}
		
		public void withSpotColor(int id) {
			this.spotColorID = id;
		}
		
		public StructureModel build() {
			return new StructureModel(name, parameters, comment, colorID, spotColorID);
		}
	}
}
