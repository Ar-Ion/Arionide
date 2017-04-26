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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.lang.Code;
import org.azentreprise.lang.Data;
import org.azentreprise.lang.ISAMapping;
import org.azentreprise.lang.LangarionError;
import org.azentreprise.lang.Link;
import org.azentreprise.lang.Object;
import org.azentreprise.ui.Style;
import org.azentreprise.ui.views.UIMainView;

public class Project extends Configuration {
	
	public volatile int version = Arionide.getVersion();
	public volatile String name = "Untitled";
	public volatile String owner = "Nobody";
	public volatile String description = "A mysterious project";
	public volatile int pixelsPerCell = 128;
	public volatile int gridPosX = 0;
	public volatile int gridPosY = 0;
	public volatile int gridSize = 256;
	public volatile boolean isGridInitialized = false;
	public volatile String current = ""; // root
	public volatile List<String> serializedObjects = new ArrayList<>(Arrays.asList("Compiler [-2 -1 1 mutable structure]",  "Project [2 -1 0 mutable structure]", "Native [0 2 2 immutable structure]"));
	public volatile	List<String> serializedLinks = new ArrayList<>(Arrays.asList("Compiler<-->Project", "Compiler<-->Native", "Native<-->Project"));
	public volatile List<String> serializedStyles = new ArrayList<>(Arrays.asList("[Name:<Primary> Parent:<None> Red range:<0 0> Green range:<63 127> Blue range:<191 255> Wave control:<10 1> Linear motion matrix:<2 2 2 2 2 2> Rotation matrix:<0 1 1> Alpha constant:<1.0>]", 
																				  "[Name:<Secondary> Parent:<None> Red range:<0 63> Green range:<127 255> Blue range:<0 63> Wave control:<10 1> Linear motion matrix:<2 2 2 2 2 2> Rotation matrix:<0 1 1> Alpha constant:<1.0>]",	
																				  "[Name:<Native> Parent:<None> Red range:<127 255> Green range:<63 127> Blue range:<0 63> Wave control:<10 1> Linear motion matrix:<2 2 2 2 2 2> Rotation matrix:<0 1 1> Alpha constant:<1.0>]"));	

	public final List<Object> objects;
	public final List<Link> links;
	public final List<Style> styles;
	
	public final Map<String, Object> objectMapping = new HashMap<>();
	public final Map<String, Data> globalVariables = new HashMap<>();
	
	public final Code theCode = new Code();
		
	public Project(File path, String identifier) throws IOException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		super(path, identifier);
		
		Debug.taskBegin("loading project " + path.getAbsolutePath());
		
		ISAMapping.loadISADesignObjects("Native.name [x y 2 immutable native]", this.serializedObjects);
		ISAMapping.loadISADesignLinks("Native.x<-->Native.y", this.serializedLinks);
		
		this.loadConfiguration();
		
		if(this.version < Arionide.getBackwardCompatibility()) {
			if(JOptionPane.showConfirmDialog(null, "Outdated project. Do you want to delete it?") == JOptionPane.YES_OPTION) {
				this.delete();
				UIMainView.updatePageID();
			}
			
			throw new RuntimeException("Version " + this.version + " is not compatible anymore...");
		}
		
		this.objects = Parseable.parse(this.serializedObjects, Object.class);
		this.links = Parseable.parse(this.serializedLinks, Link.class);
		this.styles = Parseable.parse(this.serializedStyles, Style.class);
		
		this.createMapping(Object.class, "id", this.objects, this.objectMapping);
		
		Object.uploadStyleList(this.styles);
		Object.uploadCurrentID(this.current);
		
		Link.uploadObjectMapping(this.objectMapping);
		
		try {
			this.theCode.compile(this);
		} catch (LangarionError e) {
			;
		}
		
		Debug.taskEnd();
	}
	
	public void save() throws IllegalArgumentException, IllegalAccessException, IOException {
		Parseable.serialize(this.objects, this.serializedObjects);
		Parseable.serialize(this.links, this.serializedLinks);
		Parseable.serialize(this.styles, this.serializedStyles);
		
		super.save();
	}
	
	public <T> void createMapping(Class<T> clazz, String keyField, List<T> source, Map<String, T> target) {
		for(T object : source) {
			try {
				Field field = clazz.getDeclaredField(keyField);
				boolean accessibility = field.isAccessible();
				field.setAccessible(true);
				target.put((String) field.get(object), object);
				field.setAccessible(accessibility);
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
	}
}