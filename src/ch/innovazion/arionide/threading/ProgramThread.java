package ch.innovazion.arionide.threading;

import java.util.concurrent.atomic.AtomicReference;

import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.programs.ProgramIO;

public class ProgramThread extends WorkingThread {

	private final AtomicReference<Program> currentProgram = new AtomicReference<>();
	private int currentTarget;
	private ProgramIO currentIO;;
	
	public void tick() {
		Program prog = currentProgram.getAndSet(null);
		
		if(prog != null) {
			prog.run(currentTarget, currentIO);
		}
	}
	
	public boolean launch(Program program, int target, ProgramIO io) {
		this.currentTarget = target;
		this.currentIO = io;
		return currentProgram.compareAndSet(null, program);
	}

	public long getRefreshDelay() {
		return 20;
	}

	public String getDescriptor() {
		return "Program execution thread";
	}
	
	public void reset() {
		interrupt();
	}

	public boolean respawn(int attempt) {
		return true;
	}
}
