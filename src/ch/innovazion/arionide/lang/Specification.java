/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Specification implements Serializable, Cloneable {
	private static final long serialVersionUID = -7857295906601141122L;
	
	private final int id;
	private final List<SpecificationElement> elements;
	
	public Specification(Specification model) {
		this.id = model.id;
		this.elements = Collections.synchronizedList(model.elements.stream().map(SpecificationElement::clone).collect(Collectors.toList()));
	}
	
	public Specification(int id, SpecificationElement... elements) {
		this.id = id;
		this.elements = Collections.synchronizedList(new ArrayList<>(Arrays.asList(elements)));
	}
	
	public List<SpecificationElement> getElements() {
		return this.elements;
	}
	
	public String toString() {
		return this.elements.stream().map(SpecificationElement::toString).reduce((a, b) -> a + ", " + b).orElse(new String());
	}
	
	public boolean hasSameOrigin(Specification other) {
		return other.id == this.id;
	}
}