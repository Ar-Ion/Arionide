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
package ch.innovazion.arionide.ui.gc;

import java.util.Stack;

public class Trash {
	
	private static Trash instance;
	
	private final TrashContext context;
	private final Stack<Trashable> trash = new Stack<>();
	
	private Trash(TrashContext context) {
		this.context = context;
	}
	
	public boolean throwAway(Trashable garbage) {
		if(garbage != null) {
			return this.trash.add(garbage);
		} else {
			return false;
		}
	}
	
	public void burnGarbage() {
		synchronized(this.trash) {
			while(!this.trash.isEmpty()) {
				Trashable garbage = this.trash.pop();
			
				if(garbage.checkContextCompatibility(this.context)) {
					garbage.burn(this.context);
				} else {
					System.err.println("Application tried to burn an object incompatible with the current trash context.");
				}
			}
		}
	}
	
	public static void init(TrashContext context) {
		assert instance == null;
		instance = new Trash(context);
	}
	
	public static Trash instance() {
		if(instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("Trash not initialized");
		}
	}
}
