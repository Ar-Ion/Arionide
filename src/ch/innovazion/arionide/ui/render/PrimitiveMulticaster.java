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
package ch.innovazion.arionide.ui.render;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface PrimitiveMulticaster {
	public Primitive[] getPrimitives();
	
	//  All methods from the binding interface should have void as return type.
	@SuppressWarnings("unchecked")
	public static <T> T create(Class<T> bindingInterface, Primitive... primitives) {
		for(Primitive primitive : primitives) {
			if(!(bindingInterface.isInstance(primitive))) {
				throw new IllegalArgumentException();
			}
		}
		
		return (T) Proxy.newProxyInstance(PrimitiveMulticaster.class.getClassLoader(), new Class<?>[] { bindingInterface, PrimitiveMulticaster.class }, new MulticasterRedirect(primitives));
	}
	
	class MulticasterRedirect implements InvocationHandler {
		
		private final Primitive[] primitives;
		
		private MulticasterRedirect(Primitive[] primitives) {
			this.primitives = primitives;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.equals(PrimitiveMulticaster.class.getDeclaredMethod("getPrimitives"))) {
				return this.primitives;
			} else {
				Object symbolicReturn = null;
				
				for(Primitive primitive : this.primitives) {
					symbolicReturn = method.invoke(primitive, args);
				}
				
				return symbolicReturn;
			}
		}
	}
}
