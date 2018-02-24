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
package org.azentreprise.ui.views.project;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.azentreprise.Debug;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.lang.ObjectType;

public class CodeStructureSpawner implements CodeObjectSpawner 	{
	public void loadCell(Point2D point) {
		String name = JOptionPane.showInputDialog(null, "Enter the name of the new structure", "New structure", JOptionPane.PLAIN_MESSAGE);
		
		if(name != null) {
			Project project = Projects.getCurrentProject();
			
			String realName = project.current + "." + name;
			
			org.azentreprise.lang.Object structure = new org.azentreprise.lang.Object(realName, point, 0, ObjectType.STRUCTURE, true);
			
			project.objects.add(structure);
			project.objectMapping.put(realName, structure);
			
			UIProjectView.simpleRenderer.reset(project);
			UIProjectView.complexRenderer.reset(project);
			
			Debug.info("Creating " + realName);
		}
	}

	public void updateHighlighting(Map<Point2D, Color> cells) {
		List<Point2D> busyCells = CodeObjectSpawner.getBusyCells(Projects.getCurrentProject());
		
		Color notAvailable = new Color(255, 0, 0, 63);
		
		for(Point2D cell : busyCells) {
			cells.put(cell, notAvailable);
			cells.put(new Point2D.Double(cell.getX() + 1, cell.getY()), notAvailable);
			cells.put(new Point2D.Double(cell.getX(), cell.getY() + 1), notAvailable);
			cells.put(new Point2D.Double(cell.getX() - 1, cell.getY()), notAvailable);
			cells.put(new Point2D.Double(cell.getX(), cell.getY() - 1), notAvailable);
		}
	}
}
