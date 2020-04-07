package ch.innovazion.arionide.lang.avr;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.Program;

public class AVRLanguage extends Language {

	private static final long serialVersionUID = -962242275743950263L;

	private final Environment env = new AVREnvironment(this);
	private final Instruction entryPoint = new AVREntryPoint();
	
	protected void registerInstructions() {
		registerInstruction(new NoOperation());
	}

	protected short getVersionMajor() {
		return 1;
	}

	protected short getVersionMinor() {
		return 0;
	}

	public String getVendorUID() {
		return "Atmel Corp. (Microchip Technology)";
	}

	public Instruction getEntryPoint() {
		return entryPoint;
	}

	public Environment getEnvironment() {
		return env;
	}

	public List<Program> getPrograms() {
		return Arrays.asList();
	}

}
