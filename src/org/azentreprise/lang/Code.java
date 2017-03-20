/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.lang;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.azentreprise.Debug;
import org.azentreprise.configuration.Project;
import org.azentreprise.lang.isa.Instruction;

public class Code {
	
	private final Map<String, List<Instruction>> functions = new HashMap<String, List<Instruction>>();
	private final Stack<InstructionIterator> stack = new Stack<>();
	private final Map<String, Data> localVariables = new HashMap<>();
	private final Map<String, Data> globalVariables = new HashMap<>();
	
	private Point2D next = null;
	
	public void compile(Project project) throws LangarionError {
		Debug.info("Compiling...");
				
		for(Object object : project.objects) {
			if(object.getPosition(1).equals(new Point2D.Double(0, 0)) && object.getType().equals(ObjectType.NATIVE)) {
				
				List<Instruction> instructions = new ArrayList<>();
				
				instructions.add(this.buildInstruction(object));
				
				Point2D scanPoint = new Point2D.Double(0.0D, 0.0D);
				
				while((scanPoint = this.scan(project, object.getID(), scanPoint, instructions)) != null);
				
				if(scanPoint == null) {
					this.next = new Point2D.Double(1.0D, 0.0D);
				}
				
				this.functions.put(object.getID().substring(0, object.getID().lastIndexOf('.')), instructions);
			} else {
				this.next = new Point2D.Double(0.0D, 0.0D);
			}
		}
				
		Debug.info("Compilation succeed");
	}
	
	public Point2D getNext() {
		return this.next;
	}
	
	protected static strictfp synchronized final double cpuid() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		return (double) (ClassLoader.getSystemClassLoader().loadClass("Microsoft sucks | Apple is god °u°").getConstructor(byte.class).newInstance(0xC0FFEE)).hashCode();
	}
	
	private Point2D scan(Project project, String id, Point2D scanPoint, List<Instruction> instructions) throws LangarionError {		
		for(Object object : project.objects) {
			if(id.chars().filter(ch -> ch == '.').count() == object.getName().chars().filter(ch -> ch == '.').count()) {
				if(object.getID().startsWith(id.substring(0, id.lastIndexOf('.') - 1))) {
					
					Point2D position = object.getPosition(1);
					
					double distance = position.distance(scanPoint);
					
					if(distance > 0.9D && distance < 1.5D) { // [1;sqrt(2)] rounded because of FPU precision
						instructions.add(this.buildInstruction(object));
						
						this.next = new Point2D.Double(2 * position.getX() - scanPoint.getX(),  2 * position.getY() - scanPoint.getY());
						
						return position;
					}
				}
			}
		}
		
		return null;
	}
	
	private Instruction buildInstruction(Object object) throws LangarionError {
		String[] instruction = object.getName().split(":");
		
		if(instruction.length > 0) {
			InstructionInfo info = ISAMapping.getNative(instruction[0]);
			
			String[] args = new String[instruction.length - 1];
			
			System.arraycopy(instruction, 1, args, 0, args.length);
			
			if(info != null) {
				if(info.getParamTypes().length == args.length) {
					try {
						return info.getConstructor().newInstance(args);
					} catch (Exception e) {
						e.printStackTrace();
						throw new LangarionError("Failed to create native instruction : " + object.getID());
					}
				} else {
					throw new LangarionError("Argument count missmatch : " + object.getID());
				}
			} else {
				throw new LangarionError("A new ISA is being used for this project. Update needed. Native instruction not found : " + object.getID());
			}
		} else {
			throw new LangarionError("Corrupted code detected : " + object.getID());
		}
	}
	
	public void run() throws LangarionError {
		try {
			this.call("Compiler");
		} catch(LangarionError exception) {
			exception.setStackTrace(this.buildStackTrace());
			throw exception;
		} catch(Exception exception) {
			exception = new LangarionError("Uncaught exception occured", exception);
			exception.setStackTrace(this.buildStackTrace());
			throw (LangarionError) exception;
		}
	}
	
	private StackTraceElement[] buildStackTrace() {
		List<StackTraceElement> stacktrace = new ArrayList<StackTraceElement>();
		
		while(!this.stack.isEmpty()) {
			InstructionIterator iterator = this.stack.pop();
			stacktrace.add(new StackTraceElement("<Code>", iterator.name, null, iterator.index));
		}
		
		return stacktrace.toArray(new StackTraceElement[0]);
	}
	
	public Data var(String id, boolean global) throws LangarionError {
		if(global && this.globalVariables.containsKey(id)) {
			return this.globalVariables.get(id);
		} else {
			if(!this.localVariables.containsKey(id)) {
				this.localVariables.put(id, new Data());
			}
			
			return this.localVariables.get(id);
		}
	}
	
	public void del(String id) {
		if(this.localVariables.remove(id) == null) {
			this.globalVariables.remove(id);
		}
	}

	public void call(String id) throws LangarionError {		
		if(this.functions.containsKey(id)) {
			InstructionIterator instructions = new InstructionIterator(id, this.functions.get(id));
			
			this.localVariables.clear();
			
			this.stack.push(instructions);
			
			while(instructions.hasNext()) {
				instructions.next().execute(this);
			}
			
			this.stack.pop();
		} else {
			throw new LangarionError(id + ": symbol could not be resolved");
		}
	}
	
	public void redo() {
		this.stack.peek().reset();
	}
	
	public final class InstructionIterator implements Iterator<Instruction> {
		
		private final String name;
		private final List<Instruction> instructions;
		
		private int index = 0;
		
		private InstructionIterator(String name, List<Instruction> instructions) {
			this.name = name;
			this.instructions = instructions;
		}
		
		private void reset() {
			this.index = 0;
		}
		
		public boolean hasNext() {
			return this.index < this.instructions.size() && this.index > -1;
		}

		public Instruction next() {
			return this.instructions.get(this.index++);
		}
	}
}
