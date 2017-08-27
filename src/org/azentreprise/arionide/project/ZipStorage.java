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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;

import com.sun.nio.zipfs.ZipFileSystem;

public class ZipStorage extends Storage {

	private static final Map<String, String> fileSystemEnvironment = new HashMap<>();

	static {
		fileSystemEnvironment.put("create", "true");
	}
	
	private final File location;
	
	private FileSystem fs;
	
	private Path metaPath;
	private Path hierarchyPath;
	private Path inheritancePath;
	private Path callGraphPath;
	private Path historyPath;
	private Path structureMetaPath;
	private Path currentDataPath;

	@IAm("initializing the project storage")
	public ZipStorage(File path) {
		this.location = path;
	}
	
	public void initFS() {
		try {
			this.initFS0();
						
			this.metaPath = this.init(null, null, "meta", "project");
			
			Supplier<List<StructureElement>> supplier = ArrayList<StructureElement>::new;
			
			this.hierarchyPath = this.init(supplier, e -> this.hierarchy = e,"struct", "hierarchy");
			this.inheritancePath = this.init(supplier, e -> this.inheritance = e, "struct", "inheritance");
			this.callGraphPath = this.init(supplier, e -> this.callGraph = e, "struct", "callgraph");
			this.structureMetaPath = this.init(HashMap<Integer, StructureMeta>::new, e -> this.structMeta = e, "struct_meta");
			this.historyPath = this.init(ArrayList<HistoryElement>::new, e -> this.history = e, "history");
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	private void initFS0() throws IOException {
		URI uri = URI.create("jar:".concat(this.location.toURI().toString()));
		
		try {
			this.fs = FileSystems.getFileSystem(uri);
		} catch(Exception e) {
			this.fs = FileSystems.newFileSystem(uri, fileSystemEnvironment);
		}
	}
	
	public void flushFS() {
		try {
			if(this.fs.isOpen()) {
				Method method = ZipFileSystem.class.getDeclaredMethod("sync");
				method.setAccessible(true);
				method.invoke(this.fs);
			}
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}
	
	private <T> Path init(Supplier<T> allocator, Consumer<T> initializer, String first, String... more) throws IOException {
		Path path = this.fs.getPath(first, more);
		
		if(!Files.exists(path)) {
			if(more.length > 0) {
				String[] dirs = new String[more.length - 1];
				
				System.arraycopy(more, 0, dirs, 0, dirs.length);
				
				Files.createDirectories(this.fs.getPath(first, dirs));
			}
			
			Files.createFile(path);
						
			if(allocator != null) {
				T object = allocator.get();
				
				if(initializer != null) {
					initializer.accept(object);
				}
				
				this.save(path, object);
			}
		}
			
		return path;
	}
	
	public File getLocation() {
		return this.location;
	}
		
	public Path getMetaPath() {
		return this.metaPath;
	}
	
	public void loadHierarchy() {	
		this.hierarchy = this.load(this.hierarchyPath);
	}
	
	public void saveHierarchy() {
		this.save(this.hierarchyPath, this.hierarchy);
	}
	
	public void loadInheritance() {	
		this.inheritance = this.load(this.inheritancePath);
	}
	
	public void saveInheritance() {
		this.save(this.inheritancePath, this.inheritance);
	}
	
	public void loadCallGraph() {	
		this.callGraph = this.load(this.callGraphPath);
	}
	
	public void saveCallGraph() {
		this.save(this.callGraphPath, this.callGraph);
	}
	
	public void loadStructureMeta() {	
		this.structMeta = this.load(this.structureMetaPath);
	}
	
	public void saveStructureMeta() {
		this.save(this.structureMetaPath, this.structMeta);
	}
	
	public void loadHistory() {	
		this.history = this.load(this.historyPath);
	}
	
	public void saveHistory() {
		this.save(this.historyPath, this.history);
	}
	
	public void loadData(int id) {
		try {
			this.currentDataPath = this.init(ArrayList<DataElement>::new, e -> this.currentData = e, "data", String.valueOf(id));
		} catch (IOException exception) {
			Debug.exception(exception);
		}
		
		this.currentData = this.load(this.currentDataPath);
	}
	
	public void saveData() {
		if(this.currentDataPath != null) {
			this.save(this.currentDataPath, this.currentData);
		} else {
			throw new IllegalStateException("No data to be saved");
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Serializable> T load(Path path) {
		try(ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path))) {
			return (T) input.readObject();
		} catch (IOException | ClassNotFoundException exception) {
			Debug.exception(exception);
			return null;
		}
	}
	
	private void save(Path path, Object object) {
		
		assert object instanceof Serializable;
		
		try(ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE))) {
			output.writeObject(object);
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}

}
