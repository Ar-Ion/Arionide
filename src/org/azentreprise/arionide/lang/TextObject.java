package org.azentreprise.arionide.lang;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.azentreprise.arionide.lang.natives.NativeTypes;

public class TextObject extends Object {

	private String value;
	
	protected TextObject(String value) {
		super(Arrays.asList(NativeTypes.TEXT));
		this.value = value;
	}
	
	protected void setValue(String value) {
		this.value = value;
	}
	
	protected String getValue() {
		return this.value;
	}
	
	protected Bit[] getData() {
		return Bit.fromByteArray(this.value.getBytes(Charset.forName("utf8")));
	}
}