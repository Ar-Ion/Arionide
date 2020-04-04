package ch.innovazion.arionide.menu.params.edit;

import ch.innovazion.arionide.events.GeometryInvalidateEvent;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.menu.params.assign.InformationAssigner;
import ch.innovazion.automaton.Inherit;

public class VariableEditor extends InformationAssigner {
	
	@Inherit
	protected Parameter parameter; // If the parameter is mutable

	public VariableEditor(MenuManager manager) {
		super(manager);
	}

	protected void onExit() {
		super.onExit();

		if(parameter != null) {
			getSpecificationManager().refactorParameterDefault(parameter, new Variable(parameter.getName(), (Information) value));
			dispatch(new GeometryInvalidateEvent(1));
		}
	}
	
	protected String getDescriptionTitle() {
		return "Setting initial value of variable parameter '" + parameter.getName() + "'. " + super.getDescriptionTitle();
	}
}
