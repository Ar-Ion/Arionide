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

import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.ui.animations.Animation;
import ch.innovazion.arionide.ui.layout.Surface;

public class Transition {
	
	public static Transition slowReplace = new Transition(255, 0, 1000);
	public static Transition replace = new Transition(255, 0, 500);
	public static Transition fade = new Transition(255, 63, 500);
	public static Transition none = new Transition(255, 0, 0);
	
	private final int activeOpacity;
	private final int inactiveOpacity;
	private final int duration;
	
	private Transition(int activeOpacity, int inactiveOpacity, int duration) {
		this.activeOpacity = activeOpacity;
		this.inactiveOpacity = inactiveOpacity;
		this.duration = duration;
	}
	
	private void enable(Component component) {
		component.setEnabled(true);
	}
	
	private void show(Surface surface) {
		surface.setVisible(true);
	}
	
	private void disable(Component component) {
		component.setEnabled(false);
	}
	
	private void hide(Surface surface) {
		surface.setVisible(false);
	}
	
	public void show(View view, Animation animation) {
		view.getComponents().forEach(this::enable);
		view.getComponents().forEach(this::show);

		view.viewWillAppear();
		show(view);
		
		animation.startAnimation(duration, activeOpacity);
	}
	
	public void hide(View view, Animation animation) {
		// Async to avoid a dead lock on the event dispatcher (flush)
		new Thread(() -> {
			IEventDispatcher dispatcher = view.getAppManager().getEventDispatcher();
			
			dispatcher.flush();
			dispatcher.pause();
			
			view.getComponents().forEach(this::disable);
			view.viewWillDisappear();
			
			animation.startAnimation(duration, after -> {
				if(inactiveOpacity == 0) {
					view.getComponents().forEach(this::hide);
					hide(view);
				}
								
				dispatcher.resume();
			}, inactiveOpacity);
		}).start();
	}
}
