package org.azentreprise.arionide;

import java.util.HashMap;
import java.util.Map;

public class SystemCache {
	
	private static final Map<String, Object> elements = new HashMap<>();
	
	public static void set(String key, Object value) {
		SystemCache.elements.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) SystemCache.elements.get(key);
	}
}