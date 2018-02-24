/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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

import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class SpecificMenu extends Menu {
	
	private WorldElement current;

	protected SpecificMenu(AppManager manager, String... elements) {
		super(manager, elements);
	}
	
	public void setCurrent(WorldElement current) {
		assert current != null;
		this.current = current;
	}
	
	public WorldElement getCurrent() {
		return this.current;
	}
	
	public String getDescription() {
		return this.current != null ? "'" + this.current.getName() + "'" : "nothing";
	}

	public void reload() {
		this.setCurrent(this.getCurrent());
	}
}
