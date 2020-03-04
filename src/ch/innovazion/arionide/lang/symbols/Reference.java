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
package ch.innovazion.arionide.lang.symbols;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.innovazion.arionide.Utils;

public class Reference implements ParameterValue {

	private static final long serialVersionUID = 7287598499790357836L;
	
	private final List<Parameter> lazyParameters;
	private final Map<String, ParameterValue> lazyMapping = new HashMap<>();
	private final Map<Parameter, ParameterValue> bindings = new HashMap<>();
	private Callable target;

	public Reference(List<Parameter> lazyParameters) { 
		// Lazy parameters will be evaluated only when the target is called. (lambda input)
		this.lazyParameters = Collections.unmodifiableList(lazyParameters);
		
		for(Parameter param : lazyParameters) {
			lazyMapping.put(param.getName(), param.getValue());
		}
	}
	
	public void setTarget(Callable target) {
		this.target = target;
		autobind();
	}
	
	public Callable getTarget() {
		return target;
	}
	
	public void autobind() {
		for(Parameter param : target.getSpecification().getParameters()) {
			ParameterValue value = lazyMapping.get(param.getName());
			
			if(value != null) {
				bindings.put(param, value);
			}
		}
	}
	
	public List<Parameter> getFreeParameters() {
		return target.getSpecification().getParameters().stream().filter(Utils.identityPredicate().and(bindings::containsKey).negate()).collect(Collectors.toList());
	}
	
	public List<Parameter> getBoundParameters() {
		return target.getSpecification().getParameters().stream().filter(bindings::containsKey).collect(Collectors.toList());
	}
	
	public void bind(Parameter calleeParameter, ParameterValue callerParameter) throws SymbolResolutionException {
		if(calleeParameter == null) {
			throw new SymbolResolutionException("Cannot use empty callee parameter for caller parameter <" + String.join("; ", callerParameter.getDisplayValue()) + ">");
		}
		
		if(callerParameter == null) {
			throw new SymbolResolutionException("Cannot use empty caller parameter for callee '" + calleeParameter.getName() + "'");
		}
		
		bindings.put(calleeParameter, callerParameter);
	}
	
	public ParameterValue resolve(Parameter calleeParameter) throws SymbolResolutionException {
		ParameterValue value = bindings.get(calleeParameter);
		
		if(value != null) {
			return value;
		} else {
			throw new SymbolResolutionException("Failed to resolve callee parameter '" + calleeParameter.getName() + "'");
		}
	}

	public List<String> getDisplayValue() {
		return Arrays.asList("Reference to '" + target.getName() + "'");
	}
	
	public Reference clone() {
		Reference clone = new Reference(lazyParameters);
		
		clone.target = target;
		clone.autobind();
		
		return clone;
	}
}