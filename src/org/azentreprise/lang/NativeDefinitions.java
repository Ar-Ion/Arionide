package org.azentreprise.lang;

import java.io.File;
import java.io.IOException;

import org.azentreprise.configuration.Definitions;

public class NativeDefinitions extends Definitions {
	
	public NativeDefinitions() throws IOException, SecurityException, IllegalArgumentException, IllegalAccessException {
		super(File.createTempFile("native", ".defs"), "native");
	}
}
