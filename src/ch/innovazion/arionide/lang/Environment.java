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
package ch.innovazion.arionide.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;

public abstract class Environment {

	private final AtomicLong programCounter = new AtomicLong();
	
	private final List<Peripheral> peripherals = new ArrayList<>();
	
	protected void registerPeripheral(Peripheral peripheral) {
		peripherals.add(peripheral);
	}
	
	public void sample() {
		peripherals.forEach(Peripheral::sample);
	}
	
	public Container create(AppManager manager, Bounds renderBounds) {
		LayoutManager layoutManager = new LayoutManager(manager.getDrawingContext(), manager.getEventDispatcher());
		
		Container container = new Container(null, layoutManager);
		
		Point firstPoint = renderBounds.getFirstPoint();
		Point secondPoint = renderBounds.getSecondPoint();
		
		layoutManager.register(container, null, firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
		
		float height = 1.0f / peripherals.size();
		float x = 0.0f;
		
		for(Peripheral peripheral : peripherals) {
			Container display = new Container(container, layoutManager);

			container.add(new Label(container, peripheral.getUID()), x, 0.0f, x + 0.2f * height, 1.0f);
			container.add(display, x + 0.2f * height, 0.0f, x + height, 1.0f);
			
			x += height;
			
			peripheral.createDisplay(display);
		}
		
		layoutManager.compute();
		layoutManager.apply();
		
		return container;
	}
	
	public AtomicLong getProgramCounter() {
		return programCounter;
	}
	
	public abstract Language getLanguage();
}
