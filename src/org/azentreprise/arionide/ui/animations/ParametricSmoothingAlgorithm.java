/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.ui.animations;

public class ParametricSmoothingAlgorithm implements SimpleTransformationAlgorithm {

	private final double smoothingConstant;
	
	public ParametricSmoothingAlgorithm(double smoothingLevel) {		
		assert smoothingLevel > 1.0d; // if less than one it becomes an anti-smoothing algorithm and if equal to one, it is like the NoTransformationAlgorithm.
		
		this.smoothingConstant = 1.0d / smoothingLevel;
	}

	public double compute(double x) {
		return (this.pseudoRoot(2*x - 1) + 1) / 2;
	}
	
	private double pseudoRoot(double x) {
		double root = Math.pow(Math.abs(x), this.smoothingConstant);
		return x > 0.0d ? root : -root;
	}
}