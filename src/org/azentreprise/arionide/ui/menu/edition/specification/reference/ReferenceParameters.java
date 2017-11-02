/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.menu.edition.specification.reference;

import java.util.List;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Specification;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceParameters extends Menu {
	
	private final Menu parent;
	private final Specification spec;
	private final int id;
	private final List<Data> parameters;
	private final ReferenceParametersEditor paramsEditor;
	
	protected ReferenceParameters(AppManager manager, Menu parent, Specification spec, int id, List<Data> parameters) {
		super(manager, "Back", "Add");
		
		this.parent = parent;
		this.spec = spec;
		this.id = id;
		this.parameters = parameters;
		this.paramsEditor = new ReferenceParametersEditor(manager, parent);
		
		for(Data data : parameters) {
			this.getElements().add(data.getName());
		}
		
		this.setMenuCursor(1);
	}
	
	public void onClick(int id) {
		if(id == 0) {
			this.parent.show();
		} else if(id == 1) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the name for the new parameter", "New parameter", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					Data data = new Data(name, null, -1);
					this.parameters.add(data);
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("Parameter successfully added", MessageType.SUCCESS));
				}
			}).start();
		} else {
			id -= 2;
			
			this.paramsEditor.setTarget(this.spec, this.id, id, this.parameters.get(id));
			this.paramsEditor.show();
		}
	}
}