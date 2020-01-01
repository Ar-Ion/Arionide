/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.core.geom;

public class Connection {
	
	public static int ONE_WAY = 0x0;
	public static int RECIPROCAL = 0x1;
	
	private final WorldElement firstOrSource;
	private final WorldElement secondOrDestination;
	private final int type;
	
	/*
	 * If the context is ambiguous, we prefer using a reciprocal connection.
	 */
	protected Connection(WorldElement first, WorldElement second) {
		this(first, second, RECIPROCAL);
	}
	
	protected Connection(WorldElement firstOrSource, WorldElement secondOrDestination, int type) {
		this.firstOrSource = firstOrSource;
		this.secondOrDestination = secondOrDestination;
		this.type = type;
	}
	
	public WorldElement getFirstElement() {
		if(this.type != RECIPROCAL) {
			throw new IllegalStateException("This isn't a reciprocal connection");
		}
		
		return this.firstOrSource;
	}
	
	public WorldElement getSecondElement() {
		if(this.type != RECIPROCAL) {
			throw new IllegalStateException("This isn't a reciprocal connection");
		}
		
		return this.secondOrDestination;
	}
	
	public WorldElement getSource() {
		if(this.type != ONE_WAY) {
			throw new IllegalStateException("This isn't a one-way connection");
		}
		
		return this.firstOrSource;
	}
	
	public WorldElement getDestination() {
		if(this.type != ONE_WAY) {
			throw new IllegalStateException("This isn't a one-way connection");
		}
		
		return this.secondOrDestination;
	}
}
