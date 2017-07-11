/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.animations;

import org.azentreprise.arionide.Utils;

public class ParametricSmoothingAlgorithm implements TransformationAlgorithm {

	private final double smoothingConstant;
	
	public ParametricSmoothingAlgorithm(double smoothingLevel) {		
		assert smoothingLevel > 1.0d; // if less than one it becomes an anti-smoothing algorithm and if equal to one, it becomes like the NoTransformationAlgorithm.
		
		this.smoothingConstant = 1.0d / smoothingLevel;
	}

	public double compute(double x) {
		return (Utils.fakeComplexPower(2*x - 1, this.smoothingConstant) + 1) / 2.0d;
	}

}