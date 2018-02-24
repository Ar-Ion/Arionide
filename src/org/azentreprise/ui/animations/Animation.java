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
package org.azentreprise.ui.animations;

import java.util.function.Consumer;

import org.azentreprise.Arionide;
import org.azentreprise.Tickable;

public abstract class Animation extends Tickable {
	
	private int duration;
	private Consumer<Animation> animateAfter;
	
	public Animation() {
		Arionide.registerTickable(this);
	}
	
	public void startAnimation(int duration, Consumer<Animation> animateAfter, Object... params) {
		this.startAnimation(duration, params);
		this.animateAfter = animateAfter;
	}
	
	public void startAnimation(int duration, Object... params) {
		this.stopAnimation();
		this.tickIgnore = false;
		this.resetTickCounter();
		this.duration = duration;
	}
	
	public void stopAnimation() {
		this.tickIgnore = true;
		
		if(this.animateAfter != null) {
			Consumer<Animation> clone = this.animateAfter;
			this.animateAfter = null;
			clone.accept(this);
		}
	}
	
	protected int getDuration() {
		return this.duration;
	}
	
	protected double getProgression() {
		return (double) this.getTickID() / this.duration;
	}

	public int getTicksBetweenExecutions() {
		return 1;
	}
	
	public String getTickableDescriptor() {
		return "a user interface animation";
	}
}
