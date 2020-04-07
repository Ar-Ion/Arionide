package ch.innovazion.arionide.lang.avr;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.Language;

public class AVREnvironment extends Environment {

	private final AVRLanguage lang;
	
	protected AVREnvironment(AVRLanguage lang) {
		super(4000000000L);
		this.lang = lang;
	}

	public Language getLanguage() {
		return lang;
	}
}