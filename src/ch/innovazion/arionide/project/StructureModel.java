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

import ch.innovazion.arionide.lang.symbols.Parameter;

public class StructureModel {
						
	private final String uniqueName;
	private final List<Parameter> parameters;
	private final List<String> comment;
	private final int colorID;
	private final int spotColorID;

	StructureModel(String uniqueName, List<Parameter> parameters, List<String> comment, int colorID, int spotColorID) {
		this.uniqueName = uniqueName;
		this.parameters = parameters;
		this.comment = comment;
		this.colorID = colorID;
		this.spotColorID = spotColorID;
	}

	public String getUniqueName() {
		return uniqueName;
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
		return "[Name: " + uniqueName + "; Comment: " + comment + "; Parameters: [" + parameters + "]]";
	}
}