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
package org.azentreprise.arionide.ui.overlay.components;

import java.awt.geom.Point2D;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.overlay.AlphaLayer;
import org.azentreprise.arionide.ui.render.GLPrimitiveRenderer;
import org.azentreprise.arionide.ui.render.PrimitiveRenderer;

public class OpenGLTabDesign implements TabDesign {
		
	public void enterDesignContext(AppManager manager, Point2D center, double radius) {
		if(radius > 0) {
			PrimitiveRenderer primitives = manager.getDrawingContext().getPrimitives();
			
			assert primitives instanceof GLPrimitiveRenderer;
			
			GLPrimitiveRenderer glPrimitives = (GLPrimitiveRenderer) primitives;
					
			int initialAlpha = (int) (255.0d * glPrimitives.getAlpha());
			manager.getAlphaLayering().pop(AlphaLayer.COMPONENT);
			int preAlpha = (int) (255.0d * glPrimitives.getAlpha());
			manager.getAlphaLayering().push(AlphaLayer.COMPONENT, Utils.fakeDivision(255 * initialAlpha, preAlpha, 255));
			
			glPrimitives.enableLight(center, radius, preAlpha / 255.0d);
		}
	}

	public void exitDesignContext(AppManager manager) {
		PrimitiveRenderer primitives = manager.getDrawingContext().getPrimitives();
		
		assert primitives instanceof GLPrimitiveRenderer;
		
		GLPrimitiveRenderer glPrimitives = (GLPrimitiveRenderer) primitives;
		glPrimitives.disableLight(manager.getDrawingContext());
	}
}