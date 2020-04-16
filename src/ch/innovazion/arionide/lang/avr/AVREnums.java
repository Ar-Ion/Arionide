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
package ch.innovazion.arionide.lang.avr;

import ch.innovazion.arionide.lang.avr.device.AVRSRAM;
import ch.innovazion.arionide.lang.symbols.Enumeration;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.SymbolResolutionException;

public class AVREnums {
	public static final Enumeration REGISTER = new Enumeration();
	public static final Enumeration LOW_REGISTER = new Enumeration();
	public static final Enumeration HIGH_REGISTER = new Enumeration();
	public static final Enumeration POINTER = new Enumeration();
	public static final Enumeration DISP_POINTER = new Enumeration();

	static {
		for(int i = 0; i < 32; i++) {
			REGISTER.addPossibleValue(new Numeric(i).cast(8).label("R" + i));
			
			if(i < 16) {
				LOW_REGISTER.addPossibleValue(new Numeric(i).cast(8).label("R" + i));
			} else {
				HIGH_REGISTER.addPossibleValue(new Numeric(i).cast(8).label("R" + i));
			}
		}
		
		try {
			POINTER.addPossibleValue(new Node("X").connect(new Numeric(AVRSRAM.X).label("register")).connect(new Numeric(0).label("increment")));
			POINTER.addPossibleValue(new Node("Y").connect(new Numeric(AVRSRAM.Y).label("register")).connect(new Numeric(0).label("increment")));
			POINTER.addPossibleValue(new Node("Z").connect(new Numeric(AVRSRAM.Z).label("register")).connect(new Numeric(0).label("increment")));
			POINTER.addPossibleValue(new Node("X+").connect(new Numeric(AVRSRAM.X).label("register")).connect(new Numeric(+1).label("increment")));
			POINTER.addPossibleValue(new Node("Y+").connect(new Numeric(AVRSRAM.Y).label("register")).connect(new Numeric(+1).label("increment")));
			POINTER.addPossibleValue(new Node("Z+").connect(new Numeric(AVRSRAM.Z).label("register")).connect(new Numeric(+1).label("increment")));
			POINTER.addPossibleValue(new Node("X-").connect(new Numeric(AVRSRAM.X).label("register")).connect(new Numeric(-1).label("increment")));
			POINTER.addPossibleValue(new Node("Y-").connect(new Numeric(AVRSRAM.Y).label("register")).connect(new Numeric(-1).label("increment")));
			POINTER.addPossibleValue(new Node("Z-").connect(new Numeric(AVRSRAM.Z).label("register")).connect(new Numeric(-1).label("increment")));
			
			DISP_POINTER.addPossibleValue(new Node("Y").connect(new Numeric(AVRSRAM.Y).label("register")).connect(new Numeric(0).label("increment")));
			DISP_POINTER.addPossibleValue(new Node("Z").connect(new Numeric(AVRSRAM.Z).label("register")).connect(new Numeric(0).label("increment")));
		} catch(SymbolResolutionException e) {
			e.printStackTrace();
		}
	}
}