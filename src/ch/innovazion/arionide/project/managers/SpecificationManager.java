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
package ch.innovazion.arionide.project.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.Specification;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.project.Manager;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.StructureMeta;

public class SpecificationManager extends Manager {
		
	protected SpecificationManager(Storage storage) {
		super(storage);
	}
	
	public MessageEvent addElement(Specification spec, SpecificationElement element) {
		this.doForeachConnectedSpecification(spec, other -> other.getElements().add(element));
	
		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent deleteElement(Specification spec, int id) {
		this.doForeachConnectedSpecification(spec, l -> l.getElements().remove(id));

		this.saveMeta();

		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent refactorName(Specification spec, int id, String newName) {
		this.doForeachConnectedSpecification(spec, l -> l.getElements().get(id).setName(newName));

		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}

	public MessageEvent refactorType(Specification spec, int id, int newType) {
		this.doForeachConnectedSpecification(spec, l -> ((Data) l.getElements().get(id)).setType(newType));
		
		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent refactorParameterName(Specification spec, int id, int data, String newName) {
		this.doForeachConnectedSpecification(spec, l -> ((Reference) l.getElements().get(id)).getEagerParameters().get(data).setName(newName));

		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent refactorParameterType(Specification spec, int id, int data, int newType) {
		this.doForeachConnectedSpecification(spec, l -> ((Data) ((Reference) l.getElements().get(id)).getEagerParameters().get(data)).setType(newType));
		
		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent bind(SpecificationElement sourceParam, SpecificationElement targetParam) {
		sourceParam.setValue(targetParam.getValue());
		
		this.saveMeta();
									
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent toggleCallability(Reference reference) {
		if(reference.getLazyParameters() != null) {
			reference.setSpecificationParameters(null);
			this.saveMeta();
			return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
		} else {
			reference.setSpecificationParameters(new ArrayList<>());
			this.saveMeta();
			return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
		}
	}
	
	public MessageEvent remove(Specification spec, SpecificationElement element) {
		if(spec.getElements().remove(element)) {
			this.saveMeta();
			return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
		} else {
			return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.WARN);
		}
	}
	
	public MessageEvent addParam(List<SpecificationElement> parameters, SpecificationElement newParam) {
		parameters.add(newParam);
		
		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent setValue(SpecificationElement element, String value) {
		element.setValue(value);

		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	public MessageEvent bindParameter(SpecificationElement source, SpecificationElement target) {
		target.setValue(SpecificationElement.VAR + source.getName());
		source.setValue(null);
		
		this.saveMeta();
		
		return new MessageEvent(DataManager.SUCCESS_STRING, MessageType.SUCCESS);
	}
	
	private void doForeachConnectedSpecification(Specification spec, Consumer<Specification> action) {
		this.getMeta().values().stream()
			.map(StructureMeta::getSpecification)
			.filter(spec::hasSameOrigin)
			.forEach(action);
	}
}
