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
package ch.innovazion.arionide.menu.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.AtomicValue;
import ch.innovazion.arionide.lang.symbols.Enumeration;
import ch.innovazion.arionide.lang.symbols.InvalidValueException;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.Numeric;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Text;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.managers.specification.InformationManager;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class InformationUpdater extends ParameterUpdater {

	private InformationManager infoManager;
	private List<Node> children;
	
	@Inherit
	@Export
	protected boolean frozen;
	
	private Node currentNode;
	private Node selectedNode;
	
	public InformationUpdater(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		super.onEnter();
		updateCurrentNode(currentNode);
	}
	
	protected void onRefresh(String identifier, Object prevValue) {
		super.onRefresh(identifier, prevValue);
				
		if(identifier.equals("value")) {
			this.infoManager = getSpecificationManager().loadInformationManager(value);
			updateCurrentNode(infoManager.getRootNode());	
		}
	}
	
	private void updateCurrentNode(Node currentNode) {
		if(currentNode == null) {
			currentNode = infoManager.getRootNode();
		}
		
		this.currentNode = currentNode;
		this.children = currentNode.getNodes();
		
		List<String> elements = new ArrayList<>();
		
		if(value instanceof Number) {
			elements.add("Set number");
			
			if(!frozen) {
				elements.add(null);
				elements.addAll(Arrays.asList("As node", "As text", "As enumeration", "As variable", "As reference"));
			}
		} else if(value instanceof Text) {
			elements.add("Set text");
			
			if(!frozen) {
				elements.add(null);
				elements.addAll(Arrays.asList("As node", "As number", "As enumeration", "As variable", "As reference"));
			}
		} else if(value instanceof Enumeration) {
			elements.add("Setup enumeration");
		
			if(!frozen) {
				elements.add(null);
				elements.addAll(Arrays.asList("As node", "As number", "As text", "As variable", "As reference"));
			}
		} else if(value instanceof Variable) {
			elements.add("Setup variable");

			if(!frozen) {
				elements.add(null);
				elements.addAll(Arrays.asList("As node", "As number", "As text", "As enumeration", "As reference"));
			}
		} else if(value instanceof Reference) {
			elements.add("Setup reference");

			if(!frozen) {
				elements.add(null);
				elements.addAll(Arrays.asList("As node", "As number", "As text", "As variable", "As enumeration"));
			}
		} else {
			elements.add("New node");
			elements.add(null);
			children.stream().map(Node::getLabel).forEach(elements::add);
			elements.add(null);
			elements.addAll(Arrays.asList("As number", "As text", "As reference", "As enumeration"));
		}		
		
		elements.add(null);
		elements.addAll(Arrays.asList("Parent", "Label", "Destroy"));
		
		setDynamicElements(elements.toArray(new String[0]));
		
		updateCursor(0);
		
		updateDescription();
	}

	public void onAction(String action) {
		if(id < 10) {
			switch(action) {
			case "New node":
				createNode();
				break;
			case "As node":
				reassignAsNode();
				break;
			case "As number":
			case "Set number":
				reassignAsParseable(Numeric::new);
				break;
			case "As text":
			case "Set text":
				reassignAsParseable(Text::new);
				break;
			case "As variable":
				reassignAsVariable();
				break;
			case "Setup variable":
				this.value = currentNode;
				go("var");
				break;
			case "As reference":
				reassignAsReference();
				break;
			case "Setup reference":
				this.value = currentNode;
				go("ref");
				break;
			case "As enumeration":
				reassignAsEnumeration();
				break;
			case "Setup enumeration":
				this.value = currentNode;
				go("enum");
				break;
			case "Parent":
				back();
				break;
			case "Label":
				Views.input.setText("Please enter the label of the information")
						   .setPlaceholder("Information label")
						   .setResponder(this::label)
						   .stackOnto(Views.code);				
				break;
			case "Destroy":
				destroy();
				break;
			}
		} else {
			updateCurrentNode(children.get(id - 10));
			go(".");
		}
		
		
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
		case 8:
			destroy();
			break;
		case 1:
		case 5:
		case 9:
			break;
		default:

		}
	}
	
	private void createNode() {
		MessageEvent result = infoManager.createNode(currentNode);
		
		dispatch(result);
		
		if(result.getMessageType() != MessageType.ERROR) {
			dispatch(new GeometryInvalidateEvent(0));
			int nodeIndex = currentNode.getNumElements();
			
			updateParameter();			
			updateCursor(9 + nodeIndex);
		}
	}
	
	private void reassignAsNode() {
		Node node = new Node("new_node");
		dispatch(infoManager.assign(currentNode, node));
		dispatch(new GeometryInvalidateEvent(0));
		updateParameter();			
	}
	
	private void reassignAsEnumeration() {
		Enumeration enumeration = new Enumeration();
		dispatch(infoManager.assign(currentNode, enumeration));
		dispatch(new GeometryInvalidateEvent(0));
		
		updateParameter();
		
		this.value = enumeration;
		go("enum");
	}
	
	private void reassignAsVariable() {
		Variable variable = new Variable();
		dispatch(infoManager.assign(currentNode, variable));
		dispatch(new GeometryInvalidateEvent(0));
		updateParameter();
		
		this.value = variable;
		go("var");
	}
	
	private void reassignAsReference() {
		Reference ref = new Reference();
		dispatch(infoManager.assign(currentNode, ref));
		dispatch(new GeometryInvalidateEvent(0));
		
		updateParameter();
		
		this.value = ref;		
		go("ref");
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
				
		updateParameter();
	}
	
	private void back() {
		if(currentNode != null) {
			updateCurrentNode(currentNode.getParent());
			go(".");
		}
	}
	
	private void label(String name) {
		dispatch(infoManager.setLabel(currentNode, name));
		dispatch(new GeometryInvalidateEvent(0));
		updateParameter();
	}
	
	private void destroy() {
		if(currentNode.getParent() != null) {
			dispatch(infoManager.destroy(currentNode));
			dispatch(new GeometryInvalidateEvent(0));
			updateParameter();
			back();	
		} else {
			updateCurrentNode(infoManager.reset());
			updateParameter();
		}
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);
		
		if(cursor >= 10) {
			this.selectedNode = children.get(cursor - 10);
		} else {
			this.selectedNode = null;
		}
		
		if(value != null) {
			updateDescription();
		}
	}
	
	protected void updateDescription() {
		List<String> elements = new ArrayList<>();
			
		elements.add(getDescriptionTitle());
		
		if(currentNode != null) {
			elements.addAll(currentNode.getDisplayValue());
		} else if(value != null) {
			elements.addAll(value.getDisplayValue());
		}

		elements.add(new String());
		
		if(selectedNode != null) {
			elements.addAll(selectedNode.getDisplayValue());
		}
		
		this.description = new MenuDescription(elements.toArray(new String[0]));
	}
	
	protected String getDescriptionTitle() {
		if(currentNode != null) {
			if(currentNode instanceof AtomicValue) {
				return "Setting value of '" + currentNode.getPath() + "'";
			} else {
				return "Modifying structure of node '" + currentNode.getPath() + "'";
			}
		} else {
			return "Modifying node structure";
		}
	}
}