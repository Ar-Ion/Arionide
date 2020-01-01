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

import java.util.Set;

import com.jogamp.newt.event.KeyEvent;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MenuEvent;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.events.ProjectCloseEvent;
import ch.innovazion.arionide.events.ProjectEvent;
import ch.innovazion.arionide.events.ProjectOpenEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.ui.topology.Point;

public class CoreEventHandler implements EventHandler {
	
	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	private static final int worldToggle = KeyEvent.VK_R;
	private static final int spawnKey = KeyEvent.VK_C;
	
	private static final float mouseSensibility = 0.0001f;
	
	private final CoreController controller;
	
	private boolean isControlDown = false;
	
	private Menu currentMenu;
	
	public CoreEventHandler(CoreController controller) {
		this.controller = controller;
	}
	
	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ProjectEvent) {
			if(event instanceof ProjectOpenEvent) {
				controller.onProjectOpen(((ProjectEvent) event).getProject());
			} else if(event instanceof ProjectCloseEvent) {
				controller.onProjectClose();
			}
		}
		
		if(controller.isReady()) {
			if(event instanceof PressureEvent) {
				PressureEvent pressure = (PressureEvent) event;

				if(pressure.getKeycode() == worldToggle) {
					controller.toggleActivity();
				}
			}
			
			if(controller.isActive()) {
				if(event instanceof MoveEvent) {
					if(controller.isActive()) {
						Point position = ((MoveEvent) event).getPoint();
						
						controller.updateYaw((position.getX() - 1.0f) * mouseSensibility);
						controller.updatePitch((1.0f - position.getY()) * mouseSensibility);
						
						event.abortDispatching();
					}
				} else if(event instanceof PressureEvent) {
					PressureEvent pressure = (PressureEvent) event;
		
					if(controller.isActive() || pressure.getKeycode() == worldToggle) {
						switch(pressure.getKeycode()) {
							case forward:
								controller.moveX(pressure.isDown() ? 1 : 0);
								break;
							case backward:
								controller.moveX(pressure.isDown() ? -1 : 0);
								break;
							case left:
								controller.moveZ(pressure.isDown() ? 1 : 0);
								break;
							case right:
								controller.moveZ(pressure.isDown() ? -1 : 0);
								break;
							case up:
								controller.moveY(pressure.isDown() ? 1 : 0);
								break;
							case down:
								controller.moveY(pressure.isDown() ? -1 : 0);
								break;
							case spawnKey:
								if(isControlDown && pressure.isDown()) {
									controller.reset();
								}
								
								break;
							case KeyEvent.VK_CONTROL:
								this.isControlDown = pressure.isDown();
								break;
						}
					}
				} else if(event instanceof WheelEvent) {
					if(isControlDown) {
						WheelEvent wheel = (WheelEvent) event;
						controller.accelerate(Math.pow(1.01f, 2 * wheel.getDelta()));
						wheel.abortDispatching();
					}
				} else if(event instanceof ActionEvent) {
					ActionEvent action = (ActionEvent) event;
					
					if(action.getType() == ActionType.CLICK) {
						if(action.isButton(ActionEvent.BUTTON_LEFT)) {
							if(!controller.onLeftClick()) {
								currentMenu.click(); // Trigger menu event if the event was not captured by the controller
							}
						} else if(action.isButton(ActionEvent.BUTTON_RIGHT)) {
							controller.onRightClick();
						}
						
						action.abortDispatching();
					}
				} else if(event instanceof MenuEvent) {
					this.currentMenu = ((MenuEvent) event).getMenu();
				}
			}
		}
	}
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(MoveEvent.class, PressureEvent.class, WheelEvent.class, ActionEvent.class, ProjectOpenEvent.class, ProjectCloseEvent.class, MenuEvent.class);
	}
}
