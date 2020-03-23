package ch.innovazion.arionide.menu;

import java.util.List;
import java.util.stream.Collectors;

import ch.innovazion.arionide.events.TargetUpdateEvent;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.automaton.Export;

public abstract class Browser extends Menu {

	private List<Structure> browsables;
	
	@Export
	protected Structure target;
	
	public Browser(MenuManager manager) {
		super(manager);
	}
	
	protected void onEnter() {
		List<HierarchyElement> elements = fetchBrowsableIDs();
		browsables = elements.stream().map(HierarchyElement::getID).map(project.getStorage().getStructures()::get).collect(Collectors.toList());
		
		setDynamicElements(browsables.stream().map(Structure::getName).toArray(String[]::new));
				
		if(target != null) {
			int index = browsables.indexOf(target);
			
			if(index != -1) {
				this.cursor = index;
				this.id = index;
				this.selection = target.getName();
			}
		} else if(id < browsables.size()){
			updateCursor(0);
			dispatch(new TargetUpdateEvent(target));
		}
		
		super.onEnter();
	}
	
	protected void updateCursor(int cursor) {
		super.updateCursor(cursor);		
		
		if(browsables != null && id < browsables.size()) {
			this.target = browsables.get(id);
			this.selection = target.getName();

			this.description = new MenuDescription();
			
			if(target != null) {
				for(Parameter param : target.getSpecification().getParameters()) {
					description.add(param.toString());
				}
				
				description.spacer();
				
				for(String comment : target.getComment()) {
					description.add(comment);
				}
			}	
		}
	}

	public void onAction(String action) {
		if(target != null) {
			browse();
		}
	}

	protected abstract List<HierarchyElement> fetchBrowsableIDs();
	protected abstract void browse();
}
