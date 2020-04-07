package ch.innovazion.arionide.menu.params.edit;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Variable;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.menu.params.ParameterUpdater;
import ch.innovazion.automaton.Inherit;

public class VariableEditor extends ParameterUpdater {
	
	@Inherit
	protected Parameter parameter;

	public VariableEditor(MenuManager manager) {
		super(manager, "Assign", "Initialise");
	}
	
	@Override
	public void onAction(String action) {
		switch(action) {
		case "Assign":
			go("assign");
			break;
		case "Initialise":
			this.value = ((Variable) value).getInitialValue();
			go("edit");
			break;
		}
	}
	
	protected String getDescriptionTitle() {
		if(parameter != null) {
			return "Setting up variable parameter '" + parameter.getName() + "'";
		} else {
			return "Setting up variable";
		}
	}
}
