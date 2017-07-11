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
package org.azentreprise.arionide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;

public class LocalProject implements Project {

	public static final long versionUID = 100L;
	
	private static final Map<String, byte[]> projectProtocolMapping = new LinkedHashMap<>();
	
	static {
		projectProtocolMapping.put("version", Long.toString(versionUID).getBytes(Coder.charset));
		projectProtocolMapping.put("name", new String("Undefined").getBytes(Coder.charset));
		projectProtocolMapping.put("definitions", new String("org.azentreprise.arionide.native.NativeDefinitions()").getBytes(Coder.charset));
		projectProtocolMapping.put("runtime", new String("org.azentreprise.arionide.native.NativeRuntime()").getBytes(Coder.charset));
		projectProtocolMapping.put("code", new byte[0]);
	}
	
	
	private final File path;
	private final Map<String, byte[]> properties = new LinkedHashMap<>();
	
	protected LocalProject(File path) {
		this.path = path;
	}
	
	@IAm("loading a project")
	public void load() {
		this.load("Microsoft sucks");
	}
	
	private void load(String neededKey) {
		try {
			if(this.properties.containsKey(neededKey)) {
				return;
			}
			
			this.properties.clear();
			
			byte[] data = Files.readAllBytes(this.path.toPath());
			
			int startIndex = 0;
			int endIndex = 0;
			
			while((endIndex = Utils.search(data, startIndex, data.length, Coder.separator)) > -1 && !this.properties.containsKey(neededKey)) {
				byte[] keyBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, keyBuffer, 0, keyBuffer.length);
								
				startIndex = Utils.search(data, endIndex, data.length, Coder.sectionStart) + 1;
				endIndex = Utils.search(data, startIndex, data.length, Coder.sectionEnd);
				byte[] valueBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, valueBuffer, 0, valueBuffer.length);
				
				this.properties.put(new String(keyBuffer, Coder.charset).replaceAll(Coder.whitespaceRegex, Coder.empty), valueBuffer);
				
				startIndex = endIndex + 1;
			}
			
			System.out.println(this.properties);
						
			this.verifyProtocol();
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}

	@IAm("saving a project")
	public void save() {
		try {
			OutputStream stream = new FileOutputStream(this.path);
			
			this.verifyProtocol();
			
			for(Entry<String, byte[]> property : this.properties.entrySet()) {
				stream.write(property.getKey().getBytes(Coder.charset));
				stream.write(Coder.separator);
				stream.write(Coder.space);
				stream.write(Coder.sectionStart);
				
				if(property.getValue().length > 64) {
					stream.write(Coder.windowsNewline);
					stream.write(Coder.newline);
					stream.write(Coder.tab);
					stream.write(property.getValue());
					stream.write(Coder.windowsNewline);
					stream.write(Coder.newline);
					stream.write(Coder.sectionEnd);
				} else {
					stream.write(property.getValue());
					stream.write(Coder.sectionEnd);
				}
				
				stream.write(Coder.windowsNewline);
				stream.write(Coder.newline);
			}
			
			stream.close();
		} catch(Exception exception) {
			Debug.exception(exception);
		}
	}
	
	private void verifyProtocol() {
		for(Entry<String, byte[]> entry : LocalProject.projectProtocolMapping.entrySet()) {
			if(!this.properties.containsKey(entry.getKey())) {
				this.properties.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public <T> T getProperty(String key, Decoder<T> decoder) {
		
		int hash = this.hashCode();
		
		if(SystemCache.has(key)) {
			return SystemCache.get(hash + key);
		}
		
		T decoded = decoder.decode(this.properties.get(key));
		
		SystemCache.set(hash + key, decoded, SystemCache.NEVER);
		
		return decoded;
	}

	public <T> void setProperty(String key, T value, Encoder<T> encoder) {
		this.properties.put(key, encoder.encode(value));
		SystemCache.set(key, value, SystemCache.NEVER);
	}

	public Map<String, byte[]> getProtocolMapping() {
		return LocalProject.projectProtocolMapping;
	}
	
	public String getName() {
		this.load("name");
		return this.getProperty("name", Coder.stringDecoder);
	}

	public File getPath() {
		return this.path;
	}

	public boolean checkVersionCompatibility() {
		this.load("version");
		return this.getProperty("version", Coder.integerDecoder) == LocalProject.versionUID;
	}
	
	public boolean equals(Object other) {
		return other instanceof LocalProject && this.getPath() == ((LocalProject) other).getPath();
	}
}