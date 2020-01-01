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
package ch.innovazion.arionide.ui.overlay.views;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class TextPromptView extends PromptView {
	
	private final Label primary;
	private final Label secondary;
	
	public TextPromptView(AppManager appManager, LayoutManager layoutManager, int numButtons) {
		super(appManager, layoutManager, numButtons);
		
		this.add(this.primary = new Label(this, "").setAlpha(255), 0.1f, 0.1f, 0.9f, 0.45f);
		this.add(this.secondary = new Label(this, "").setAlpha(191), 0.1f, 0.45f, 0.9f, 0.65f);
	}
	
	public TextPromptView setPrimaryText(String text) {
		primary.setLabel(text);
		return this;
	}
	
	public TextPromptView setSecondaryText(String text) {
		secondary.setLabel(text);
		return this;
	}
}
