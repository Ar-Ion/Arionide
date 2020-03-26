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

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.ui.ApplicationTints;

public class StructureModelFactory {
	
	public static IncompleteModel draft(String uniqueName) {
		return new IncompleteModel(uniqueName);
		
	}
	
	public static class IncompleteModel {
		
		private final String uniqueName;
		
		private List<Parameter> parameters = new ArrayList<>();
		private List<String> comment = new ArrayList<>();
		private int colorID = ApplicationTints.getColorIDByName("White");
		private int spotColorID = ApplicationTints.getColorIDByName("White");
		
		private IncompleteModel(String uniqueName) {
			this.uniqueName = uniqueName;
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
			return new StructureModel(uniqueName, parameters, comment, colorID, spotColorID);
		}
	}
}
