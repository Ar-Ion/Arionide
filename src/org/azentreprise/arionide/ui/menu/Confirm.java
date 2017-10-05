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
package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class Confirm extends SpecificMenu {
	
	private static final String yes = "Yes";
	private static final String no = "No";

	private final Menu parent;
	private final Confirmable confirm;
	private final String caption;
	
	public Confirm(AppManager manager, Menu parent, Confirmable confirm, String caption) {
		super(manager, yes, no);
		this.parent = parent;
		this.confirm = confirm;
		this.caption = caption;
	}
	
	public void setCurrent(WorldElement current) {
		super.setCurrent(current);
		this.getAppManager().getEventDispatcher().fire(new MessageEvent(this.caption.replace("$name", current.getName()), MessageType.INFO));
	}
	
	public void onClick(String element) {
		this.parent.show();

		if(element.equals(yes)) {
			this.confirm.confirm();
		}
	}
}