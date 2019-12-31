/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.innovazion.arionide.ui.topology.Affine;
import ch.innovazion.arionide.ui.topology.Application;

public class Utils {
	
	private static final Application glTransform = new Affine(1.0f, -1.0f, -1.0f, 1.0f);
	
	public static double fakeComplexPower(double x, double power) {
		return Math.signum(x) * Math.pow(Math.abs(x), power);
	}
	
	public static int search(byte[] data, int start, int length, byte element) {
		while(start < length) {
			if(data[start] == element) {
				return start;
			}
			
			start++;
		}
		
		return -1;
	}
	
	public static int getAlpha(int argb) {
		return (argb >>> 24) & 0xFF;
	}
	
	public static int getRed(int argb) {
		return (argb >>> 16) & 0xFF;
	}
	
	public static int getGreen(int argb) {
		return (argb >>> 8) & 0xFF;
	}
	
	public static int getBlue(int argb) {
		return argb & 0xFF;
	}
	
	public static int packRGB(int red, int green, int blue) {
		checkColorRange("Red", red);
		checkColorRange("Green", green);
		checkColorRange("Blue", blue);
		
		return (red << 16) | (green << 8) | blue;
	}
	
	public static int packARGB(int red, int green, int blue, int alpha) {
		return packARGB(packRGB(red, green, blue), alpha);
	}
	
	public static int packARGB(int rgb, int alpha) {
		checkColorRange("Alpha", alpha);
		
		return (alpha << 24) | rgb;
	}
	
	public static void checkColorRange(String colorName, int color) {		
		if(checkUnsignedByteOverflow(color)) {
			throw new IllegalArgumentException(colorName + " out of range: " + color);
		}
	}
	
	public static boolean checkUnsignedByteOverflow(int value) {
		return value < 0 || value > 255;
	}
	
	public static int brighter(int color, float factor) {
		checkFactorRange(factor);
		
		int red = getRed(color);
		int green = getGreen(color);
		int blue = getBlue(color);
		
		red += (int) ((0xFF - red) * factor);
		green += (int) ((0xFF - green) * factor);
		blue += (int) ((0xFF - blue) * factor);

		return packRGB(red, green, blue);
	}
	
	private static void checkFactorRange(float factor) {
		if(factor < 0.0f || factor > 1.0f) {
			throw new IllegalArgumentException("Factor " + factor + " out of range (0 ≤ f ≤ 1)");
		} 
	}
	
	public static long convertToUnsignedLong(int in) {
		if(in < 0) {
			return (1 << 32) | -in;
		} else {
			return in;
		}
	}
	
	public static Application getLayout2GL() {
		return glTransform;
	}
	
	public static String read(InputStream input) throws IOException {
		if(input == null) {
			throw new FileNotFoundException("Failed to read resource");
		}
		
		byte[] buffer = new byte[128];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int count = 0;
		
		while((count = input.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		
		return new String(baos.toByteArray(), Charset.forName("utf8"));
	}
	
	public <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}
	
	public static <T, K> boolean contains(List<T> list, K key, Function<T, K> extractor) {
		return extract(list, extractor).contains(key);
	}
	
	public static <I, O> List<O> extract(List<I> input, Function<I, O> extractor) {
		return input.stream().map(extractor).collect(Collectors.toCollection(ArrayList::new));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] combine(Class<T> componentType, T[] scope, T... rest) {
		T[] objects = (T[]) Array.newInstance(componentType, scope.length + rest.length);
		
		System.arraycopy(scope, 0, objects, 0, scope.length);
		System.arraycopy(rest, 0, objects, scope.length, rest.length);
		
		return objects;
	}
	
	// Warning: this function modifies and returns the input
	@SafeVarargs
	public static <T> Set<T> combine(Set<T> scope, T... rest) {		
		for(T element : rest) {
			scope.add(element);
		}
		
		return scope;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T bind(Class<T> bindingInterface, T... objects) {
		return (T) Proxy.newProxyInstance(bindingInterface.getClassLoader(), new Class<?>[] { bindingInterface }, (nil, method, args) -> {
			if(method.getReturnType().equals(Void.TYPE)) {
				Stream.of(objects).forEach(t -> {
					try {
						method.invoke(t, args);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				});
				
				return null;
			} else {
				throw new UnsupportedOperationException("Non-void methods unsupported");
			}
		});
	}
	
	@SafeVarargs
	public static <T> Set<T> asSet(T... elements) {
		Set<T> set = new HashSet<T>();
		
		for(T element : elements) {
			set.add(element);
		}
		
		return set;
	}
	
	public static <T> Predicate<T> identityPredicate() {
		return (nil) -> true;
	}
}