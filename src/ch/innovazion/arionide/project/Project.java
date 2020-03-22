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
package ch.innovazion.arionide.project;

import ch.innovazion.arionide.Loadable;
import ch.innovazion.arionide.MappedStructure;
import ch.innovazion.arionide.Resource;
import ch.innovazion.arionide.Saveable;
import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.project.managers.StructureManager;

public interface Project extends Resource, MappedStructure, Loadable, Saveable {
	
	@IAm("checking the version compatibility")
	public boolean checkVersionCompatibility();
	
	@IAm("initializing the project file system")
	public void initFS();
	
	@IAm("loading a project's metadata")
	public void loadMeta() throws StorageException;
		
	public Storage getStorage();
	
	public StructureManager getStructureManager();
}