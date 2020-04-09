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
package ch.innovazion.arionide.project.managers.specification;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.symbols.AtomicValue;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.lang.symbols.SymbolResolutionException;
import ch.innovazion.arionide.project.Storage;

public class InformationManager extends ContextualManager<Information> {
	protected InformationManager(Storage storage) {
		super(storage);
	}
	
	public Node getRootNode() {
		return getContext().getRoot();
	}
	
	public Node reset() {
		return getContext().resetRootNode();
	}
		
	public MessageEvent setLabel(Node target, String name) {
		target.label(name);
		return success();
	}
	
	public MessageEvent destroy(Node target) {
		try {
			Node parent = target.getParent();
						
			if(parent != null) {
				parent.disconnect(target);
				return success();
			} else {
				return warn();
			}
		} catch (SymbolResolutionException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
	
	public MessageEvent createNode(Node parent) {
		try {
			if(parent instanceof AtomicValue) {
				return new MessageEvent("Cannot connect a node to an atomic information", MessageType.ERROR);
			}
			
			Node node = new Node("lambda");
			StringBuilder sb = new StringBuilder();
			
			sb.append(parent.getNumElements());

			while(node.has(sb.toString())) {
				sb.append('\'');
			}
			
			node.label(sb.toString());
			
			parent.connect(node);
			
			return success();
		} catch (SymbolResolutionException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
	
	public MessageEvent assign(Node prevValue, Node newValue) {
		try {
			Node parent = prevValue.getParent();

			if(parent != null) {
				if(parent instanceof AtomicValue) {
					return new MessageEvent("Cannot assign a value to an atomic information", MessageType.ERROR);
				}
				
				newValue.label(prevValue.getLabel());
								
				int index = parent.indexOf(prevValue);
				parent.disconnect(prevValue);
				parent.connect(newValue, index);
								
				return success();
			} else {
				return warn();
			}
		} catch (SymbolResolutionException exception) {
			return new MessageEvent(exception.getMessage(), MessageType.ERROR);
		}
	}
}