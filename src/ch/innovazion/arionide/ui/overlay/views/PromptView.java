package ch.innovazion.arionide.ui.overlay.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Component;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.components.Button;

public abstract class PromptView extends OverlayView implements EventHandler {
	
	private final List<Component> buttons = new ArrayList<>();
	private final Map<String, Consumer<View>> actions = new HashMap<>();
	
	public PromptView(AppManager appManager, LayoutManager layoutManager, int numButtons) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.3f, 0.35f, 0.7f, 0.65f);
		
		setBorderColor(ApplicationTints.MAIN_COLOR);
		
		float horizontalMargin = 0.05f;
		
		for(int i = 0; i < numButtons; i++) {
			float width = (1.0f - horizontalMargin) / numButtons;
			this.add(new Button(this, "<Undefined>").enclose(buttons), horizontalMargin + width*i, 0.75f, width*(i + 1), 0.9f);
		}
		
		appManager.getEventDispatcher().registerHandler(this);
	}
	
	public void viewWillAppear() {				
		setupFocusCycle();
		getAppManager().getFocusManager().request(1);
	}
	
	public PromptView setButtons(String... labels) {
		if(labels.length >= buttons.size()) {
			for(int i = 0; i < buttons.size(); i++) {
				((Button) buttons.get(i)).setSignal("prompt", labels[i]).setLabel(labels[i]);
			}
		} else {
			System.err.println("Failed to set labels for prompt view.");
		}
		
		return this;
	}
	
	public PromptView react(String signal, Consumer<View> action) {
		actions.put(signal, action);
		return this;
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "prompt")) {
				String actionIdentifier = (String) click.getData()[0];
				actions.getOrDefault(actionIdentifier, (nil) -> {}).accept(this);
			}
		}
	}

	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(ClickEvent.class);
	}
}
