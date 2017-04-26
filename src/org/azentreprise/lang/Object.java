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
package org.azentreprise.lang;

import java.awt.geom.Point2D;
import java.util.List;

import org.azentreprise.configuration.Parseable;
import org.azentreprise.ui.Renderable;
import org.azentreprise.ui.Style;

public class Object extends Parseable implements Renderable {
	
	private volatile static List<Style> styleList;
	private volatile static String currentID;
	
	private final String id;
	private final String name;
	private final String[] args;
	private Point2D position;
	private int styleID;
	private final ObjectType type;
	private final boolean mutable;
	
	public Object(String serialized) {
		super(serialized);
		
		String[] objInfo = serialized.substring(serialized.indexOf('[') + 1, serialized.indexOf(']')).split(" ");
		
		this.id = serialized.split(" ")[0];
		
		String[] idComponents = this.id.split("\\.");
		
		if(idComponents.length > 0){
			String[] data = idComponents[idComponents.length - 1].split(":");
			
			this.name = data[0];
			this.args = new String[data.length - 1];
			
			System.arraycopy(data, 1, this.args, 0, this.args.length);
		} else {
			this.name = this.id;
			this.args = new String[0];
		}
				
		this.position = new Point2D.Float(Integer.parseInt(objInfo[0]), Integer.parseInt(objInfo[1]));
		this.styleID = Integer.parseInt(objInfo[2]);
		this.mutable = objInfo[3].equals("mutable");
		this.type = ObjectType.valueOf(objInfo[4].toUpperCase());
	}
	
	public Object(String id, Point2D position, int styleID, ObjectType type, boolean mutable) {
		super(null);
		
		this.id = id;
		
		String[] idComponents = this.id.split("\\.");
		
		if(idComponents.length > 0){
			String[] data = idComponents[idComponents.length - 1].split(":");
			
			this.name = data[0];
			this.args = new String[data.length - 1];
			
			System.arraycopy(data, 1, this.args, 0, this.args.length);
		} else {
			this.name = this.id;
			this.args = new String[0];
		}
		
		this.position = position;
		this.styleID = styleID;
		this.type = type;
		this.mutable = mutable;
	}
	
	protected String serialize() {
		return this.id + " [" + (int) this.position.getX() + " " + (int) this.position.getY() + " " + this.styleID + " " + (this.mutable ? "mutable" : "immutable") + " " + this.type.name().toLowerCase() + "]";
	}
	
	public boolean shouldBeRendered() {
		return this.id.startsWith(Object.currentID) && getHierarchyLevel(this.id) == getHierarchyLevel(Object.currentID) + 1;
	}
	
	public static int getHierarchyLevel(String id) {
		return id == "" ? -1 : (int) id.chars().filter(ch -> ch == '.').count();
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String[] getArgs() {
		return this.args;
	}
	
	public Point2D getPosition(float mult, float scalar) {
		return new Point2D.Double((this.position.getX() + scalar) * mult, (this.position.getY() + scalar) * mult);
	}
	
	public Style getStyle() {
		return Object.styleList.get(this.styleID);
	}
	
	public ObjectType getType() {
		return this.type;
	}
	
	public boolean isMutable() {
		return this.mutable;
	}
	
	public void setPosition(Point2D position) {
		this.position = position;
	}
	
	public void setStyleID(int id) {
		this.styleID = id;
	}
	
	public static void uploadStyleList(List<Style> styleList) {
		Object.styleList = styleList;
	}
	
	public static void uploadCurrentID(String id) {
		Object.currentID = id;
	}
}