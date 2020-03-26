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
package ch.innovazion.arionide.ui.animations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.ui.AppManager;

public class ObjectModifierAnimation extends Animation {

	private final Map<Field, Animation> animations = new HashMap<>();
	
	private int counter = 0;
	
	public ObjectModifierAnimation(AppManager manager, Class<?> clazz, Object instance) {
		this(manager, clazz, instance, new HyperbolicSymmetryAlgorithm(11.0d, 3.0d), new HashSet<>());
		manager.registerAnimation(this);
	}
	
	public ObjectModifierAnimation(AppManager manager, Class<?> clazz, Object instance, TransformationAlgorithm smoothingAlgorithm, Set<Object> ignore) {
		super(manager);
				
		try {
			for(Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);

				Object currentValue = field.get(instance);
								
				if(currentValue instanceof Number) {
					animations.put(field, new FieldModifierAnimation(manager, field.getName(), clazz, instance, smoothingAlgorithm));
				} else {
					if(currentValue != null && !ignore.contains(currentValue)) {
						ignore.add(currentValue);
						animations.put(field, new ObjectModifierAnimation(manager, currentValue.getClass(), currentValue, smoothingAlgorithm, ignore));
					}
				}
			}
		} catch(IllegalArgumentException | IllegalAccessException exception) {
			Debug.exception(exception);
		}
	}
	
	public void startAnimation(int duration, Object... params) {		
		if(params.length > 0) {
			Object target = params[0];
			
			for(Entry<Field, Animation> entry : animations.entrySet()) {
				try {
					Object fieldTarget = entry.getKey().get(target);
					entry.getValue().startAnimation(duration, nil -> {
						tryEndAnimation();
					}, fieldTarget);
				} catch (IllegalArgumentException | IllegalAccessException exception) {
					exception.printStackTrace();
					// Could not animate some fields: Silently ignore
				}
			}
		} else { 
			throw new RuntimeException("You must specify the field target parameter");
		}
		
		super.startAnimation(duration, params);
		
		// Do not call the super method
	}
	
	private void tryEndAnimation() {
		counter++;
		
		if(counter == animations.size()) {
			endAnimation();
			counter = 0;
		}
	}
	
	public void tick() {
		; // Do nothing
	}
}
