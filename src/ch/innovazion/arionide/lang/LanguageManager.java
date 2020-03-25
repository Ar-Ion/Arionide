package ch.innovazion.arionide.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageManager {
	
	private static final List<String> names = new ArrayList<>();
	private static final Map<String, Language> languages = new HashMap<>();
	
	
	static {
		register("AVR", null);
	}
	
	
	private static void register(String name, Language lang) {
		names.add(name);
		languages.put(name, lang);
	}
	
	public static List<String> getAvailableLanguages() {
		return names;
	}
	
	public static Language get(String name) {
		return languages.get(name);
	}
}
