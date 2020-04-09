/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.menu.params.assign;

import java.util.List;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.lang.symbols.Constant;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.menu.params.ParameterUpdater;
import ch.innovazion.arionide.project.managers.specification.ConstantManager;
import ch.innovazion.arionide.ui.overlay.Views;

public class ConstantAssigner extends ParameterUpdater {

	private ConstantManager constManager;
	
	private List<Node> constants;

	public ConstantAssigner(MenuManager manager) {
		super(manager, "New", null);
	}
	
	protected void onEnter() {
		super.onEnter();
		
		this.constManager = getSpecificationManager().loadConstantManager(value);
		this.constants = constManager.getConstants(target);
		
		setDynamicElements(constants.stream().map(Node::getLabel).toArray(String[]::new));
	}

	public void onAction(String action) {
		if(id == 0) {
			Views.input.setText("Please enter the name of the new constant")
					   .setPlaceholder("Constant name")
					   .setResponder(this::createConstant)
					   .stackOnto(Views.code);
		} else if(id != 1) {
			dispatch(constManager.assign((Constant) constants.get(id - 2)));
			this.value = constManager.getContext();
			updateParameter();
		}
	}
	
	protected String getDescriptionTitle() {
		return "Assigning a constant";
	}
	
	private void createConstant(String name) {
		dispatch(constManager.createAndAssign(target, name));
		dispatch(new GeometryInvalidateEvent(2));
		this.value = constManager.getContext();
		go(".");
	}
}