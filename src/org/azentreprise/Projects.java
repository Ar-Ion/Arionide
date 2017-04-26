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
package org.azentreprise;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.azentreprise.configuration.Project;
import org.azentreprise.configuration.WorkspaceConfiguration;

public class Projects {
	
	private static WorkspaceConfiguration workspaceConfiguration;
	private static Project currentProject;
	
	public static void openWorkspace(WorkspaceConfiguration configuration) {
		Projects.workspaceConfiguration = configuration;
	}
	
	public static WorkspaceConfiguration getWorkspaceConfiguration() {
		return Projects.workspaceConfiguration;
	}
	
	public static List<String> getProjectsList() {
		Iterator<String> iterator = Projects.workspaceConfiguration.projects.iterator();
		
		while(iterator.hasNext()) {
			if(!new File(Arionide.getSystemConfiguration().workspaceLocation, iterator.next() + ".proj").exists()) {
				iterator.remove();
			}
		}
		
		return Collections.unmodifiableList(Projects.workspaceConfiguration.projects);
	}
	
	public static Project loadProject(String name) throws IllegalArgumentException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
		Debug.taskBegin("loading a project");
		File project = new File(Arionide.getSystemConfiguration().workspaceLocation, name + ".proj");
		
		if(project.exists()) {
			Projects.currentProject = new Project(project, "Project: " + name);
		} else {
			Projects.createProject(name);
		}
		
		Debug.taskEnd();
		
		return Projects.currentProject;
	}
	
	public static Project createProject(String name) throws IllegalArgumentException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException {
		Debug.taskBegin("creating a new project");
		File project = new File(Arionide.getSystemConfiguration().workspaceLocation, name.replaceAll("\\P{InBasic_Latin}", "_") + ".proj");
		
		if(!project.exists()) {
			project.createNewFile();
			
			if(!Projects.workspaceConfiguration.projects.contains(name)) {
				Projects.workspaceConfiguration.projects.add(name);
				Projects.workspaceConfiguration.save();
			}

			Projects.currentProject = new Project(project, "Project: " + name);
		} else {
			Projects.loadProject(name);
		}
		Debug.taskEnd();
		
		return Projects.currentProject;
	}
	
	public static void repairProject(String name) {
		// TODO
	}
	
	public static Project getCurrentProject() {
		return Projects.currentProject;
	}
}