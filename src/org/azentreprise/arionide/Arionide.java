package org.azentreprise.arionide;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.events.EventDispatcher;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.Resources;

public interface Arionide {
	
	public static enum WatchdogState {
		NO_PROBLEM,
		SHUTDOWN,
		RESTART,
		BUG,
		CRASH
	}
	
	@IAm("setting up the workspace")
	public void setupWorkspace();
	
	@IAm("preparing the core threads for processing")
	public void startThreads();
	
	@IAm("setting up the application drawing context")
	public AppDrawingContext setupAppDrawingContext();
	
	@IAm("setting up the event dispatcher")
	public EventDispatcher setupEventDispatcher();
	
	@IAm("loading the system resources")
	public Resources loadResources();
	
	@IAm("loading the core renderer")
	public CoreRenderer loadCoreRenderer();
	
	@IAm("setting up the layout manager")
	public LayoutManager setupLayoutManager();
	
	@IAm("showing up the user interface")
	public void showUI();
	
	@IAm("running the watchdog")
	public WatchdogState runWatchdog();

	@IAm("shutting down")
	public void shutdown();
}