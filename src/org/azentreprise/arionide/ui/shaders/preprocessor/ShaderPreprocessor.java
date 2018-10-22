package org.azentreprise.arionide.ui.shaders.preprocessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShaderPreprocessor {
	
	private static final Map<String, Integer[]> argumentCount = new HashMap<>(); // Sorted assumption
	
	static {
		argumentCount.put("config", new Integer[1]);
		argumentCount.put("import", new Integer[1]);
	}
	
	
	private final ShaderSettings settings;
	
	public ShaderPreprocessor(ShaderSettings settings) {
		this.settings = settings;
	}
	
	public String processCommand(String command, String[] args) throws PreprocessorException {
		this.checkCommand(command, args.length);
		
		switch(command) {
			case "config":
				return this.settings.resolveConstant(args[0]);	
			case "import":
				return this.settings.resolveFunction(args[0]);
			default:
				throw new PreprocessorException();
		}
	}
	
	private void checkCommand(String command, int count) throws PreprocessorException {
		Integer[] argumentCounts = argumentCount.get(command);
		
		if(argumentCounts != null) {
			if(Arrays.binarySearch(argumentCounts, Integer.valueOf(count)) < 0) {
				throw new PreprocessorException("Preprocessor command signature mismatch");
			}
		} else {
			throw new PreprocessorException("Preprocessor command not found");
		}
	}
}
