package org.azentreprise.arionide.ui.overlay.components;

import java.util.List;

import org.azentreprise.arionide.ui.overlay.Component;
import org.azentreprise.arionide.ui.overlay.View;

public abstract class MultiComponent extends Component {
	
	private final List<Component> components;
	
	public MultiComponent(View parent, List<Component> components) {
		super(parent);
		
		assert components.size() > 0;
		
		this.components = components;
	}
	
	public List<Component> getComponents() {
		return this.components;
	}
}