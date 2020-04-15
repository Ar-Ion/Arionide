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
					registers[i].setLabel("0x" + pad2(Integer.toHexString(get(i))));
				}
				
				sreg.setLabel("SREG: 0b" + pad8(Integer.toBinaryString(get(0x5F))));
				sp.setLabel("SP: 0x" + pad4(Integer.toHexString((get(0x5E) << 8) | get(0x5D))));
				eind.setLabel("EIND: 0x" + pad2(Integer.toHexString(get(0x5C))));
				rampz.setLabel("RAMPZ: 0x" + pad2(Integer.toHexString(get(0x5B))));
				rampy.setLabel("RAMPY: 0x" + pad2(Integer.toHexString(get(0x5A))));
				rampx.setLabel("RAMPX: 0x" + pad2(Integer.toHexString(get(0x59))));
				rampd.setLabel("RAMPD: 0x" + pad2(Integer.toHexString(get(0x58))));
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
				display.add(label, x * height, y * height, (x + 1) * width - 0.01f, (y + 1) * height);
			}
		}
		
		float specialRegistersY = 0.5f;
		
		display.add(this.sreg = new Label(display, new String()), 0.0f, specialRegistersY, 0.2f - 0.01f, specialRegistersY + 0.3f);
		display.add(this.sp = new Label(display, new String()), 0.2f, specialRegistersY, 0.4f - 0.01f, specialRegistersY + 0.3f);
		display.add(this.eind = new Label(display, new String()), 0.4f, specialRegistersY, 0.6f - 0.01f, specialRegistersY + 0.3f);
		display.add(this.rampz = new Label(display, new String()), 0.6f, specialRegistersY, 0.7f - 0.01f, specialRegistersY + 0.3f);
		display.add(this.rampy = new Label(display, new String()), 0.7f, specialRegistersY, 0.8f - 0.01f, specialRegistersY + 0.3f);
		display.add(this.rampx = new Label(display, new String()), 0.8f, specialRegistersY, 0.9f - 0.01f, specialRegistersY + 0.3f);
		display.add(this.rampd = new Label(display, new String()), 0.9f, specialRegistersY, 1.0f - 0.01f, specialRegistersY + 0.3f);

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
