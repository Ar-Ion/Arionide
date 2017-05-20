/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.debugging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Debug {
	
	private static PrintStream output = null;
		
	public static void exception(Exception exception) {
		if(Debug.output != null) {
			exception.printStackTrace(Debug.output);
			Debug.output.flush();
		}

		exception.printStackTrace();
		
		String[] stacktrace = Debug.buildNiceStacktrace(exception);
		
		System.out.println(String.join("\n", stacktrace));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "An internal error occured" + (stacktrace.length > 1 ? " " + stacktrace[1] : "") + ".\n\"" + exception.getClass().getSimpleName() + ": " + exception.getMessage() + "\".", "An error occured", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	private static String[] buildNiceStacktrace(Exception exception) {
		List<String> stacktrace = new ArrayList<>();
		
		stacktrace.add("A very sad problem occured to Arionide! It's an " + exception.getClass().getName() + "!");
		
		StackTraceElement[] elements = exception.getStackTrace();
		
		for(StackTraceElement element : elements) {
			try {
				Method[] methods = Class.forName(element.getClassName()).getDeclaredMethods();
				
				
				for(Method method : methods) {
					IAm annotation = method.getAnnotation(IAm.class);
					
					if(annotation != null) {
						stacktrace.add("while " + annotation.value());
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				;
			}
		}
		
		return stacktrace.toArray(new String[0]);
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
