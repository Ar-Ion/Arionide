/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.Workspace;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.ui.AppDrawingContext;

public class Resources {
	
	private final Workspace workspace;
	
	private final List<String> resources = new ArrayList<>();
	
	public Resources(Workspace workspace, AppDrawingContext context) {
		this.workspace = workspace;
		
		this.resources.add("font");
	
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
	}
	
	@IAm("getting a resource")
	public File getResource(String name) {
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