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
package org.azentreprise.arionide.events;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class WriteEvent extends Event {
	
	private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	private final char ch;
	private final int keycode;
	private final boolean shift;
	private final boolean alt;
	
	public WriteEvent(char ch, int keycode, boolean shift, boolean alt) {
		this.ch = ch;
		this.keycode = keycode;
		this.shift = shift;
		this.alt = alt;
	}
	
	public char getChar() {
		return this.ch;
	}
	
	public int getKeycode() {
		return this.keycode;
	}
	
	public boolean isShiftDown() {
		return this.shift;
	}
	
	public boolean isAltDown() {
		return this.alt;
	}
	
	public boolean isCapsDown() {
		return WriteEvent.toolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
	}
}