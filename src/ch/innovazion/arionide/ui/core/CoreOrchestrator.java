/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
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
		
	public CoreOrchestrator(IEventDispatcher dispatcher, CoreController controller, GLRenderer renderer) {
		this.dispatcher = dispatcher;
		this.controller = controller;
		this.renderer = renderer;
		
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
