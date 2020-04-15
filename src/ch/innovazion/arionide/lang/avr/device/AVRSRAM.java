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
package ch.innovazion.arionide.lang.avr.device;

import java.util.concurrent.atomic.AtomicBoolean;

import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.peripherals.SRAM;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class AVRSRAM extends SRAM {
	
	private final AtomicBoolean readyForSampling = new AtomicBoolean(false);
	
	private Label[] registers = new Label[32];
	private Label sreg;
	private Label sp;
	private Label eind;
	private Label rampz;
	private Label rampy;
	private Label rampx;
	private Label rampd;

	public AVRSRAM(int size) {
		super(size);
	}
	
	
	private String pad2(String input) {
		return String.format("%2s", input).replace(" ", "0");
	}
	
	private String pad4(String input) {
		return String.format("%4s", input).replace(" ", "0");
	}
	
	private String pad8(String input) {
		return String.format("%8s", input).replace(" ", "0");
	}

	public void sample() {
		try {
			if(readyForSampling.get()) {
				for(int i = 0; i < 32; i++) {
					registers[i].setLabel("0x" + pad2(Integer.toHexString(getUnsigned(i))));
				}
				
				sreg.setLabel("SREG: 0b" + pad8(Integer.toBinaryString(getUnsigned(0x5F))));
				sp.setLabel("SP: 0x" + pad4(Integer.toHexString((getUnsigned(0x5E) << 8) | getUnsigned(0x5D))));
				eind.setLabel("EIND: 0x" + pad2(Integer.toHexString(getUnsigned(0x5C))));
				rampz.setLabel("RAMPZ: 0x" + pad2(Integer.toHexString(getUnsigned(0x5B))));
				rampy.setLabel("RAMPY: 0x" + pad2(Integer.toHexString(getUnsigned(0x5A))));
				rampx.setLabel("RAMPX: 0x" + pad2(Integer.toHexString(getUnsigned(0x59))));
				rampd.setLabel("RAMPD: 0x" + pad2(Integer.toHexString(getUnsigned(0x58))));
			}
		} catch (EvaluationException e) {
			;
		}
	}

	public void createDisplay(Container display) {
		float width = 1.0f / 8;
		float height = 1.0f / 8;
		
		readyForSampling.set(false);
		
		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 8; x++) {
				Label label = new Label(display, new String());
				label.setVisible(true);
				registers[y * 8 + x] = label;
				display.add(label, x * height, y * height + 0.1f, (x + 1) * width - 0.01f, (y + 1) * height + 0.1f);
			}
		}
		
		float specialRegistersY = 0.7f;
		
		display.add(this.sreg = new Label(display, new String()), 0.0f, specialRegistersY, 0.2f - 0.01f, specialRegistersY + 0.2f);
		display.add(this.sp = new Label(display, new String()), 0.2f, specialRegistersY, 0.4f - 0.01f, specialRegistersY + 0.2f);
		display.add(this.eind = new Label(display, new String()), 0.4f, specialRegistersY, 0.6f - 0.01f, specialRegistersY + 0.2f);
		display.add(this.rampz = new Label(display, new String()), 0.6f, specialRegistersY, 0.7f - 0.01f, specialRegistersY + 0.2f);
		display.add(this.rampy = new Label(display, new String()), 0.7f, specialRegistersY, 0.8f - 0.01f, specialRegistersY + 0.2f);
		display.add(this.rampx = new Label(display, new String()), 0.8f, specialRegistersY, 0.9f - 0.01f, specialRegistersY + 0.2f);
		display.add(this.rampd = new Label(display, new String()), 0.9f, specialRegistersY, 1.0f - 0.01f, specialRegistersY + 0.2f);

		sreg.setVisible(true);
		sp.setVisible(true);
		eind.setVisible(true);
		rampz.setVisible(true);
		rampy.setVisible(true);
		rampx.setVisible(true);
		rampd.setVisible(true);
		
		readyForSampling.set(true);
	}
}
