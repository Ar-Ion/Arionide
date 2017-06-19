package org.azentreprise.arionide.ui.overlay.views;

import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.IProject;
import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.Scroll;
import org.azentreprise.arionide.ui.overlay.components.Tab;

public class CodeView extends View implements EventHandler {
	
	private IProject currentProject;
	
	public CodeView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);
		
		this.add(new Button(this, "<").setSignal("back"), 0.05f, 0.05f, 0.15f, 0.1f);
		this.add(new Tab(this, "Inheritance", "Hierarchy", "Call graph").setSignal("sceneChanged"), 0.3f, 0.05f, 0.7f, 0.1f);
		this.add(new Button(this, "Run").setSignal("run"), 0.85f, 0.05f, 0.95f, 0.1f);
		
		this.add(new Button(this, "+").setSignal("add"), 0.05f, 0.85f, 0.15f, 0.95f);
		this.add(new Scroll(this, "salut", "sd", "sigma","salut", "sd", "sigma","salut", "sd", "sigma"), 0.05f, 0.85f, 0.95f, 0.95f);
		this.add(new Button(this, "...").setSignal("more"), 0.05f, 0.85f, 0.15f, 0.95f);

		this.getAppManager().getEventDispatcher().registerHandler(this);
	}
	
	public void show() {
		super.show();
		this.setupFocusCycle(2, 0);
		this.currentProject = this.getAppManager().getWorkspace().getCurrentProject();
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "back")) {
				this.openView(Views.main);
			} else if(click.isTargetting(this, "sceneChanged")) {
				int tabID = (int) click.getData()[0];
				
				switch(tabID) {
					case 0:
						this.getAppManager().getCoreRenderer().setScene(RenderingScene.INHERITANCE);
						break;
					case 1:
						this.getAppManager().getCoreRenderer().setScene(RenderingScene.HIERARCHY);
						break;
					case 2:
						this.getAppManager().getCoreRenderer().setScene(RenderingScene.CALLGRAPH);
						break;
					default:
						throw new RuntimeException("Invalid scene id");
				}
			} else if(click.isTargetting(this, "run")) {
				
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ClickEvent.class);
	}
}