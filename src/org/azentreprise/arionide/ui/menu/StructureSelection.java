/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.menu;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.azentreprise.arionide.project.Storage;
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
		Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
		
		Map<Integer, StructureMeta> elements = storage.getStructureMeta();
		this.ordered = elements.entrySet().stream()
				.filter(e -> !e.getValue().getName().equals("?") && storage.getInheritance().containsKey(e.getKey()))
				.map(Entry::getKey)
				.collect(Collectors.toList());
		
		
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