package org.azentreprise.arionide;

import org.azentreprise.arionide.events.EventDispatcher;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.Resources;

public class ArionideImpl implements Arionide {

	public void setupWorkspace() {
		
	}

	public void startThreads() {
		
	}

	public AppDrawingContext setupAppDrawingContext() {
		return null;
	}

	public EventDispatcher setupEventDispatcher() {
		return null;
	}

	public Resources loadResources() {
		return null;
	}

	public CoreRenderer loadCoreRenderer() {
		return null;
	}

	public LayoutManager setupLayoutManager() {
		return null;
	}

	public void showUI() {
		
	}

	public WatchdogState runWatchdog() {
		return WatchdogState.NO_PROBLEM;
	}

	public void shutdown() {
		
	}

}
