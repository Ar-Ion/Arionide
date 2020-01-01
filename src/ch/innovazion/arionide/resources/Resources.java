/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.Workspace;
import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.debugging.IAm;

public class Resources {
	
	private final Workspace workspace;
	private final List<String> resources = new ArrayList<>();
	
	private boolean installed = false;
	
	public Resources(Workspace workspace) {
		this.workspace = workspace;
		
		this.resources.add("font-bitmap");
		this.resources.add("font-meta");
		this.resources.add("edge");
		
		this.installResources();
	}
	
	@IAm("installing the resources")
	private void installResources() {
		for(String name : this.resources) {
			InputStream stream = this.getClass().getResourceAsStream(this.getFullName(name));
			
			try {
				Files.copy(stream, this.getFile(name).toPath());
			} catch(FileAlreadyExistsException e) {
				;
			} catch (IOException exception) {
				Debug.exception(exception);
			}
		}
		
		this.installed = true;
	}
	
	private void checkInstalled() {
		if(!this.installed) {
			throw new IllegalStateException("Resources have not been installed");
		}
	}
	
	@IAm("getting a resource")
	public File getResource(String name) {
		this.checkInstalled();
		
		if(this.resources.contains(name)) {
			return this.getFile(name);
		} else {
			throw new RuntimeException("Resource not found");
		}
	}
	
	private File getFile(String resource) {
		return new File(this.workspace.getPath(), this.getFullName(resource));
	}
	
	private String getFullName(String name) {
		return name.concat(".res");
	}
}