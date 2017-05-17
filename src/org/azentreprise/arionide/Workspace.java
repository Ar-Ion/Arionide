package org.azentreprise.arionide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;

public class Workspace implements IWorkspace {
	
	private final File path;
	private final List<? super IProject> projects = new ArrayList<>();
	
	public Workspace(File path) {
		this.path = path;
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
		this.projects.clear();
		
		File[] files = this.path.listFiles();
		
		for(File potential : files) {
			if(potential.isFile() && potential.getName().endsWith(".proj")) {
				this.projects.add(new Project(potential));
			}
		}
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

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
}