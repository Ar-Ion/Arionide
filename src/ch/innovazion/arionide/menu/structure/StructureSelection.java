/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.menu.structure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.ui.AppManager;

public class StructureSelection extends StructureBrowser {

	private final Consumer<Integer> delegate;
	private final List<Comparator<Integer>> comparators = new ArrayList<>();
	private final Stack<Integer> hierarchy = new Stack<>();
	
	private Predicate<Integer> filter = Utils.identityPredicate();
	
	public StructureSelection(AppManager manager, Consumer<Integer> delegate) {
		super(manager);
		this.delegate = delegate;
	}
	
	public void resetComparators() {
		comparators.clear();
	}
	
	public void setupComparator(Comparator<Integer> comparator) {
		comparators.add(comparator);
	}
	
	public void resetFilters() {
		filter = Utils.identityPredicate();
	}
	
	public void setupFilter(Predicate<Integer> other) {
		filter = filter.and(other);
	}
	
	public void show() {
		hierarchy.clear();
		
		if(getProject() != null) {
			hierarchy.addAll(getProject().getDataManager().getHostStack().getStack());
		}
		
		super.show();
	}

	public List<Integer> loadCurrentElements() {		
		List<HierarchyElement> root = getProject().getStorage().getHierarchy();
		List<HierarchyElement> current = getProject().getDataManager().getCurrentGeneration(root, hierarchy);
		
		List<Integer> IDs = Utils.extract(current, HierarchyElement::getID);
		
		for(Comparator<Integer> comparator : comparators) {
			IDs.sort(comparator);
		}
		
		return IDs.stream().filter(filter).collect(Collectors.toList());
	}
	
	protected void onUp() {
		if(!hierarchy.isEmpty()) {
			hierarchy.pop();
			super.show();
		}
	}
	
	protected void onDown() {
		if(!getElements().isEmpty()) {
			hierarchy.push(getSelectedID());
			super.show();
		}
	}
	
	public void onClick(int element) {
		this.delegate.accept(getSelectedID());
	}
}