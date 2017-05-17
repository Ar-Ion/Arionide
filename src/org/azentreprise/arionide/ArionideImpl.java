package org.azentreprise.arionide;

import java.io.File;

import org.azentreprise.arionide.events.EventDispatcher;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.Resources;

public class ArionideImpl implements Arionide {

	public IWorkspace setupWorkspace() {
		File path = new File(System.getProperty("user.home") + File.separator + "Arionide Workspace");
		return new Workspace(path);
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

	public Resources loadResources(IWorkspace workspace, AppDrawingContext context) {
		return null;
	}

	public CoreRenderer loadCoreRenderer(AppDrawingContext context, EventDispatcher dispatcher, Resources resources) {
		return null;
	}

	public LayoutManager setupLayoutManager(AppDrawingContext context, EventDispatcher dispatcher) {
		return null;
	}

	public void loadUI(Arionide theInstance, IWorkspace workspace, AppDrawingContext context, EventDispatcher dispatcher, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		
	}

}
