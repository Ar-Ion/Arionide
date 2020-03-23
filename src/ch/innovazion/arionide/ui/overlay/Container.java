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
package ch.innovazion.arionide.ui.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.layout.LayoutManager;

public class Container extends Component {		

	private final LayoutManager layoutManager;
	private final List<Component> components = new ArrayList<>();
	
	public Container(Container parent, LayoutManager layoutManager) {
		super(parent);
		this.layoutManager = layoutManager;
	}
	
	public void load() {
		for(Component component : components) {
			component.load();
		}
	}
	
	public void add(Component component, float x1, float y1, float x2, float y2) {
		components.add(component);
		layoutManager.register(component, this, x1, y1, x2, y2);
		getAppManager().getFocusManager().registerComponent(component);
	}
	
	public void drawSurface(AppDrawingContext context) {
		drawComponents(context);		
	}
	
	protected void drawComponents(AppDrawingContext context) {
		for(Component component : components) {
			component.draw(context);
		}
	}
	
	public void update() {
		for(Component component : components) {
			component.update();
		}
	}
	
	protected List<Component> getComponents() {
		return Collections.unmodifiableList(components);
	}

	public boolean isFocusable() {
		return false;
	}
}
