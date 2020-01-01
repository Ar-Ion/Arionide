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

import java.util.List;
import java.util.Map;

import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.project.mutables.MutableCodeChain;
import ch.innovazion.arionide.project.mutables.MutableHierarchyElement;
import ch.innovazion.arionide.project.mutables.MutableHistoryElement;
import ch.innovazion.arionide.project.mutables.MutableInheritanceElement;
import ch.innovazion.arionide.project.mutables.MutableStructureMeta;

public abstract class Manager {
	
	private static final String ACK_STRING = "°";
	
	private final Storage storage;
	
	protected Manager(Storage storage) {
		this.storage = storage;
	}
	
	protected List<MutableHierarchyElement> getHierarchy() {
		return storage.getMutableHierarchy();
	}
	
	protected Map<Integer, MutableInheritanceElement> getInheritance() {
		return storage.getMutableInheritance();
	}
	
	protected List<MutableHierarchyElement> getCallGraph() {
		return storage.getMutableCallGraph();
	}
	
	protected Map<Integer, MutableStructureMeta> getMeta() {
		return storage.getMutableStructureMeta();
	}
	
	protected List<MutableHistoryElement> getHistory() {
		return storage.getMutableHistory();
	}
	
	protected Map<Integer, MutableCodeChain> getCode() {
		return storage.getMutableCode();
	}
	
	protected void saveHierarchy() {
		try {
			storage.saveHierarchy();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveInheritance() {
		try {
			storage.saveInheritance();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}

	protected void saveCallGraph() {
		try {
			storage.saveCallGraph();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveMeta() {
		try {
			storage.saveStructureMeta();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveHistory() {
		try {
			storage.saveHistory();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void saveCode() {
		try {
			storage.saveCode();
		} catch (StorageException exception) {
			Debug.exception(exception);
		}
	}
	
	protected Storage getStorage() {
		return storage;
	}
	
	protected MessageEvent success() {
		return new MessageEvent(ACK_STRING, MessageType.SUCCESS);
	}
	
	protected MessageEvent warn() {
		return new MessageEvent(ACK_STRING, MessageType.SUCCESS);
	}
}