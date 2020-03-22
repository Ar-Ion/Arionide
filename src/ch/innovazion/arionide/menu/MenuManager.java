package ch.innovazion.arionide.menu;

import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.StateManager;

public class MenuManager extends StateManager {
	
	private static final MenuHierarchy hierarchy = new MenuHierarchy();
	private final IEventDispatcher dispatcher;
		
	public MenuManager(IEventDispatcher dispatcher) {
		super(hierarchy, true);
		
		this.dispatcher = dispatcher;
	}
	
	public void setProject(Project project) {
		hierarchy.root.project = project;
	}
	
	public void selectStructure(Structure structure) {
		hierarchy.structureBrowser.target = structure;
		
		go("/");
		triggerAction(RootMenu.structureBrowser);
	}
	
	public void selectCode(Structure code) {
		hierarchy.codeBrowser.target = code;
		
		go("/");
		triggerAction(RootMenu.codeBrowser);
	}
	
	public void select(int cursor) {
		Menu menu = hierarchy.resolveCurrentState();
		
		menu.updateCursor(cursor);
				
		if(menu instanceof Browser) {
			Structure target = ((Browser) menu).target;			
			dispatcher.fire(new TargetUpdateEvent(target));
		}
	}
	
	public void click() {
		Menu menu = hierarchy.resolveCurrentState();
		menu.onAction(menu.selection);
	}
	
	public void back() {
		Menu menu = hierarchy.resolveCurrentState();
		
		if(menu != hierarchy.codeBrowser && menu != hierarchy.structureBrowser) {
			go("..");
		}
	}
	
	public void refresh(Menu current) {
		dispatcher.fire(new MenuEvent(current));
	}
	
	public void dispatchMessage(MessageEvent event) {
		dispatcher.fire(event);
	}
}
