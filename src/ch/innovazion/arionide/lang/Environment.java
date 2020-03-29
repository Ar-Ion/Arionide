package ch.innovazion.arionide.lang;

import java.util.ArrayList;
import java.util.List;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.Container;
import ch.innovazion.arionide.ui.overlay.components.Label;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;

public class Environment {

	private final List<Peripheral> peripherals = new ArrayList<>();
	
	protected void registerPeripheral(Peripheral peripheral) {
		peripherals.add(peripheral);
	}
	
	public void sample() {
		peripherals.forEach(Peripheral::sample);
	}
	
	public Container create(AppManager manager, Bounds renderBounds) {
		LayoutManager layoutManager = new LayoutManager(manager.getDrawingContext(), manager.getEventDispatcher());
		
		Container container = new Container(null, layoutManager);
		
		Point firstPoint = renderBounds.getFirstPoint();
		Point secondPoint = renderBounds.getSecondPoint();
		
		layoutManager.register(container, null, firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
		
		float height = 1.0f / peripherals.size();
		float x = 0.0f;
		
		for(Peripheral peripheral : peripherals) {
			Container display = new Container(container, layoutManager);

			container.add(new Label(container, peripheral.getUID()), x, 0.0f, x + 0.2f * height, 1.0f);
			container.add(display, x + 0.2f * height, 0.0f, x + height, 1.0f);
			
			x += height;
			
			peripheral.createDisplay(display);
		}
		
		layoutManager.compute();
		layoutManager.apply();
		
		return container;
	}
}
