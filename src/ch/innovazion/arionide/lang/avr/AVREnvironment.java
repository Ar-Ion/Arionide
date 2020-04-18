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

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.avr.device.AVRSRAM;
import ch.innovazion.arionide.lang.peripherals.DigitalInput;
import ch.innovazion.arionide.lang.peripherals.DigitalOutput;

public class AVREnvironment extends Environment {

	private final AVRLanguage lang;
	private final AVRSRAM sram = new AVRSRAM(4096);
	
	protected AVREnvironment(AVRLanguage lang) {
		super(4000000000L);
		this.lang = lang;
				
		registerPeripheral(sram);
		registerPeripheral(new DigitalInput(sram, 8, AVRSRAM.IO_BEGIN + AVRSRAM.PINB));
		registerPeripheral(new DigitalOutput(sram, 8, AVRSRAM.IO_BEGIN + AVRSRAM.PORTB));
	}
	
	public void init() {
		super.init();
		
		try {
			sram.set(AVRSRAM.IO_BEGIN + AVRSRAM.PORTB, 0xFF);
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
	}

	public Language getLanguage() {
		return lang;
	}
	
	
	protected void executeInterrupt(long address) throws EvaluationException {
		sram.pushWord((int) getProgramCounter().get());
		sram.set(AVRSRAM.SREG, sram.get(AVRSRAM.SREG) & 0b01111111);	
		super.executeInterrupt(address);
	}
}