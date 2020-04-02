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
import java.util.function.Supplier;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.InvalidValueException;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Text;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.menu.params.ParameterValueAssigner;
import ch.innovazion.arionide.project.managers.specification.InformationManager;
import ch.innovazion.arionide.ui.overlay.Views;

public class InformationAssigner extends ParameterValueAssigner {

	private InformationManager infoManager;
	private List<Node> children;
	
	private Node currentNode;
	
	public InformationAssigner(MenuManager manager) {
		super(manager, "New node", null, "Number", "Text", "Reference", null, "Parent", "Label", "Destroy", null);
	}
	
	protected void onEnter() {
		super.onEnter();
		this.infoManager = getSpecificationManager().loadInformationManager(value);
		updateCurrentNode(null);
	}
	
	private void updateCurrentNode(Node currentNode) {
		if(currentNode == null) {
			currentNode = infoManager.getRootNode();
		}
		
		this.currentNode = currentNode;
		this.children = currentNode.getNodes();
		this.setDynamicElements(children.stream().map(Node::getLabel).toArray(String[]::new));
		
		updateDescription();
	}

	public void onAction(String action) {
		switch(id) {
		case 0:
			createNode();
			break;
		case 2:
			reassignAsParseable(Numeric::new);
			break;
		case 3:
			reassignAsParseable(Text::new);
			break;
		case 4:
			reassignAsReference();
			break;
		case 6:
			back();
			break;
		case 7:
			Views.input.setText("Please enter the label of the information")
					   .setPlaceholder("Information label")
					   .setResponder(this::label)
					   .stackOnto(Views.code);
			break;
		case 8:
			destroy();
			break;
		case 1:
		case 5:
		case 9:
			break;
		default:
			updateCurrentNode(children.get(id - 9));
			go(".");
		}
	}
	
	private void createNode() {
		dispatch(infoManager.assign(currentNode, new Node()));
		dispatch(new GeometryInvalidateEvent(0));
		go(".");
	}
	
	private void reassignAsReference() {
		Reference ref = new Reference();
		dispatch(infoManager.assign(currentNode, ref));
		dispatch(new GeometryInvalidateEvent(0));
		
		this.value = ref;
		
		go("../reference");
	}
	
	private void reassignAsParseable(Supplier<Node> valueAllocator) {
		Views.input.setText("Please enter the value of the information")
				   .setPlaceholder("Information value")
				   .setResponder(rawValue -> reassignParseable0(rawValue, valueAllocator))
				   .stackOnto(Views.code);
	}
	
	private void reassignParseable0(String rawValue, Supplier<Node> valueAllocator) {
		Node value = valueAllocator.get();
		
		try {
			value.parse(rawValue);
			dispatch(infoManager.assign(currentNode, value));
			dispatch(new GeometryInvalidateEvent(0));
			updateCurrentNode(value);
		} catch (InvalidValueException e) {
			dispatch(new MessageEvent(e.getMessage(), MessageType.ERROR));
		}
		
		go(".");
	}
	
	private void back() {
		if(currentNode != null) {
			updateCurrentNode(currentNode.getParent());
		}
	}
	
	private void label(String name) {
		dispatch(infoManager.setLabel(currentNode, name));
		dispatch(new GeometryInvalidateEvent(0));
		go(".");
	}
	
	private void destroy() {
		dispatch(infoManager.destroy(currentNode));
		dispatch(new GeometryInvalidateEvent(0));
		back();
		go(".");
	}
	
	protected String getDescriptionTitle() {
		if(currentNode != null) {
			return "Modifying structure of node '" + currentNode.getPath() + "'";
		} else {
			return "Modifying node structure";
		}
	}
}