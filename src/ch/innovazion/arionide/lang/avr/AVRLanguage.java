package ch.innovazion.arionide.lang.avr;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.programs.Debugger;
import ch.innovazion.arionide.lang.programs.Relocator;
import ch.innovazion.arionide.lang.programs.SkeletonBuilder;
import ch.innovazion.arionide.project.Storage;

public class AVRLanguage extends Language {

	private static final long serialVersionUID = -962242275743950263L;

	private final Environment env = new AVREnvironment(this);
	private Instruction entryPoint = new AVREntryPoint();
		
	protected void registerPrograms(Storage storage) {
		registerProgram(new SkeletonBuilder(storage));
		registerProgram(new Relocator(storage));
		registerProgram(new Debugger(storage, env));
	}
	
	protected void registerInstructions() {
		registerShadowInstruction(this.entryPoint = new AVREntryPoint());
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
}
