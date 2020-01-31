package ch.innovazion.arionide.ui.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.layout.LayoutManager;

public class Container extends Component {		

	private final LayoutManager layoutManager;
	private final List<Component> components = new ArrayList<>();
	
	public Container(Container parent, LayoutManager layoutManager) {
		super(parent);
		this.layoutManager = layoutManager;
	}
	
	public void load() {
		for(Component component : components) {
			component.load();
		}
	}
	
	public void add(Component component, float x1, float y1, float x2, float y2) {
		components.add(component);
		layoutManager.register(component, this, x1, y1, x2, y2);
		getAppManager().getFocusManager().registerComponent(component);
	}
	
	public void drawSurface(AppDrawingContext context) {
		drawComponents(context);		
	}
	
	protected void drawComponents(AppDrawingContext context) {
		for(Component component : components) {
			component.draw(context);
		}
	}
	
	public void update() {
		for(Component component : components) {
			component.update();
		}
	}
	
	protected List<Component> getComponents() {
		return Collections.unmodifiableList(components);
	}

	public boolean isFocusable() {
		return false;
	}
}
