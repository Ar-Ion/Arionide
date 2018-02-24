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
package org.azentreprise.arionide.ui.overlay;

import java.util.Stack;

import org.azentreprise.arionide.ui.AppDrawingContext;

public class AlphaLayeringSystem {
	
	private final AppDrawingContext context;
	private final Object[] layers = new Object[AlphaLayer.values().length];

	public AlphaLayeringSystem(AppDrawingContext context) {
		this.context = context;
		
		for(int i = 0; i < this.layers.length; i++) {
			Stack<Integer> layer = new Stack<>();
			layer.push(0xFF);
			this.layers[i] = layer;
		}
	}
	
	/* Returns the resulting alpha value */
	@SuppressWarnings("unchecked")
	public int push(AlphaLayer layer, int value) {
		((Stack<Integer>) this.layers[layer.ordinal()]).push(value);
		return this.commit();
	}
	
	/* Returns the resulting alpha value */
	@SuppressWarnings("unchecked")
	public int pop(AlphaLayer layer) {		
		((Stack<Integer>) this.layers[layer.ordinal()]).pop();
		return this.commit();
	}
	
	@SuppressWarnings("unchecked")
	private int commit() {
		int alpha = 0xFF;
		
		for(int i = 0; i < this.layers.length; i++) {
			alpha *= ((Stack<Integer>) this.layers[i]).lastElement();
			alpha /= 255;
		}
		
		this.context.setAlpha(alpha);
		
		return alpha;
	}
}