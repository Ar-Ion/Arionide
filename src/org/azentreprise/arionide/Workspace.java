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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;
import org.azentreprise.arionide.debugging.Debug;

public class Workspace implements IWorkspace {
	
	private static final Map<?, ?> workspaceProtocolMapping = new HashMap<>();
	
	static {
		
	}
	
	
	private final File path;
	private final File configurationFile;

	private final Map<String, String> properties = new HashMap<>();
	
	private final List<? super IProject> projects = new ArrayList<>();
	
	public Workspace(File path) {
		this.path = path;
		this.configurationFile = new File(this.path, "workspace.config");
	}
	
	public void load() {
		try {
			this.projects.clear();
		
			File[] files = this.path.listFiles();
			
			for(File potential : files) {
				if(potential.isFile() && potential.getName().endsWith(".proj")) {
					this.projects.add(new Project(potential));
				}
			}
		
			Files.readAllLines(this.configurationFile.toPath()).forEach(property -> {
				String[] elements = property.split("=");
				
				if(elements.length > 1) {
					this.properties.put(elements[0], elements[1]);
				}
			});
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}

	public void save() {
		
	}
	
	public String getName() {
		return "Arionide Workspace";
	}

	public File getPath() {
		return this.path;
	}
	
	public void requestDiscover() {
		this.discover0();
	}
	
	private void discover0() {
		
	}

	public List<? extends IProject> getProjectList() {
		return null;
	}

	public IProject getCurrentProject() {
		return null;
	}

	public void loadProject(String name) {
		
	}

	public void closeProject(String name) {
		
	}

	public void createProject(String name) {
		
	}

	public void deleteProject(String name) {
		
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