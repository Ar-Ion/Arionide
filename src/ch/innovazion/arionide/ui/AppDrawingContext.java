/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui;

import ch.innovazion.arionide.Workspace;
import ch.innovazion.arionide.resources.Resources;
import ch.innovazion.arionide.threading.Purgeable;
import ch.innovazion.arionide.ui.core.CoreRenderer;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.render.PrimitiveRenderingSystem;
import ch.innovazion.arionide.ui.render.font.FontRenderer;
import ch.innovazion.arionide.ui.topology.Size;

public interface AppDrawingContext extends Purgeable {
	
	public static final double MOUSE_WHEEL_SENSIBILITY = 1.0d;
	
	public void load(Workspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager);
	public void draw();
	public void update();
	public Size getWindowSize();
	public FontRenderer getFontRenderer();
	public PrimitiveRenderingSystem getRenderingSystem();
	public PrimitiveRenderingSystem getOverlayRenderingSystem();
	public Resources getResources();
	public void setCursorVisible(boolean visible);
	public void moveCursor(int x, int y);
}