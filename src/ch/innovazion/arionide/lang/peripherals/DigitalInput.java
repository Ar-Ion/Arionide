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
package ch.innovazion.arionide.lang.peripherals;

import java.util.stream.Stream;

import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Peripheral;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Button;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class DigitalInput implements Peripheral {
	
	private final Button[] buttons;
	
	private final SRAM sram;
	private final int pinCount;
	private final int pinAddress;
	
	private Label state;
	
	public DigitalInput(SRAM sram, int pinCount, int pinAddress) {		
		this.sram = sram;
		this.pinCount = pinCount;
		this.pinAddress = pinAddress;
		
		this.buttons = new Button[pinCount];
	}
	
	public void sample() {
		if(state != null) {
			int bits = Stream.of(buttons).map(Button::isPressed).mapToInt(b -> b ? 0 : 1).reduce((a, b) -> (a << 1) | b).orElse(0xFF); // Pull-up
			
			try {
				sram.set(pinAddress, bits);
			} catch (EvaluationException e) {
				System.err.println("Invalid digital input pin address");
			}
			
			state.setLabel("Encoded pin value: 0b" + String.format("%8s", Integer.toBinaryString(bits)).replace(" ", "0"));
		}
	}

	public void createDisplay(Container display) {
		float dimension = 1.0f / pinCount;
		
		for(int i = 0; i < pinCount; i++) {
			Button button = new Button(display, "o");
			button.setVisible(true);
			button.setEnabled(true);
			button.setBordered(false);
			buttons[i] = button;
			display.add(button, i * dimension, 0.1f, (i + 1) * dimension, 0.4f);
		}
		
		display.add(this.state = new Label(display, new String()), 0.0f, 0.5f, 1.0f, 0.8f);
		state.setVisible(true);
	}

	public String getUID() {
		return "DigitalIn";
	}
	
	public String toString() {
		return "User digital input";
	}
}