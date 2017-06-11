package org.azentreprise.arionide.ui.overlay.views;

import java.util.List;

import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;

public class CodeView extends View implements EventHandler {
	public CodeView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
	}

	public <T extends Event> void handleEvent(T event) {
		
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return null;
	}
}