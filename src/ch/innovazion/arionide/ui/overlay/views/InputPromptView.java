/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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

import java.util.function.Consumer;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.components.Input;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class InputPromptView extends PromptView {
	
	private final Label label;
	private final Input input;
	
	private Consumer<String> responder = (nil) -> {};
	
	public InputPromptView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager, 2);
		
		setButtons("Ok", "Cancel");
		react("Cancel", View::discard);
		react("Ok", this::onInputValidated);
		
		this.add(this.label = new Label(this, new String()), 0.1f, 0.1f, 0.9f, 0.25f);
		this.add(this.input = new Input(this, new String()), 0.1f, 0.3f, 0.9f, 0.65f);
	}
	
	public void viewWillAppear() {				
		super.viewWillAppear();
		getAppManager().getFocusManager().request(input);
	}
	
	private void onInputValidated(View nil) {
		String text = input.toString();
		
		if(text != null && !text.isEmpty()) {
			responder.accept(text);
			input.setText(new String());
			discard();
		}
	}

	public InputPromptView setText(String text) {
		label.setLabel(text);
		return this;
	}
	
	public InputPromptView setPlaceholder(String placeholder) {
		input.setPlaceholder(placeholder);
		return this;
	}
	
	public InputPromptView setResponder(Consumer<String> responder) {
		this.responder = responder;
		return this;
	}
}
