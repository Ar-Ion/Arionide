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
package ch.innovazion.arionide.threading;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.programs.ProgramIO;

public class ProgramThread extends WorkingThread {

	private final AtomicReference<Program> currentProgram = new AtomicReference<>();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private int currentTarget;
	private ProgramIO currentIO;;
	
	public void tick() {
		Program prog = currentProgram.getAndSet(null);

		if(prog != null) {
			System.out.println("Launching program...");
			
			running.compareAndSet(false, true);
			
			try {
				prog.run(currentTarget, currentIO);
			} catch(Exception err) {
				err.printStackTrace();
			} catch(StackOverflowError err) {
				err.printStackTrace();
			} finally {
				running.compareAndSet(true, false);
				System.out.println("Program terminated");
				System.gc();
			}
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
	
	public void terminate() {
		interrupt();
	}
	
	public boolean isRunning() {
		return running.get();
	}

	public boolean respawn(int attempt) {
		return true;
	}
}
