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
package org.azentreprise.arionide.ui.core.awt;

import java.awt.Rectangle;

import org.azentreprise.arionide.Project;
import org.azentreprise.arionide.ui.AWTDrawingContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;

public class AWTCoreRenderer implements CoreRenderer {
	public void render(AppDrawingContext context, Rectangle bounds) {
		assert context instanceof AWTDrawingContext;
		
		AWTDrawingContext awtContext = (AWTDrawingContext) context;
		
		awtContext.setColor(0);
		awtContext.getPrimitives().fillRect(context, bounds);
	}

	@Override
	public void setScene(RenderingScene scene) {
		// TODO Auto-generated method stub
		System.out.println(scene);
		
	}

	@Override
	public void loadProject(Project project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Rectangle bounds) {
		// TODO Auto-generated method stub
		
	}
}