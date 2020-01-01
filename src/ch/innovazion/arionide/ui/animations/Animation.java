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
package ch.innovazion.arionide.ui.animations;

import java.util.function.Consumer;

import ch.innovazion.arionide.ui.AppManager;

public abstract class Animation {
		
	private long startTime;
	private long stopTime;
	private boolean ignoreTicks = true;
	
	private Consumer<Animation> animateAfter;
	
	public Animation(AppManager manager) {
		manager.registerAnimation(this);
	}
	
	public void startAnimation(int duration, Consumer<Animation> animateAfter, Object... params) {
		this.startAnimation(duration, params);
		this.animateAfter = animateAfter;
	}
	
	public void startAnimation(int duration, Object... params) {
		this.stopAnimation();
		
		long now = System.currentTimeMillis();
		
		this.startTime = now;
		this.stopTime = now + duration;
		
		this.ignoreTicks = false;
	}
	
	public void endAnimation() {
		this.ignoreTicks = true;
		
		if(this.animateAfter != null) {
			Consumer<Animation> clone = this.animateAfter;
			this.animateAfter = null;
			clone.accept(this);
		}
	}
	
	public void stopAnimation() {
		this.ignoreTicks = true;
	}
	
	public boolean isAnimating() {
		return this.stopTime > System.currentTimeMillis();
	}
	
	protected double getProgression() {
		return (double) (System.currentTimeMillis() - this.startTime) / (this.stopTime - this.startTime);
	}
	
	public void doTick() {
		if(!this.ignoreTicks) {
			this.tick();
		}
	}
	
	public abstract void tick();
}