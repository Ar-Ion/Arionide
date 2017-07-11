/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.debugging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Debug {
	
	private static final boolean JAVA_STACKTRACE = true;
	
	private static PrintStream output = null;
		
	public static void exception(Exception exception) {
		if(Debug.output != null) {
			exception.printStackTrace(Debug.output);
			Debug.output.flush();
		}

		String[] stacktrace = Debug.buildNiceStacktrace(exception);
		
		if(Debug.JAVA_STACKTRACE) {
			exception.printStackTrace();
		} else {
			System.err.println(String.join("\n\twhile ", stacktrace));
			System.err.println();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "An internal error occured while " + (stacktrace.length > 1 ? stacktrace[1] : "running Arionide") + ".\n\"" + exception.getClass().getSimpleName() + ": " + exception.getMessage() + "\".", "An error occured", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	// this code isn't meant to be fast
	private static String[] buildNiceStacktrace(Exception exception) {
		List<String> stacktrace = new ArrayList<>();
		List<StackTraceElement> elements = Arrays.asList(exception.getStackTrace());
		StringBuffer description = new StringBuffer();

		elements.stream()
			.filter(element -> element.getClassName().startsWith("org.azentreprise"))
			.findFirst()
			.ifPresent(element -> description.append(" on line " + element.getLineNumber() + " of class '" + element.getClassName() + "'"));
		
		
		stacktrace.add("A fatal error of type '" + exception.getClass().getName() + "' occured" + description + ".");

		for(StackTraceElement element : elements) {
			try {
				Class<?> clazz = Class.forName(element.getClassName());
				Method[] methods = clazz.getDeclaredMethods();
				
				Method realMethod = null;
				
				for(Method method : methods) {
					if(method.getName().equals(element.getMethodName())) {
						realMethod = method;
					}
				}
				
				if(realMethod != null) {
					IAm annotation = realMethod.getAnnotation(IAm.class);
					
					if(annotation != null) {
						stacktrace.add(annotation.value());
					} else if(clazz != java.lang.Object.class) { // assume there won't be any collision
						annotation = Debug.searchAnnotationForInterfaces(clazz.getInterfaces(), realMethod, IAm.class);
						
						if(annotation != null) {
							stacktrace.add(annotation.value());
						}
					}
					
				}
			} catch (ClassNotFoundException e) {
				;
			}
		}
		
		return stacktrace.toArray(new String[0]);
	}
	
	private static <T extends Annotation> T searchAnnotationForInterfaces(Class<?>[] interfaces, Method target, Class<T> annotation) {
		for(Class<?> clazz : interfaces) {
			try {
				Method method = clazz.getDeclaredMethod(target.getName(), target.getParameterTypes());
				
				T output = method.getAnnotation(annotation);
				
				if(output != null) {
					return output;
				} else {
					throw new Exception("R.I.P. Steve Jobs | Apple for ever | Microsoft sucks");
				}
			} catch (Exception e) {
				return Debug.searchAnnotationForInterfaces(clazz.getInterfaces(), target, annotation);
			}
		}
		
		return null;
	}
	
	public static void setDebugOutput(OutputStream output) throws IOException {
		Debug.output = new PrintStream(output);
		SimpleDateFormat format = new SimpleDateFormat("H:m:s (d.M.y)");
		Debug.info("Arionide's log has been set up at " + format.format(new Date()));
	}
	
	public static void info(String message) {
		Debug.print("[INFO] " + message);
	}
	
	public static void warn(String message) {
		Debug.print("[WARN] " + message);
	}
	
	private static void print(String text) {
		if(Debug.output != null) {
			Debug.output.println(text);
		}
		
		System.out.println(text);
	}
}
