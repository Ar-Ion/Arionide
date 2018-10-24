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
package org.azentreprise.arionide.ui.core.awt;

import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AWTContext;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.core.TeleportInfo;
import org.azentreprise.arionide.ui.core.geom.Geometry;
import org.azentreprise.arionide.ui.topology.Bounds;

public class AWTCoreRenderer implements CoreRenderer {
	public void render3D(AppDrawingContext context) {
		assert context instanceof AWTContext;
	}

	@Override
	public void setScene(RenderingScene scene) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadProject(Project project) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Bounds bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectInstruction(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teleport(TeleportInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render2D(AppDrawingContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Geometry getStructuresGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry getCodeGeometry() {
		// TODO Auto-generated method stub
		return null;
	}
}