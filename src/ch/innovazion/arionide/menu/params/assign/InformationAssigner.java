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
import java.util.Stack;
import java.util.function.Supplier;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.InvalidValueException;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.SymbolResolutionException;
import ch.innovazion.arionide.lang.symbols.Text;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.menu.params.ParameterValueAssigner;
import ch.innovazion.arionide.project.managers.specification.InformationManager;
import ch.innovazion.arionide.ui.overlay.Views;

public class InformationAssigner extends ParameterValueAssigner {

	private InformationManager infoManager;
	private List<Information> children;

	private Stack<Information> path = new Stack<>();
	
	public InformationAssigner(MenuManager manager) {
		super(manager, "Node", "Number", "Text", "Reference", null, "Back", "Label", "Destroy", null);
	}
	
	protected void onEnter() {
		super.onEnter();
		
		Information root = (Information) value;
		
		try {
			path.push(root);
			path.push(root.resolve("<root>"));
		} catch (SymbolResolutionException exception) {
			Debug.exception(exception);
			go("..");
		}
		
		update();
	}
	
	private void update() {
		this.infoManager = getSpecificationManager().loadInformationManager(path.peek());
		this.children = infoManager.getChildren();
		this.setDynamicElements(children.stream().map(Information::getLabel).toArray(String[]::new));
	}

	public void onAction(String action) {
		switch(id) {
		case 0:
			reassign(Information::new);
			break;
		case 1:
			reassign(Numeric::new);
			break;
		case 2:
			reassign(Text::new);
			break;
		case 3:
			reassign(Reference::new);
			break;
		case 5:
			back();
			break;
		case 6:
			Views.input.setText("Please enter the label of the information")
					   .setPlaceholder("Information label")
					   .setResponder(this::label)
					   .stackOnto(Views.code);
			break;
		case 7:
			destroy();
			break;
		case 4:
		case 8:
			break;
		default:
			path.push(children.get(id - 9));
			update();
			go(".");
		}
	}
	
	
	
	private void reassign(Supplier<Information> valueAllocator) {
		Views.input.setText("Please enter the value of the information")
				   .setPlaceholder("Information value")
				   .setResponder(rawValue -> reassign0(rawValue, valueAllocator))
				   .stackOnto(Views.code);
	}
	
	private void reassign0(String rawValue, Supplier<Information> valueAllocator) {
		Information value = valueAllocator.get();
		
		try {
			value.parse(rawValue);
			dispatch(infoManager.assign(path.get(1), value));
			dispatch(new GeometryInvalidateEvent(0));
			update();
			go(".");
		} catch (InvalidValueException e) {
			dispatch(new MessageEvent(e.getMessage(), MessageType.ERROR));
		}
	}
	
	private void back() {
		if(path.size() > 2) {
			path.pop();
			update();
			go(".");
		} else {
			go("..");
		}
	}
	
	private void label(String name) {
		dispatch(infoManager.setLabel(name));
		dispatch(new GeometryInvalidateEvent(0));
	}
	
	private void destroy() {
		dispatch(infoManager.destroy(path.get(1)));
		dispatch(new GeometryInvalidateEvent(0));
	}
}