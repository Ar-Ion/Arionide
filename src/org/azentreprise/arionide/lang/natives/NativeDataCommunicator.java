package org.azentreprise.arionide.lang.natives;

import java.util.function.BiConsumer;

public class NativeDataCommunicator {
	
	private BiConsumer<String, Integer> channel;
	
	public void setInfoChannel(BiConsumer<String, Integer> channel) {
		this.channel = channel;
	}
	
	public void info(String message, int color) {
		if(this.channel != null) {
			this.channel.accept("[prog] " + message, color);
		}
	}
}