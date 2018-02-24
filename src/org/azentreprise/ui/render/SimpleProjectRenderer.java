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
package org.azentreprise.ui.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.azentreprise.configuration.Project;
import org.azentreprise.lang.Link;
import org.azentreprise.lang.Object;

public class SimpleProjectRenderer extends ProjectRenderer {
	
	public SimpleProjectRenderer(Project project) {
		super(project);
	}

	public void render(Graphics2D g2d) {
		double transformX = g2d.getClipBounds().getWidth() / this.project.gridSize;
		double transformY = g2d.getClipBounds().getHeight() / this.project.gridSize;
		
		g2d.translate((g2d.getClipBounds().getWidth() + transformX) / 2.0D, (g2d.getClipBounds().getHeight() + transformY) / 2.0D);
		
		for(Object object : this.project.objects) {
			if(object.shouldBeRendered()) {
				g2d.setColor(new Color(object.getStyle().getColorRange().getAverageColor())); 
				Point2D position = this.tranform(object.getPosition(1, 0), transformX, transformY);
				g2d.fillRect((int) (position.getX() - transformX / 2), (int) (position.getY() - transformY / 2), (int) transformX, (int) transformY);
			}
		}
		
		for(Link link : this.project.links) {
			if(link.shouldBeRendered()) {
				Line2D line = this.tranform(link.getLine(1, 0), transformX, transformY);
				g2d.draw(line);
			}
		}
	}
	
	public void reset(Project project) {
		this.project = project;
		// explicit reset not needed
	}
}
