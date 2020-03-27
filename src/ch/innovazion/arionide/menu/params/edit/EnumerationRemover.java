package ch.innovazion.arionide.menu.params.edit;

import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuDescription;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.managers.specification.EnumerationManager;
import ch.innovazion.automaton.Inherit;

public class EnumerationRemover extends Menu {
	
	@Inherit
	protected ParameterValue value;
	
	private EnumerationManager enumManager;
	
	public EnumerationRemover(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		super.onEnter();
		this.enumManager = project.getStructureManager().getSpecificationManager().loadEnumerationManager(value);
		setDynamicElements(enumManager.getNames().toArray(new String[0]));
		
		this.description = new MenuDescription("Select the enumeration possibilty you want to remove");
	}

	public void onAction(String action) {
		dispatch(enumManager.removePossibleEnum(action));
		go("..");
	}
}