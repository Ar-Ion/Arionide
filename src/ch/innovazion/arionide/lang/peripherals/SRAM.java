package ch.innovazion.arionide.lang.peripherals;

import ch.innovazion.arionide.lang.EvaluationException;
import ch.innovazion.arionide.lang.Peripheral;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class SRAM implements Peripheral {
	
	private final byte[] memory;
	
	public SRAM(int size) {
		this.memory = new byte[size];
	}
	
	public byte get(int index) throws EvaluationException {
		if(index >= 0 && index < memory.length) {
			return memory[index];
		} else {
			throw new EvaluationException("Attempt to access an invalid SRAM memory location: " + index);
		}
	}

	public String getUID() {
		return (memory.length / 1024) + "KB SRAM memory";
	}

	public void sample() {
		
	}

	public void createDisplay(Container display) {
		display.add(new Label(display, "This SRAM memory has no structure"), 0.0f, 0.0f, 1.0f, 1.0f);
	}
}
