/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.events;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class WriteEvent extends Event {
	
	private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	private final char ch;
	private final int keycode;
	private final int modifiers;
	
	public WriteEvent(char ch, int keycode, int modifiers) {
		this.ch = ch;
		this.keycode = keycode;
		this.modifiers = modifiers;
	}
	
	public char getChar() {
		return this.ch;
	}
	
	public int getKeycode() {
		return this.keycode;
	}
	
	public int getModifiers() {
		return this.modifiers;
	}
	
	public boolean isCapsDown() {
		return WriteEvent.toolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
	}
}