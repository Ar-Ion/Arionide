package ch.innovazion.arionide.menu.structure;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.mutables.MutableStructure;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.automaton.Inherit;

public class TintSelector extends Menu {
	
	@Inherit
	protected MutableStructure target;

	public TintSelector(MenuManager manager) {
		super(manager, ApplicationTints.getColorNames().toArray(new String[0]));
	}
	
	protected void onEnter() {
		super.onEnter();
		
		cursor = target.getColorID();
		id = target.getColorID();
	}

	public void onAction(String action) {
		dispatch(project.getStructureManager().setColor(target.getIdentifier(), id));
		dispatch(new GeometryInvalidateEvent(2));
		go("..");
	}
}
