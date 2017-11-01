package org.azentreprise.arionide.lang.natives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.TypeManager;

public class StructureTypeManager implements TypeManager {
	public List<String> getSuggestions(CoreDataManager cdm) {
		List<String> suggestions = new ArrayList<>();
		
		for(Entry<Integer, String> entry : cdm.getReferencables().entrySet()) {
			suggestions.add(entry.getValue() + "$$$" + entry.getKey());
		}
		
		return suggestions;
	}

	public List<String> getActionLabels() {
		return Arrays.asList();
	}

	public List<BiConsumer<String, Consumer<String>>> getActions() {
		return Arrays.asList();
	}
}