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
package org.azentreprise.lang;

import java.awt.geom.Line2D;
import java.util.Map;

import org.azentreprise.configuration.Parseable;
import org.azentreprise.ui.Renderable;

public class Link extends Parseable implements Renderable {
	
	private volatile static Map<String, Object> objectMapping;
	
	private final String id1;
	private final String id2;
	
	public Link(String serialized) {
		super(serialized);
		
		String[] data = serialized.split("<-->");
		
		this.id1 = data[0];
		this.id2 = data[1];
	}
	
	public Link(String id1, String id2) {
		super(null);
		
		this.id1 = id1;
		this.id2 = id2;
	}
	
	protected String serialize() {
		return this.id1 + "<-->" + this.id2;
	}
	
	public boolean shouldBeRendered() {
		return Link.objectMapping.containsKey(this.id1) && Link.objectMapping.containsKey(this.id2)
			&& Link.objectMapping.get(this.id1).shouldBeRendered() && Link.objectMapping.get(this.id2).shouldBeRendered();
	}
	
	public Object[] getObjects() {
		return new Object[] {Link.objectMapping.get(this.id1), Link.objectMapping.get(this.id2)};
	}
	
	public Line2D getLine(float mult, float scalar) {
		return new Line2D.Float(Link.objectMapping.get(this.id1).getPosition(mult, scalar), Link.objectMapping.get(this.id2).getPosition(mult, scalar));
	}
	
	public static void uploadObjectMapping(Map<String, Object> objectMapping) {
		Link.objectMapping = objectMapping;
	}
}
