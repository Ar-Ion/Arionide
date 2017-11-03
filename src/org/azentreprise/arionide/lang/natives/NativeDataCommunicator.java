/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.lang.natives;

import java.util.Stack;
import java.util.function.BiConsumer;

public class NativeDataCommunicator {
	
	private final NativeRuntime runtime;
	private final BiConsumer<String, Integer> channel;
	private final Stack<Integer> stack = new Stack<>();
	
	protected NativeDataCommunicator(NativeRuntime runtime, BiConsumer<String, Integer> channel) {
		this.runtime = runtime;
		this.channel = channel;
	}
	
	public void info(String message, int color) {
		this.channel.accept("[prog] " + message, color);
	}
	
	public void exec(int structureID) {
		this.runtime.exec(structureID);
	}
	
	public Stack<Integer> getStack() {
		return this.stack;
	}
	
	public void exception(String message) {
		this.channel.accept(message, 0xFF0000);
		
		while(!this.stack.empty()) {
			int element = this.stack.pop();
			this.channel.accept("In @{" + element + "} (" + element + ":?)", 0xFF7700);
		}
	}
}