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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.azentreprise.Debug;

public class Configuration {
	
	private BufferedWriter writer;
	private final File path;
	private final String identifier;
	
	protected Configuration(File path, String identifier) throws IOException {		
		this.identifier = identifier;
		this.path = path;
		this.path.getParentFile().mkdirs();
		this.path.createNewFile();
	}
	
	protected void loadConfiguration() throws IOException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Debug.taskBegin("loading configuration [" + this.identifier + "]");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.path)));
		String buffer = null;
		int currentLine = 0;
		while((buffer = reader.readLine()) != null) {
			currentLine++;
			
			try {
				this.decodeAssignement(buffer, currentLine);
			} catch (NoSuchFieldException e) {
				;
			}
		}
		reader.close();
						
		Debug.taskEnd();
	}
	
	public void save(boolean preRegen) throws IOException, IllegalArgumentException, IllegalAccessException {
		Debug.taskBegin("flushing [" + this.identifier + "]");
		
		if(preRegen) {
			this.regenWriter();
		}
				
		Field[] fields = this.getClass().getDeclaredFields();
		
		for(Field field : fields) {
			if(Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				this.encodeAssignement(field);
			}
		}
		
		this.writer.flush();
		this.writer.close();
		
		if(!preRegen) {
			this.regenWriter();
		}
		
		Debug.taskEnd();
	}
	
	
	public void save() throws IllegalArgumentException, IllegalAccessException, IOException {
		this.save(true);
	}
	
	public void delete() throws IOException {
		this.regenWriter();
		this.writer.close();
		this.path.delete();
	}
	
	private void regenWriter() throws FileNotFoundException {
		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.path)));
	}
	
	private void decodeAssignement(String assignement, int lineNumber) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Debug.taskBegin("decoding line " + lineNumber + " of [" + this.identifier + "]");
		String[] splitted = assignement.split("=");
		if(splitted.length > 0) {
			Field field = this.getClass().getField(splitted[0]);
			
			Object value = this.decode0(field.getType().getName(), splitted.length != 2 ? "" : splitted[1]);
			
			field.set(this, value);
			
			Debug.info(field.getName() + "=" + field.get(this));
		} else {
			throw new IOException("Invalid syntax: field_name=field_value");
		}
		Debug.taskEnd();
	}
	
	private Object decode0(String type, String value) throws IOException {
		switch(type) {
			case "int":
				return Integer.parseInt(value);
			case "float":
				return Float.parseFloat(value);
			case "boolean":
				return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value == "1";
			case "java.lang.String":
				return value;
			case "java.util.List":
				String list = value.substring(1, value.length() - 1);
				List<String> array = new ArrayList<String>();
				
				if(!list.isEmpty()) {
					String[] entries = list.split(", ");
					for(String entry : entries) {
						array.add(entry);
					}
				}
				
				return array;
			case "java.util.Map":
				String[] map = value.substring(1, value.length() - 1).split(", ");
				
				HashMap<String, String> hashMap = new HashMap<String, String>();
				
				for(String entry : map) {
					String[] splittedEntry = entry.split("=");
					hashMap.put(splittedEntry[0], splittedEntry[1]);
				}
				
				return hashMap;
			case "java.io.File":
				return new File(value);
			default:
				throw new IOException("This field is not configurable (" + type + ")");
		}
	}
	
	private void encodeAssignement(Field field) throws IOException, IllegalArgumentException, IllegalAccessException {
		Debug.taskBegin("encoding assignement " + field.getName() + " for [" + this.identifier + "]");
		String encoded = field.get(this).toString();
		this.writer.write(field.getName());
		this.writer.write("=");
		this.writer.write(encoded);
		this.writer.newLine();
		Debug.info(field.getName() + "=" + encoded);
		Debug.taskEnd();
	}
	
	public File getFile() {
		return this.path;
	}
}