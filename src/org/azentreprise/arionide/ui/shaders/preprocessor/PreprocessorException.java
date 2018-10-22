package org.azentreprise.arionide.ui.shaders.preprocessor;

import java.io.IOException;

public class PreprocessorException extends IOException {
	private static final long serialVersionUID = 2707607742500487314L;
	
	public PreprocessorException() {
		super();
	}
	
	public PreprocessorException(String exception) {
		super(exception);
	}
}