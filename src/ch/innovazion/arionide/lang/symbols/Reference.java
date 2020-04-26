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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.innovazion.arionide.Utils;

public class Reference extends AtomicValue {

	private static final long serialVersionUID = 7287598499790357836L;
	
	private final List<Parameter> lazyParameters = new ArrayList<>();
	private final Map<String, ParameterValue> lazyMapping = new HashMap<>();
	private final Map<Parameter, ParameterValue> bindings = new HashMap<>();
	private Callable target;
	private Numeric targetID;
	
	public Reference() {
		super("Reference");
	}
	
	private Reference(Reference parent) {
		super(parent);
				
		this.target = parent.target;
		parent.lazyParameters.forEach(this::addLazyParameter);
		autobind();
	}
	
	/*
	 * The three following methods are used to CREATE a reference parameter inside a specification
	 */
	public void addLazyParameter(Parameter param) {
		lazyParameters.add(param);
		lazyMapping.put(param.getName(), param.getValue());
	}
	
	public void removeLazyParameter(int paramID) {
		Parameter old = lazyParameters.remove(paramID);
		
		if(old != null) {
			lazyMapping.remove(old.getName());
		}
	}
	
	public List<Parameter> getLazyParameters() {
		return Collections.unmodifiableList(lazyParameters);
	}
	
	/*
	 * The following methods are used to ASSIGN a reference parameter to a specification
	 */
	public void setTarget(Callable target) {
		this.target = target;
		this.targetID = new Numeric(target.getIdentifier());
		autobind();
	}
	
	public Callable getTarget() {
		return target;
	}
	
	public void autobind() {
		if(target != null) {
			for(Parameter param : target.getSpecification().getParameters()) {
				ParameterValue value = lazyMapping.get(param.getName());
				
				if(value != null) {
					bindings.put(param, value);
				}
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
		List<String> display = new ArrayList<>();
		
		if(target != null) {
			display.add("Reference to '" + target.getName() + "'");
		}
		
		if(lazyParameters.size() > 0) {
			display.add(new String());
		}
		
		for(Parameter param : lazyParameters) {
			display.add(param.getName() + ": " + String.join(", ", param.getDisplayValue()));
		}
		
		return display;
	}
	
	public int getSize() {
		return 0;
	}

	public Stream<Bit> getRawStream() {
		return targetID.getRawStream();
	}
	
	public Reference clone() {
		return new Reference(this);
	}
}