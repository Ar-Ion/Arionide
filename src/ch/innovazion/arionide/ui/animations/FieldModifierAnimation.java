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

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.ui.AppManager;

public class FieldModifierAnimation extends Animation {
			
	private final Object instance;
	private final TransformationAlgorithm smoothingAlgorithm;
	
	private Field field;	
	private Number initial;
	private Number target;
	
	public FieldModifierAnimation(AppManager manager, String fieldName, Class<?> clazz, Object instance) {
		this(manager, fieldName, clazz, instance, new HyperbolicSymmetryAlgorithm(11.0d, 3.0d));
	}
	
	public FieldModifierAnimation(AppManager manager, String fieldName, Class<?> clazz, Object instance, TransformationAlgorithm smoothingAlgorithm) {
		super(manager);
		
		this.instance = instance;
		this.smoothingAlgorithm = smoothingAlgorithm;
		
		try {
			this.field = clazz.getDeclaredField(fieldName);
			this.field.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException exception) {
			Debug.exception(exception);
		}
	}
	
	public void startAnimation(int duration, Object... params) {		
		if(params.length > 0) {
			Object target = params[0];
			if(target instanceof Number) {
				try {
					this.initial = (Number) this.field.get(this.instance);
					this.target = (Number) target;
				} catch (IllegalArgumentException | IllegalAccessException exception) {
					Debug.exception(exception);
				}
			} else {
				throw new RuntimeException("Invalid number");
			}
		} else {
			throw new RuntimeException("You must specify the field target parameter");
		}
		
		super.startAnimation(duration, params);
	}
	
	public void tick() {
		double t = this.getProgression();
				
		if(t <= 1.0d) {
			try {
				double real = this.smoothingAlgorithm.compute(t);

				if(Double.isInfinite(real) || Double.isNaN(real) || real < 0.0d || real > 1.0d) {
					return; // silently ignore bug and do not commit the wrong value
				}
				
				double doubleValue = (this.target.doubleValue() - this.initial.doubleValue()) * real + this.initial.doubleValue();
				Object value = null;
																
				switch(this.field.getType().getName()) {
					case "double":
						value = (double) doubleValue;
						break;
					case "float":
						value = (float) doubleValue;
						break;
					case "long":
						value = (long) doubleValue;
						break;
					case "int":
						value = (int) doubleValue;
						break;
					case "short":
						value = (short) doubleValue;
						break;
					case "byte":
						value = (byte) doubleValue;
						break;
				}
				
				this.field.set(this.instance, value);
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		} else {
			try {
				this.field.set(this.instance, this.target);
			} catch (Exception exception) {
				Debug.exception(exception);
			}
			
			this.endAnimation();
		}
	}
}
