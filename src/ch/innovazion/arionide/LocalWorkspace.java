/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.innovazion.arionide.coders.Coder;
import ch.innovazion.arionide.coders.Decoder;
import ch.innovazion.arionide.coders.Encoder;
import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.events.ProjectCloseEvent;
import ch.innovazion.arionide.events.ProjectOpenEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.project.LocalProject;
import ch.innovazion.arionide.project.Project;

public class LocalWorkspace implements Workspace {
	
	private static final String configurationAssignementSymbol = new String("=");
	
	private static final Map<?, ?> workspaceProtocolMapping = new LinkedHashMap<>();
	
	static {
		// add elements to protocol mapping
	}
	
	
	private final File path;
	private final File configurationFile;

	private final IEventDispatcher dispatcher;
	
	private final Map<String, String> properties = new LinkedHashMap<>();
	
	private final List<? super Project> projects = new ArrayList<>();
	private Project current = null;
	
	public LocalWorkspace(File path, IEventDispatcher dispatcher) {
		this.path = path;
		this.configurationFile = new File(this.path, "workspace.config");
		this.dispatcher = dispatcher;
		
		if(!this.configurationFile.exists()) {
			this.save();
		}
		
		this.load();
	}
		
	@IAm("loading the workspace")
	public void load() {
		try {		
			File[] files = this.path.listFiles();
			
			for(File potential : files) {
				if(potential.isFile() && potential.getName().endsWith(".proj")) {
					LocalProject element = new LocalProject(potential);
					
					if(!this.projects.contains(element)) {
						element.initFS();
						element.load();
						
						if(element.checkVersionCompatibility()) {
							this.projects.add(element);
						}
					}
				}
			}
		
			Files.readAllLines(this.configurationFile.toPath()).forEach(property -> {
				String[] elements = property.split(LocalWorkspace.configurationAssignementSymbol);
				
				if(elements.length > 1) {
					this.properties.put(elements[0], elements[1]);
				}
			});
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}

	@IAm("saving the workspace")
	public void save() {
		try {
			Files.write(this.configurationFile.toPath(), this.properties.entrySet().stream()
					.map(entry -> entry.getKey()
							.concat(LocalWorkspace.configurationAssignementSymbol)
							.concat(entry.getValue()))
					.collect(Collectors.toList()), Coder.charset);
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public String getName() {
		return "Arionide Workspace";
	}

	public File getPath() {
		return this.path;
	}

	public List<? super Project> getProjectList() {
		return this.projects;
	}

	public Project getCurrentProject() {
		return this.current;
	}

	public void loadProject(Project project) {
		if(this.current != project) {
			this.current = project;
			this.dispatcher.fire(new ProjectOpenEvent(project));
		}
	}

	public void closeProject(Project project) {
		if(this.current == project) {
			this.current = null;
			this.dispatcher.fire(new ProjectCloseEvent(project));
		}
	}

	public void createProject(String name) throws IOException {
		File file = new File(this.path, name.toLowerCase().replaceAll(Coder.whitespaceRegex, "_").concat(".proj"));
		
		if(!file.exists()) {
			Project project = new LocalProject(file);
			
			project.initFS();
			project.load();
			
			project.setProperty("name", name, Coder.stringEncoder);
			
			this.projects.add(project);
			
			this.current = project;
			this.dispatcher.fire(new ProjectOpenEvent(project));
		} else {
			throw new IOException("This name can't be used.");
		}
	}

	public void deleteProject(Project project) {
		project.getPath().delete();
		this.projects.remove(project);
		this.closeProject(project);
	}

	public <T> T getProperty(String key, Decoder<T> decoder) {
		return null;
	}

	public <T> void setProperty(String key, T value, Encoder<T> encoder) {
		
	}

	public Map<?, ?> getProtocolMapping() {
		return LocalWorkspace.workspaceProtocolMapping;
	}
}