package ch.innovazion.arionide.ui.core;

import ch.innovazion.arionide.debugging.IAm;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.core.gl.GLRenderer;
import ch.innovazion.arionide.ui.topology.Bounds;

public class CoreOrchestrator {
	
	private final IEventDispatcher dispatcher;
	
	private final CoreController controller;
	private final GLRenderer renderer;
		
	public CoreOrchestrator(IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		
		this.controller = new CoreController();
		this.renderer = new GLRenderer(controller);
		
		CoreEventHandler handler = new CoreEventHandler(controller);
		
		dispatcher.registerHandler(handler);
	}
	
	@IAm("orchestrating the initialisation")
	public void orchestrateInitialisation(AppDrawingContext context) {
		renderer.init(context);
		controller.reset();
	}
	
	@IAm("orchestrating the main rendering/updating pipeline")
	public void orchestrateMain(AppDrawingContext context) {
		
		UserController user = controller.getUserController();
		
		controller.updateStatic();
		
		if(controller.isReady()) {
			controller.updateDynamics();
		}
		
		renderer.updateCamera(controller.getGLPosition(), user.getYaw(), user.getPitch());
		
		if(controller.isActive()) {
			controller.updateUserDynamics();
		}
		
		dispatcher.fire(new MessageEvent(controller.getUserController().getUserDescription(), MessageType.DEBUG));

		renderer.render3D(context);
	}
	
	@IAm("orchestrating the overlay rendering/updating pipeline")
	public void orchestrateOverlay(AppDrawingContext context) {
		if(controller.isReady()) {
			renderer.render2D(context);
		}
	}
	
	@IAm("updating the viewport")
	public void updateBounds(Bounds bounds) {
		renderer.updateBounds(bounds);
		controller.updateBounds(bounds);
	}
	
	public CoreController getController() {
		return controller;
	}
	
	public GLRenderer getRenderer() {
		return renderer;
	}
}
