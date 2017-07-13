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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Debug {
	
	private static PrintStream output = null;
	private static final Stack<String> tasks = new Stack<String>();
	
	public static void taskBegin(String description) {
		if(System.getProperty("debug") != null) {
			Debug.print("Task '" + description + "' has begun.");
		}
		
		Debug.tasks.push(description);
	}
	
	public static void taskEnd() {
		if(System.getProperty("debug") != null) {
			Debug.print("Task '" + Debug.tasks.pop() + "' has terminated.");
		}
	}
		
	public static void exception(Exception exception) {
		if(Debug.output != null) {
			exception.printStackTrace(Debug.output);
			Debug.output.flush();
		}

		exception.printStackTrace();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "An internal error occured while " + Debug.tasks.peek() + ".\n\"" + exception.getClass().getSimpleName() + ": " + exception.getMessage() + "\".", "An error occured", JOptionPane.ERROR_MESSAGE);
			}
		});
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
	
	public static String getExecutionState() {
		return Debug.tasks.peek();
	}
	
	private static void print(String text) {
		if(Debug.output != null) {
			Debug.print(text, Debug.output);
		}
		
		Debug.print(text, System.out);
	}
	
	private static void print(String text, PrintStream stream) {
		
		stream.println(text);
	}
}
