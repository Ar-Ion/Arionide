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