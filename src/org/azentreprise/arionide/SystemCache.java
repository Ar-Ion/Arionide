package org.azentreprise.arionide;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SystemCache {
	
	private static final Map<String, Entry<Object, Long>> elements = new HashMap<>();
	
	public static <T> T set(String key, T value, long millisBeforeExpiration) {
		SystemCache.elements.put(key, new SimpleEntry<Object, Long>(value, System.currentTimeMillis() + millisBeforeExpiration));
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) SystemCache.elements.get(key);
	}
	
	public static boolean has(String key) {
		return SystemCache.elements.containsKey(key) || SystemCache.elements.get(key).getValue() < System.currentTimeMillis();
	}
}