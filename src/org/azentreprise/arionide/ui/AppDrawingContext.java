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
package org.azentreprise.arionide.ui;

import java.awt.Cursor;

import org.azentreprise.arionide.Workspace;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.threading.Purgeable;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.render.PrimitiveRenderingSystem;
import org.azentreprise.arionide.ui.render.font.FontRenderer;
import org.azentreprise.arionide.ui.topology.Size;

public interface AppDrawingContext extends Purgeable {
	
	public static final double MOUSE_WHEEL_SENSIBILITY = 1.0d;
	
	public void load(Workspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager);
	public void draw();
	public void update();
	public Size getWindowSize();
	public FontRenderer getFontRenderer();
	public PrimitiveRenderingSystem getRenderingSystem();
	public Resources getResources();
	public void setCursor(Cursor cursor);
	public void moveCursor(int x, int y);
}