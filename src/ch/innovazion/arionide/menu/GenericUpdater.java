package ch.innovazion.arionide.menu;

import java.util.function.Consumer;

import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.ParameterValue;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.Export;

public class GenericUpdater extends Menu {
	
	protected GenericUpdater(MenuManager manager) {
		super(manager, "You should not see this");
	}

	@Export
	protected Structure target;
	
	@Export
	protected Parameter parameter;
	
	@Export
	protected ParameterValue value;
	
	@Export
	protected Consumer<Void> onUpdate;
	
	@Export
	protected boolean frozen;
	
	public void setGenericTarget(Structure target) {
		this.target = target;
	}
	
	public void setGenericParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public void setGenericParameterValue(ParameterValue value) {
		this.value = value;
	}
	
	public void setGenericParameterFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public void onAction(String action) {
		go(action);
	}
}
