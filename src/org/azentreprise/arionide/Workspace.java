package org.azentreprise.arionide;

import java.util.List;

public interface Workspace extends Resource {
	
	public List<? extends Project> getProjectList();
	
	public Project getCurrentProject();
	
	public void loadProject(String name);
	public void closeProject(String name);
}
