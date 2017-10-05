package org.azentreprise.arionide.ui.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;

public class StructureSelection extends Menu {

	private final Consumer<Integer> delegate;
	private final Comparator<Integer>[] comparators;
	private List<Integer> ordered;
	
	@SafeVarargs
	public StructureSelection(AppManager manager, Consumer<Integer> delegate, Comparator<Integer>... comparators) {
		super(manager);
		this.delegate = delegate;
		this.comparators = comparators;
		this.reload();
	}

	public void reload() {
		Map<Integer, StructureMeta> elements = this.getAppManager().getWorkspace().getCurrentProject().getStorage().getStructureMeta();
		this.ordered = new ArrayList<>(elements.keySet());
		
		for(Comparator<Integer> comparator : this.comparators) {
			this.ordered.sort(comparator);
		}
		
		this.getElements().clear();
		this.getElements().addAll(this.ordered.stream().map(e -> elements.get(e).getName()).collect(Collectors.toList()));
	}
	
	public void onClick(int element) {
		if(this.ordered != null) {
			this.delegate.accept(this.ordered.get(element));
		}
	}
	
	public String getDescription() {
		return "Please select a structure";
	}
}