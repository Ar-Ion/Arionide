package org.azentreprise.arionide.lang;

import java.util.Arrays;

import org.azentreprise.arionide.lang.natives.NativeTypes;

public class IntegerObject extends Object {

	private String value;
	
	protected IntegerObject(String value) {
		super(Arrays.asList(NativeTypes.INTEGER));
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public Bit[] getData() {
		char type = this.value.charAt(0);
		String realValue = this.value.substring(1);
		
		switch(type) {
			case 'b':
				return Bit.fromInteger(Integer.parseInt(realValue, 2), realValue.length());
			case 'd':
				return Bit.fromInteger(Integer.parseInt(realValue), 32);
			case 'h':
				return Bit.fromInteger(Integer.parseInt(realValue, 16), realValue.length() * 4);
		}
		
		return null;
	}
}