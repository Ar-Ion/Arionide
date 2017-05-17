package org.azentreprise.arionide;

import java.util.List;

public interface IWorkspace extends Resource, IMappedStructure, ISaveable {
	
	public List<? extends IProject> getProjectList();
	
	public IProject getCurrentProject();
	
	public void loadProject(String name);
	public void closeProject(String name);
	
	public void createProject(String name);
	public void deleteProject(String name);
}
