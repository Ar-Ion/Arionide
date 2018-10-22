package org.azentreprise.arionide.events;

import org.azentreprise.arionide.project.Project;

public class ProjectEvent extends Event {
	
	private final Project project;
	
	public ProjectEvent(Project project) {
		this.project = project;
	}
	
	public Project getProject() {
		return this.project;
	}
}
