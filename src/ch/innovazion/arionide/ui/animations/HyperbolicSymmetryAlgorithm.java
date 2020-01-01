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

public class HyperbolicSymmetryAlgorithm implements TransformationAlgorithm {
	
	private final double base;
	private final double scaling;
	private final double factor;
	
	public HyperbolicSymmetryAlgorithm(double base, double scaling) {
		this.base = base;
		this.scaling = scaling;
		this.factor = 0.5d / this.computeWithoutVectorTranslation(0.5d, 1.0d);
	}
	
	public double compute(double x) {
		if(x < 0.5d) {
			return 0.5d - this.computeWithoutVectorTranslation(-x + 0.5d, this.factor);
		} else {
			return 0.5d + this.computeWithoutVectorTranslation(x - 0.5d, this.factor);
		}
	}
	
	private double computeWithoutVectorTranslation(double x, double factor) { // preserves function parity
		return factor * (1.0d - Math.pow(this.base, -this.scaling * x));
	}
}