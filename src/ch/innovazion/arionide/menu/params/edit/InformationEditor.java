package ch.innovazion.arionide.menu.params.edit;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.menu.params.assign.InformationAssigner;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

// Warning: this class should extend both InformationAssigner and ParameterValueEditor. There is a design flaw...
public class InformationEditor extends InformationAssigner {

	@Export
	@Inherit
	protected Parameter parameter; // If the parameter is mutable
	
	public InformationEditor(MenuManager manager) {
		super(manager);
	}

	protected void onExit() {
		super.onExit();

		if(parameter != null) {
			dispatch(getSpecificationManager().refactorParameterDefault(parameter, value));
			dispatch(new GeometryInvalidateEvent(1));
		}
	}
}
