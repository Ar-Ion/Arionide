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

import java.util.function.Consumer;

import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.lang.symbols.Enumeration;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Specification;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.project.Storage;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.HostStructureStack;
import ch.innovazion.arionide.project.managers.ResourceAllocator;

public class SpecificationManager extends ContextualManager<Specification> {
	
	private final InformationManager informationManager;
	private final VariableManager variableManager;
	private final ReferenceManager referenceManager;
	private final EnumerationManager enumManager;

	public SpecificationManager(Storage storage, ResourceAllocator allocator, HostStructureStack hostStack) {
		super(storage);
		
		this.informationManager = new InformationManager(storage);
		this.referenceManager = new ReferenceManager(storage, allocator, hostStack);
		this.variableManager = new VariableManager(storage);
		this.enumManager = new EnumerationManager(storage);
	}
	
	public MessageEvent addParameter(Parameter element) {
		doForeachConnectedSpecification(other -> {
			other.getParameters().add(element);
		});
		saveStructures();
		
		return success();
	}
	
	public MessageEvent removeParameter(Parameter param) {
		doForeachConnectedSpecification(other -> other.getParameters().remove(param));
		saveStructures();

		return success();
	}
	
	public MessageEvent refactorParameterName(Parameter param, String newName) {
		doForeachConnectedSpecification(other -> {
			for(Parameter target : other.getParameters()) {
				if(target.equals(param)) { // Name comparison
					target.setName(newName);
				}
			}
		});
		
		saveStructures();
		
		return success();
	}
	
	public MessageEvent refactorParameterDefault(Parameter param, ParameterValue newDefault) {
		doForeachConnectedSpecification(other -> {
			for(Parameter target : other.getParameters()) {
				if(target.equals(param) && param.getValue().equals(target.getValue())) { // Name comparison
					target.setValue(newDefault.clone()); // Only refactor the default value if the target had the previous default value
				}
			}
		});
		
		saveStructures();
		
		return success();
	}
	
	public MessageEvent bindParameters(Parameter sourceParam, Parameter targetParam) {
		targetParam.setValue(sourceParam.getValue());
		saveStructures();
		
		return success();
	}
	
	private void doForeachConnectedSpecification(Consumer<Specification> action) {
		getStructures().values().stream()
			.map(Structure::getSpecification)
			.filter(getContext()::hasSameOrigin)
			.forEach(action);
	}
	
	public InformationManager loadInformationManager(ParameterValue value) {
		informationManager.setContext((Information) value);
		return informationManager;
	}
	
	public ReferenceManager loadReferenceManager(ParameterValue value) {
		referenceManager.setContext((Reference) value);
		return referenceManager;
	}
	
	public VariableManager loadVariableManager(ParameterValue value) {
		variableManager.setContext((Variable) value);
		return variableManager;
	}
	
	public EnumerationManager loadEnumerationManager(ParameterValue value) {
		enumManager.setContext((Enumeration) value);
		return enumManager;
	}
}
