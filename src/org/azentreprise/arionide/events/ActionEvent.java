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
package org.azentreprise.arionide.events;

import java.awt.geom.Point2D;

public class ActionEvent extends Event {
	
	public static final int BUTTON_LEFT = 1;
	public static final int BUTTON_RIGHT = 3;
	
	private final Point2D point;
	private final int button;
	private final ActionType type;
	
	public ActionEvent(Point2D point2d, int button, ActionType type) {
		this.point = point2d;
		this.button = button;
		this.type = type;
	}
	
	public Point2D getPoint() {
		return this.point;
	}
	
	public boolean isButton(int buttonID) {
		return button == buttonID;
	}
	
	public ActionType getType() {
		return this.type;
	}
}