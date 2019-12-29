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
