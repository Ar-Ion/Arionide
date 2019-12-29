/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.ui.core;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.core.geom.Geometry;
import ch.innovazion.arionide.ui.topology.Bounds;

public interface CoreRenderer {
	
	@IAm("updating the core scene")
	public void update(Bounds bounds);
	
	@IAm("rendering the core scene")
	public void render3D(AppDrawingContext context);
	
	@IAm("rendering extra gui 2D")
	public void render2D(AppDrawingContext context);
	
	@IAm("changing the core scene")
	public void setScene(RenderingScene scene);
	
	@IAm("selecting an object")
	public void select(int id);

	@IAm("teleporting to an element")
	public void teleport(TeleportInfo info);
	
	public void requestFullReconstruction();
	
	public boolean isActive();
	public Geometry getCodeGeometry();
	public Geometry getStructuresGeometry();
}