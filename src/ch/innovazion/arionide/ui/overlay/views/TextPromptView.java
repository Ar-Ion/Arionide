package ch.innovazion.arionide.ui.overlay.views;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class TextPromptView extends PromptView {
	
	private final Label label;
	
	public TextPromptView(AppManager appManager, LayoutManager layoutManager, int numButtons) {
		super(appManager, layoutManager, numButtons);
		
		this.add(this.label = new Label(this, "<Undefined>"), 0.1f, 0.1f, 0.9f, 0.65f);
	}
	
	public TextPromptView setText(String text) {
		label.setLabel(text);
		return this;
	}
}
