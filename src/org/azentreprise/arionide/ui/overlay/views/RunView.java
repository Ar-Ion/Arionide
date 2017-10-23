package org.azentreprise.arionide.ui.overlay.views;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.Label;
import org.azentreprise.arionide.ui.overlay.components.Tab;

public class RunView extends View implements EventHandler {

	private final Tab sourceSelector;
	private final Label[] console = new Label[15];
	
	private int sourceID;
	
	public RunView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		layoutManager.register(this, null, 0.0f, 0.0f, 1.0f, 1.0f);

		this.add(this.sourceSelector = new Tab(this, "<No source available>").setSignal("setSource"), 0.2f, 0.05f, 0.8f, 0.1f);
		
		this.add(new Button(this, "<").setSignal("back").setYCorrection(4), 0.05f, 0.05f, 0.15f, 0.1f);
		this.add(new Button(this, "Run").setSignal("run"), 0.85f, 0.05f, 0.95f, 0.1f);
		
		for(int i = 0; i < this.console.length; i++) {
			this.add(this.console[i] = new Label(this, "blablbalblablablbalbalablablab"), 0.0f, 0.15f + i * 0.05f, 0.5f, 0.2f + i * 0.05f);
		}
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
	}

	public void show(boolean transition) {		
		Storage storage = this.getAppManager().getWorkspace().getCurrentProject().getStorage();
		
		List<HierarchyElement> elements = storage.getHierarchy();
		Map<Integer, StructureMeta> metaData = storage.getStructureMeta();
		String[] buffer = new String[elements.size()];
		
		int i = 0;
		for(HierarchyElement element : elements) {
			buffer[i++] = metaData.get(element.getID()).getName();
		}
				
		this.sourceSelector.setComponents(buffer);
		
		super.show(transition);
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "back")) {
				this.openView(Views.code);
			} else if(click.isTargetting(this, "setSource")) {
				this.sourceID = (int) click.getData()[0];
			} else if(click.isTargetting(this, "run")) {
				org.azentreprise.arionide.lang.Runtime runtime = this.getAppManager().getWorkspace().getCurrentProject().getLanguage().getRuntime();
				runtime.setupOutput(this::debug);
				runtime.run(this.sourceID);
			}
		}
	}
	
	private void debug(String message, int color) {
		System.out.println(message);
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ClickEvent.class);
	}
}