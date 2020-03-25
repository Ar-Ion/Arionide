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

	private final Object instance;
	private final Map<Field, Animation> animations = new HashMap<>();
	
	private int counter = 0;
	
	public ObjectModifierAnimation(AppManager manager, Class<?> clazz, Object instance) {
		this(manager, clazz, instance, new HyperbolicSymmetryAlgorithm(11.0d, 3.0d), new HashSet<>());
		manager.registerAnimation(this);
	}
	
	public ObjectModifierAnimation(AppManager manager, Class<?> clazz, Object instance, TransformationAlgorithm smoothingAlgorithm, Set<Object> ignore) {
		super(manager);
						
		this.instance = instance;
				
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
