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
	public static final Enumeration IO = new Enumeration();
	public static final Enumeration LOW_REGISTER = new Enumeration();
	public static final Enumeration HIGH_REGISTER = new Enumeration();
	public static final Enumeration POINTER = new Enumeration();
	public static final Enumeration DISP_POINTER = new Enumeration();
	public static final Enumeration LPM_POINTER = new Enumeration();
	public static final Enumeration ADDRESS_MASK = new Enumeration();
	public static final Enumeration OR_BIT_MASK = new Enumeration();
	public static final Enumeration AND_BIT_MASK = new Enumeration();

	static {
		for(int i = 0; i < 32; i++) {
			REGISTER.addPossibleValue(new Numeric(i).cast(8).label("R" + i));
			
			if(i < 8) {
				OR_BIT_MASK.addPossibleValue(new Numeric(~(1 << i)).label(String.valueOf(i)));
				AND_BIT_MASK.addPossibleValue(new Numeric(1 << i).label(String.valueOf(i)));
			}
			
			if(i < 16) {
				LOW_REGISTER.addPossibleValue(new Numeric(i).cast(8).label("R" + i));
			} else {
				HIGH_REGISTER.addPossibleValue(new Numeric(i).cast(8).label("R" + i));
			}
		}
		
		IO.addPossibleValue(new Numeric(AVRSRAM.DDRA).label("DDRA"));
		IO.addPossibleValue(new Numeric(AVRSRAM.DDRB).label("DDRB"));
		IO.addPossibleValue(new Numeric(AVRSRAM.DDRC).label("DDRC"));
		IO.addPossibleValue(new Numeric(AVRSRAM.DDRD).label("DDRD"));
		IO.addPossibleValue(new Numeric(AVRSRAM.DDRE).label("DDRE"));
		IO.addPossibleValue(new Numeric(AVRSRAM.DDRF).label("DDRF"));
		
		IO.addPossibleValue(new Numeric(AVRSRAM.PORTA).label("PORTA"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PORTB).label("PORTB"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PORTC).label("PORTC"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PORTD).label("PORTD"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PORTE).label("PORTE"));

		IO.addPossibleValue(new Numeric(AVRSRAM.PINA).label("PINA"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PINB).label("PINB"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PINC).label("PINC"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PIND).label("PIND"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PINE).label("PINE"));
		IO.addPossibleValue(new Numeric(AVRSRAM.PINF).label("PINF"));

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
		
			LPM_POINTER.addPossibleValue(new Node("Z").connect(new Numeric(AVRSRAM.Z).label("register")).connect(new Numeric(0).label("increment")));
			LPM_POINTER.addPossibleValue(new Node("Z+").connect(new Numeric(AVRSRAM.Z).label("register")).connect(new Numeric(+1).label("increment")));
		} catch(SymbolResolutionException e) {
			e.printStackTrace();
		}
		
		ADDRESS_MASK.addPossibleValue("Low");
		ADDRESS_MASK.addPossibleValue("High");
	}
}
