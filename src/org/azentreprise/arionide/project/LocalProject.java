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
package org.azentreprise.arionide.project;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.azentreprise.arionide.SystemCache;
import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.lang.CoreDataManager;
import org.azentreprise.arionide.lang.Language;
import org.azentreprise.arionide.lang.natives.NativeCompiler;
import org.azentreprise.arionide.lang.natives.NativeInstructionSet;
import org.azentreprise.arionide.lang.natives.NativeTypes;

public class LocalProject implements Project {

	public static final long versionUID = 176L;
	
	private static final Map<String, byte[]> projectProtocolMapping = new LinkedHashMap<>();
	
	static {
		projectProtocolMapping.put("version", Long.toString(versionUID).getBytes(Coder.charset));
		projectProtocolMapping.put("name", new String("Undefined").getBytes(Coder.charset));
		projectProtocolMapping.put("structureGen", Integer.toString(0).getBytes(Coder.charset));
		projectProtocolMapping.put("seed", Long.toString(new Random().nextLong()).getBytes(Coder.charset));
		projectProtocolMapping.put("definitions", new String("org.azentreprise.arionide.native.NativeDefinitions()").getBytes(Coder.charset));
		projectProtocolMapping.put("runtime", new String("org.azentreprise.arionide.native.NativeRuntime()").getBytes(Coder.charset));
	}
	
	private final ZipStorage storage;
	private final DataManager manager;
	private final Map<String, byte[]> properties = new LinkedHashMap<>();
	
	private Language language;
	
	public LocalProject(File path) {
		this.storage = new ZipStorage(path);
		this.manager = new DataManager(this);
	}
	
	public void initFS() {
		if(!this.properties.isEmpty()) {
			return;
		}
		
		this.storage.initFS();
		
		Runtime.getRuntime().addShutdownHook(new Thread(this::closeFS));
		
		this.storage.loadHierarchy();
		this.storage.loadInheritance();
		this.storage.loadCallGraph();
		this.storage.loadStructureMeta();
		this.storage.loadHistory();
	}
	
	private void closeFS() {
		this.save();
		this.storage.closeFS();
	}
	
	@IAm("loading a project")
	public void load() {
		if(!this.properties.isEmpty()) {
			return;
		}
		
		try {
			byte[] data = Files.readAllBytes(this.storage.getMetaPath());
			
			int startIndex = 0;
			int endIndex = 0;
			
			while((endIndex = Utils.search(data, startIndex, data.length, Coder.separator)) > -1) {
				byte[] keyBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, keyBuffer, 0, keyBuffer.length);
								
				startIndex = Utils.search(data, endIndex, data.length, Coder.sectionStart) + 1;
				endIndex = Utils.search(data, startIndex, data.length, Coder.sectionEnd);
				byte[] valueBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, valueBuffer, 0, valueBuffer.length);
				
				this.properties.put(new String(keyBuffer, Coder.charset).replaceAll(Coder.whitespaceRegex, Coder.empty), valueBuffer);
				
				startIndex = endIndex + 1;
			}
			
			this.verifyProtocol();
			
			this.language = new Language(new CoreDataManager(), new NativeTypes(), new NativeInstructionSet(this), new NativeCompiler(this));
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}

	@IAm("saving a project")
	public void save() {
		try(OutputStream stream = Files.newOutputStream(this.storage.getMetaPath(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
						
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
		String hash = this.hashCode() + key;
				
		if(SystemCache.has(hash)) {
			return SystemCache.get(hash);
		}
		
		T decoded = decoder.decode(this.properties.get(key));
		
		SystemCache.set(hash, decoded, SystemCache.NEVER);
		
		return decoded;
	}

	public <T> void setProperty(String key, T value, Encoder<T> encoder) {
		this.properties.put(key, encoder.encode(value));
		SystemCache.set(this.hashCode() + key, value, SystemCache.NEVER);
	}

	public Map<String, byte[]> getProtocolMapping() {
		return LocalProject.projectProtocolMapping;
	}
	
	public String getName() {
		return this.getProperty("name", Coder.stringDecoder);
	}

	public File getPath() {
		return this.storage.getLocation();
	}
	
	public Storage getStorage() {
		return this.storage;
	}

	public DataManager getDataManager() {
		return this.manager;
	}
	
	public Language getLanguage() {
		return this.language;
	}
	
	public boolean checkVersionCompatibility() {
		return this.getProperty("version", Coder.integerDecoder) == LocalProject.versionUID;
	}
	
	public boolean equals(Object other) {
		if(other instanceof LocalProject) {
			if(this.getPath().equals(((LocalProject) other).getPath())) {
				return true;
			}
		}

		return false;
	}
	
	public int hashCode() {
		return this.getPath().hashCode();
	}
}