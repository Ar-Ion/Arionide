package org.azentreprise.arionide.ui.overlay.views;

import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.components.Tab;

public class CodeView extends View implements EventHandler {
	public CodeView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);
		
		this.add(new Tab(this, "Inheritance", "Hierarchy", "Call graph"), 0.3f, 0.05f, 0.7f, 0.10f);
	}

	public <T extends Event> void handleEvent(T event) {
		
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return null;
	}
}