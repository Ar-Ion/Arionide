/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.azentreprise.configuration.Project;
import org.azentreprise.lang.Link;
import org.azentreprise.lang.Object;

public class ComplexProjectRenderer extends ProjectRenderer {
	
	public static final Color glow = new Color(Color.HSBtoRGB(0.6f, 0.75f, 0.75f));

	public ComplexProjectRenderer(Project project) {
		super(project);
		UIStructureRenderer.init(0.64f, 0.5f);
		UILinkRenderer.init(0.64f, 0.5f);
	}
	
	public void render(Graphics2D g2d) {
		Stroke initialContext = g2d.getStroke();
		AlphaComposite composite = (AlphaComposite) g2d.getComposite();

		g2d.setStroke(new BasicStroke((float) Math.ceil(this.project.pixelsPerCell / 4.0f)));
		g2d.setComposite(composite.derive((float) Math.pow(composite.getAlpha(), 8)));
		
		for(Link link : this.project.links) {
			if(link.shouldBeRendered()) {
				Line2D line = link.getLine(this.project.pixelsPerCell, 0.5f);
				UILinkRenderer.render(g2d, (int) (line.getX1() + this.project.gridPosX), (int) (line.getY1() + this.project.gridPosY), (int) (line.getX2() + this.project.gridPosX), (int) (line.getY2() + this.project.gridPosY), (int) Math.ceil(this.project.pixelsPerCell / 8.0f));
			}
		}
		
		g2d.setStroke(initialContext);
		g2d.setComposite(composite);
		
		for(Object obj : this.project.objects) {
			if(obj.shouldBeRendered()) {
				Point2D position = obj.getPosition(this.project.pixelsPerCell, 0.5f);
				UIStructureRenderer.render(g2d, (int) (this.project.gridPosX + position.getX()), (int) (this.project.gridPosY + position.getY()), (int) Math.ceil(this.project.pixelsPerCell / 2.0f));
			}
		}
	}
	
	public void clean() {
		System.gc();
	}
	
	public void reset(Project project) {
		this.project = project;
		this.clean();
	}
}
