/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018, 2019 AZEntreprise Corporation. All rights reserved.
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
package ch.innovazion.arionide.menu;

import ch.innovazion.arionide.ui.ApplicationTints;

public class Confirm extends Menu {
	
	private static final String yes = "Yes";
	private static final String no = "No";

	private final Confirmable confirm;
	private final String caption;
	private MenuDescription description;
	
	public Confirm(Menu parent, Confirmable confirm, String caption) {
		super(parent, yes, no);
		this.confirm = confirm;
		this.caption = caption;
	}
	
	public void onClick(String element) {		
		if(element.equals(yes)) {
			confirm.confirm();
		}
		
		back();
	}
	
	public void show() {
		super.show();
		description = new MenuDescription(ApplicationTints.DANGER_COLOR, caption.replace("$name", getTarget().getName()));
	}
	
	public MenuDescription getDescription() {
		return description;
	}
}