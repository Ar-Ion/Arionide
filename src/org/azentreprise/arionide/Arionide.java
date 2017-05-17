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
		CRASH
	}
	
	@IAm("setting up the workspace")
	public IWorkspace setupWorkspace();
	
	@IAm("preparing the core threads for processing")
	public void startThreads();
	
	@IAm("setting up the event dispatcher")
	public EventDispatcher setupEventDispatcher();
	
	@IAm("setting up the application drawing context")
	public AppDrawingContext setupAppDrawingContext();

	@IAm("loading the system resources")
	public Resources loadResources(IWorkspace workspace, AppDrawingContext context);
	
	@IAm("loading the core renderer")
	public CoreRenderer loadCoreRenderer(AppDrawingContext context, EventDispatcher dispatcher, Resources resources);
	
	@IAm("setting up the layout manager")
	public LayoutManager setupLayoutManager(AppDrawingContext context, EventDispatcher dispatcher);
	
	@IAm("showing up the user interface")
	public void loadUI(Arionide theInstance, IWorkspace workspace, AppDrawingContext context, EventDispatcher dispatcher, Resources resources, CoreRenderer renderer, LayoutManager manager);
	
	@IAm("running the watchdog")
	public WatchdogState runWatchdog();

	@IAm("shutting down")
	public void shutdown();
}