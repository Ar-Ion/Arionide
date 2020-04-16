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
	
	public static final int X = 0x1A;
	public static final int Y = 0x1C;
	public static final int Z = 0x1E;

	public static final int SREG = 0x5F;
	public static final int SPH = 0x5E;
	public static final int SPL = 0x5D;
	public static final int SP = 0x5D;
	public static final int EIND = 0x5C;
	public static final int RAMPZ = 0x5B;
	public static final int RAMPY = 0x5A;
	public static final int RAMPX = 0x59;
	public static final int RAMPD = 0x58;

	public static final int DDRA = 0x1A;
	public static final int PORTA = 0x1B;
	public static final int PINA = 0x19;

	public static final int DDRB = 0x17;
	public static final int PORTB = 0x18;
	public static final int PINB = 0x16;

	public static final int DDRC = 0x14;
	public static final int PORTC = 0x15;
	public static final int PINC = 0x13;

	public static final int DDRD = 0x11;
	public static final int PORTD = 0x12;
	public static final int PIND = 0x10;

	public static final int DDRE = 0x02;
	public static final int PORTE = 0x03;
	public static final int PINE = 0x01;

	public static final int DDRF = 0x61;
	public static final int PINF = 0x00;


	
	public static final int REG_BEGIN = 0x00;
	public static final int IO_BEGIN = 0x20;
	public static final int EXT_IO_BEGIN = 0x60;
	public static final int INT_SRAM_END = 0xF00; // exclusive
	public static final int SRAM_END = 0x20000; // exclusive
	
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
					registers[i].setLabel("0x" + pad2(Integer.toHexString(get(i))));
				}
				
				sreg.setLabel("SREG: 0b" + pad8(Integer.toBinaryString(get(SREG))));
				sp.setLabel("SP: 0x" + pad4(Integer.toHexString((get(SPH) << 8) | get(SPL))));
				eind.setLabel("EIND: 0x" + pad2(Integer.toHexString(get(EIND))));
				rampz.setLabel("RAMPZ: 0x" + pad2(Integer.toHexString(get(RAMPZ))));
				rampy.setLabel("RAMPY: 0x" + pad2(Integer.toHexString(get(RAMPY))));
				rampx.setLabel("RAMPX: 0x" + pad2(Integer.toHexString(get(RAMPX))));
				rampd.setLabel("RAMPD: 0x" + pad2(Integer.toHexString(get(RAMPD))));
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
	
	public void push(int value) throws EvaluationException {
		int sp = getWord(AVRSRAM.SP);
		set(sp, value);
		setWord(AVRSRAM.SP, sp - 1);
	}
	
	public int pop() throws EvaluationException {
		int sp = getWord(AVRSRAM.SP);
		setWord(AVRSRAM.SP, sp + 1);
		return get(sp + 1);
	}
	
	public void pushWord(int value) throws EvaluationException {
		int sp = getWord(AVRSRAM.SP);
		setWord(sp - 1, value);
		setWord(AVRSRAM.SP, sp - 2);
	}
	
	public int popWord() throws EvaluationException {
		int sp = getWord(AVRSRAM.SP);
		setWord(AVRSRAM.SP, sp + 2);
		return getWord(sp + 1);
	}
	
	public int getWord(int lowAddr) throws EvaluationException {
		int low = get(lowAddr);
		int high = get(lowAddr + 1);
		
		return (high << 8) | low;
	}
	
	public void setWord(int lowAddr, int value) throws EvaluationException {
		set(lowAddr, value & 0xFF);
		set(lowAddr + 1, (value >> 8) & 0xFF);
	}
	
	public int getRegister(int id) throws EvaluationException {
		return get(REG_BEGIN + id);
	}

	public int getIO(int id) throws EvaluationException {
		return get(IO_BEGIN + id);
	}
	
	public int getExtIO(int id) throws EvaluationException {
		return get(EXT_IO_BEGIN + id);
	}
	
	public int getData(int address) throws EvaluationException {
		return get(address);
	}
	
	public void setRegister(int id, int value) throws EvaluationException {
		set(REG_BEGIN + id, value);
	}

	public void setIO(int id, int value) throws EvaluationException {
		set(IO_BEGIN + id, value);
	}
	
	public void setExtIO(int id, int value) throws EvaluationException {
		set(EXT_IO_BEGIN + id, value);
	}
	
	public void setData(int address, int value) throws EvaluationException {
		set(address, value);
	}
}
