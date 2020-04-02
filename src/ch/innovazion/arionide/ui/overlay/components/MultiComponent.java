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
package ch.innovazion.arionide.ui.overlay.components;

import java.util.List;
import java.util.function.Function;

import ch.innovazion.arionide.ui.overlay.Component;
import ch.innovazion.arionide.ui.overlay.View;

public abstract class MultiComponent extends Component {
	
	private List<Component> components;
	
	public MultiComponent(View parent, List<Component> components) {
		super(parent);
		
		assert components.size() > 0;

		this.components = components;
	}
	
	public List<Component> getComponents() {
		return this.components;
	}
	
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
	private Label generate(String str) {
		if(str != null) {
			Label label = new Label(this.getParent(), str);
			label.load();
			return label;
		} else {
			return null;
		}
	}
	
	public Function<String, Component> getGenerator() {
		return this::generate;
	}
}