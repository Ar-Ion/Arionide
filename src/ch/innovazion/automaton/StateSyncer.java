package ch.innovazion.automaton;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
				
		for(Field syncable : syncables) {			
			Field component = lookupMap.get(syncable.getName());
			
			if(component != null) {
				try {
					Object pre = syncable.get(target);
					Object post = component.get(source);
					
					if(!pre.equals(post)) {
						syncable.set(target, component.get(source));
						onUpdate.accept(syncable.getName(), pre);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new StateSynchronizationException("Given target state '" + target + "' is not compatible with current state '" + source + "'");
				}
			}
		}
	}
	
	private Stream<Field> scanFieldsForAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		return Stream.of(clazz.getDeclaredFields()).filter(e -> e.isAnnotationPresent(annotationClass));
	}
	
	private boolean isAccessible(Field field, State target) {
		Export annotation = field.getAnnotation(Export.class);
		assert annotation != null;
		
		return annotation.value().length == 0 || Stream.of(annotation.value()).anyMatch(target.getClass()::equals);
	}
}
