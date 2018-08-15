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
package org.azentreprise.arionide.ui.render;

import org.azentreprise.arionide.ui.AWTContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;

public class AWTPrimitiveRenderer implements PrimitiveRenderer {
		
	public void init(AppDrawingContext context) {
		assert context instanceof AWTContext;
	}

	@Override
	public void drawRect(Bounds bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillRect(Bounds bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRoundRect(Bounds bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillRoundRect(Bounds bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLine(float x1, float y1, float x2, float y2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point drawText(String text, Bounds bounds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawCursor() {
		// TODO Auto-generated method stub
		
	}
	

}