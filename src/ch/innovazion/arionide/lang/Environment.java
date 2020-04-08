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

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;

public abstract class Environment {

	private final long frequency;
	
	private final AtomicLong programCounter = new AtomicLong();
	private final AtomicLong clock = new AtomicLong();
	private final AtomicLong timer = new AtomicLong();

	private final List<Peripheral> peripherals = new ArrayList<>();
	
	private Label pcLabel;
	private Label clkLabel;
	private Label timLabel;

	protected Environment(long frequency) {
		this.frequency = frequency;
	}
	
	protected void registerPeripheral(Peripheral peripheral) {
		peripherals.add(peripheral);
	}
	
	public void sample() {
		peripherals.forEach(Peripheral::sample);
		
		if(pcLabel != null) {
			pcLabel.setLabel("Program Counter: 0x" + Long.toHexString(programCounter.get()));
		}
		
		if(clkLabel != null) {
			clkLabel.setLabel("Clock: " + clock + " [ticks] (" + (1000.0f * clock.get() / frequency) + "ms)");
		}
		
		if(timLabel != null) {
			timLabel.setLabel("Timer: " + timer + " [ticks] (" + (1000.0f * timer.get() / frequency) + "ms)");
		}
	}
		
	public Container create(AppManager manager, Bounds renderBounds, LayoutManager dedicatedLayoutManager) {				
		Container container = new Container(null, dedicatedLayoutManager) {
			public void drawComponents(AppDrawingContext context) {
				sample();
				super.drawComponents(context);
			}
			
			public AppManager getAppManager() {
				return manager;
			}
		};
		
		Point firstPoint = renderBounds.getFirstPoint();
		Point secondPoint = renderBounds.getSecondPoint();
		
		dedicatedLayoutManager.register(container, null, firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
		
		float height = 0.9f / peripherals.size();
		float y = 0.1f;
		
		container.add(this.pcLabel = new Label(container, new String()), 0.0f, 0.0f, 0.3f, 0.1f);
		container.add(this.clkLabel = new Label(container, new String()), 0.4f, 0.0f, 0.6f, 0.1f);
		container.add(this.timLabel = new Label(container, new String()), 0.7f, 0.0f, 0.9f, 0.1f);
				
		for(Peripheral peripheral : peripherals) {
			Container display = new Container(container, dedicatedLayoutManager);

			container.add(new Label(container, peripheral.getUID()), y, 0.0f, y + 0.2f * height, 1.0f);
			container.add(display, 0.0f, y + 0.2f * height, 1.0f, y + height);
			
			y += height;
			
			peripheral.createDisplay(display);
		}
		
		pcLabel.setVisible(true);
		clkLabel.setVisible(true);
		timLabel.setVisible(true);
						
		return container;
	}
	
	public AtomicLong getProgramCounter() {
		return programCounter;
	}
	
	public AtomicLong getClock() {
		return clock;
	}
	
	public abstract Language getLanguage();
}
