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

import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Peripheral;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class DigitalOutput implements Peripheral {
	
	private final Label[] labels;
	
	private final SRAM sram;
	private final int pinCount;
	private final int pinAddress;
	
	private Label state;
	
	public DigitalOutput(SRAM sram, int pinCount, int pinAddress) {		
		this.sram = sram;
		this.pinCount = pinCount;
		this.pinAddress = pinAddress;
		
		this.labels = new Label[pinCount];
	}
	
	public void sample() {
		if(state != null) {
			try {
				int bits = sram.getUnsigned(pinAddress);
				
				state.setLabel("Encoded pin value: 0b" + String.format("%8s", Integer.toBinaryString(bits)).replace(" ", "0"));
				
				for(int i = 0; i < pinCount; i++) {
					if(bits >> i == 0) {
						labels[i].setAlpha(0xFF);
					} else {
						labels[i].setAlpha(0x7F);
					}
				}
			} catch (EvaluationException e) {
				System.err.println("Invalid digital output port address");
			}
		}
	}

	public void createDisplay(Container display) {
		float dimension = 1.0f / pinCount;
		
		for(int i = 0; i < pinCount; i++) {
			Label label = new Label(display, "o");
			label.setVisible(true);
			label.setEnabled(true);
			labels[i] = label;
			display.add(label, i * dimension, 0.1f, (i + 1) * dimension, 0.4f);
		}
		
		display.add(this.state = new Label(display, new String()), 0.0f, 0.5f, 1.0f, 0.8f);
		state.setVisible(true);
	}

	public String getUID() {
		return "DigitalOut";
	}
	
	public String toString() {
		return "User digital output";
	}
}