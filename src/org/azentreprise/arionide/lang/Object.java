package org.azentreprise.arionide.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.azentreprise.arionide.lang.natives.NativeDataCommunicator;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class Object {
	
	private final List<Integer> structure;
	private final List<Object> data = new ArrayList<>();
	private int index = 0;
	
	public Object(List<Integer> structure) {
		this.structure = structure;
	}
	
	protected void add(String value, NativeDataCommunicator communicator) {
		switch(this.structure.get(this.index++ % this.structure.size())) {
			case NativeTypes.INTEGER:
				this.data.add(new IntegerObject(value));
				break;
			case NativeTypes.TEXT:
				this.data.add(new TextObject(value));
				break;
			case NativeTypes.STRUCTURE:
				this.data.add(communicator.getObject(value));
		}
	}
	
	public boolean isConsistent() {
		return this.index % this.structure.size() == 0;
	}
	
	protected Bit[] getData() {
		return this.data.stream().map(Object::getDataStream).reduce(Stream::concat).orElse(Stream.of()).toArray(Bit[]::new);
	}
	
	private Stream<Bit> getDataStream() {
		return Arrays.stream(this.getData());
	}
	
	protected int getSize() {
		return this.getData().length;
	}
}