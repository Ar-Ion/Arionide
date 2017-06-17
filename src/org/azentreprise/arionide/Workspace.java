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
package org.azentreprise.arionide;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.ProjectCloseEvent;
import org.azentreprise.arionide.events.ProjectOpenEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;

public class Workspace implements IWorkspace {
	
	private static final String configurationAssignementSymbol = new String("=");
	
	private static final Map<?, ?> workspaceProtocolMapping = new HashMap<>();
	
	static {
		// add elements to protocol mapping
	}
	
	
	private final File path;
	private final File configurationFile;

	private final IEventDispatcher dispatcher;
	
	private final Map<String, String> properties = new HashMap<>();
	
	private final List<? super IProject> projects = new ArrayList<>();
	private IProject current = null;
	
	public Workspace(File path, IEventDispatcher dispatcher) {
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
			this.projects.clear();
		
			File[] files = this.path.listFiles();
			
			for(File potential : files) {
				if(potential.isFile() && potential.getName().endsWith(".proj")) {
					Project element = new Project(potential);
					
					if(!this.projects.contains(element)) {
						element.load();
						this.projects.add(element);
					}
				}
			}
		
			Files.readAllLines(this.configurationFile.toPath()).forEach(property -> {
				String[] elements = property.split(Workspace.configurationAssignementSymbol);
				
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
							.concat(Workspace.configurationAssignementSymbol)
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

	public List<? super IProject> getProjectList() {
		return this.projects;
	}

	public IProject getCurrentProject() {
		return this.current;
	}

	public void loadProject(IProject project) {
		if(this.current != project) {
			project.load();
			this.current = project;
			this.dispatcher.fire(new ProjectOpenEvent(project));
		}
	}

	public void closeProject(IProject project) {
		project.save();
		this.close0(project);
	}

	public void createProject(String name) {
		IProject project = new Project(new File(this.path, name.toLowerCase().replaceAll(Coder.whitespaceRegex, "_").concat(".proj")));
		project.setProperty("name", name, Coder.stringEncoder);
		project.save();
		
		this.current = project;
		this.dispatcher.fire(new ProjectOpenEvent(project));
	}

	public void deleteProject(IProject project) {
		project.getPath().delete();
		this.projects.remove(project);
		this.close0(project);
	}
	
	private void close0(IProject project) {
		if(this.current == project) {
			this.current = null;
			this.dispatcher.fire(new ProjectCloseEvent());
		}
	}

	public <T> T getProperty(String key, Decoder<T> decoder) {
		return null;
	}

	public <T> void setProperty(String key, T value, Encoder<T> encoder) {
		
	}
	
	public Map<?, ?> getProtocolMapping() {
		return Workspace.workspaceProtocolMapping;
	}
}