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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui.render;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.azentreprise.configuration.Project;

public abstract class ProjectRenderer {
	
	protected Project project;
	
	public ProjectRenderer(Project project) {
		this.project = project;
	}
	
	protected Point2D tranform(Point2D source, double transformX, double transformY) {
		return new Point2D.Double(source.getX() * transformX, source.getY() * transformY);
	}
	
	protected Line2D tranform(Line2D source, double transformX, double transformY) {
		return new Line2D.Double(this.tranform(source.getP1(), transformX, transformY), this.tranform(source.getP2(), transformX, transformY));
	}
	
	public abstract void render(Graphics2D g2d);
	public abstract void reset(Project project);
}
