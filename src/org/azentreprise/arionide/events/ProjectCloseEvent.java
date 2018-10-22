package org.azentreprise.arionide.events;

import org.azentreprise.arionide.project.Project;

public class ProjectCloseEvent extends ProjectEvent {
	public ProjectCloseEvent(Project project) {
		super(project);
	}
}
