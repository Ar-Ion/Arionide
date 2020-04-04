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
package ch.innovazion.automaton;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateSyncer {
	
	protected void synchronizeStates(State source, State target, BiConsumer<String, Object> onUpdate) {
		List<Field> syncables = scanFieldsForAnnotation(target.getClass(), Inherit.class).collect(Collectors.toList());		
		Stream<Field> accessibleComponents = scanFieldsForAnnotation(source.getClass(), Export.class).filter(f -> isAccessible(f, target));
				
		Map<String, Field> lookupMap = accessibleComponents.collect(Collectors.toMap(f -> f.getName(), Function.identity()));
		
		Map<String, Object> prevValues = new LinkedHashMap<>();
		
		for(Field syncable : syncables) {			
			Field component = lookupMap.get(syncable.getName());
			
			if(component != null) {
				try {
					syncable.setAccessible(true);
					component.setAccessible(true);
					
					Object pre = syncable.get(target);
					Object post = component.get(source);
					
					if(pre == null) {
						if(post != null) {
							syncable.set(target, component.get(source));
							prevValues.put(syncable.getName(), pre);
						}
					} else if(!pre.equals(post)) {
						syncable.set(target, component.get(source));
						prevValues.put(syncable.getName(), pre);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new StateSynchronizationException("Given target state '" + target + "' is not compatible with current state '" + source + "'", e);
				}
			}
		}
		
		prevValues.forEach(onUpdate);
	}
	
	private Stream<Field> scanFieldsForAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		if(clazz != Object.class) {
			return Stream.concat(scanFieldsForAnnotation(clazz.getSuperclass(), annotationClass), Stream.of(clazz.getDeclaredFields()).filter(e -> e.isAnnotationPresent(annotationClass)));
		} else {
			return Stream.empty();
		}
	}
	
	
	private boolean isAccessible(Field field, State target) {
		Export annotation = field.getAnnotation(Export.class);
		assert annotation != null;
		
		return annotation.value().length == 0 || Stream.of(annotation.value()).anyMatch(target.getClass()::equals);
	}
}
