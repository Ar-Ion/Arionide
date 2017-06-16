/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide.ui.overlay.views;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.azentreprise.arionide.IProject;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.InvalidateLayoutEvent;
import org.azentreprise.arionide.events.TimerEvent;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.animations.Animation;
import org.azentreprise.arionide.ui.animations.FieldModifierAnimation;
import org.azentreprise.arionide.ui.animations.ParametricSmoothingAlgorithm;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.Views;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.Label;

public class MainView extends View implements EventHandler {

	private static enum SwipeDirection {
		LEFT,
		RIGHT
	}
	
	private int page = 0;
	
	private Rectangle animationAnchor = null;
	private final Animation transformWidthAnimation;
	private float transformWidth = 1.0f; // mod 2
	
	private int componentsAlpha = Button.defaultAlpha;
	private final Animation componentsAlphaAnimation;
	
	public MainView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		this.transformWidthAnimation = new FieldModifierAnimation(this.getAppManager(), "transformWidth", MainView.class, this, new ParametricSmoothingAlgorithm(3.0f));
		this.componentsAlphaAnimation = new FieldModifierAnimation(this.getAppManager(), "componentsAlpha", MainView.class, this);
		
		layoutManager.register(this, null, 0.1f, 0.1f, 0.9f, 0.9f);
		
		this.setBorderColor(new Color(0xCAFE));
		
		this.add(new Label(this, "Home").alterFont(Font.BOLD), 0.0f, 0.05f, 1.0f, 0.2f);
		
		this.add(new Button(this, "Undefined"), 0.1f, 0.23f, 0.9f, 0.33f);
		this.add(new Button(this, "Undefined"), 0.1f, 0.38f, 0.9f, 0.48f);
		this.add(new Button(this, "Undefined"), 0.1f, 0.53f, 0.9f, 0.63f);
		this.add(new Button(this, "Undefined"), 0.1f, 0.68f, 0.9f, 0.78f);

		this.add(new Button(this, "New project").setSignal("new"), 0.1f, 0.83f, 0.33f, 0.92f);
		this.add(new Button(this, "Collaborate").setSignal("connect"), 0.38f, 0.83f, 0.61f, 0.92f);
		this.add(new Button(this, "Import").setSignal("import"), 0.66f, 0.83f, 0.90f, 0.92f);
		
		this.add(new Button(this, "<").setSignal("prev"), 0.2f / 7.0f, 0.43f, 0.5f / 7.0f, 0.58f);
		this.add(new Button(this, ">").setSignal("next"), 6.5f / 7.0f, 0.43f, 6.8f / 7.0f, 0.58f);
		
		this.getAppManager().getEventDispatcher().registerHandler(this);
		
		this.page = this.getMaxPage();
	}
	
	private void loadWorkspace() {
		IWorkspace theWorkspace = this.getAppManager().getWorkspace();
		
		theWorkspace.load();
		
		List<? super IProject> projects = theWorkspace.getProjectList();
		
		if(this.page > this.getMaxPage()) {
			this.page = this.getMaxPage();
		}
		
		// prev button
		if(this.page <= 0) {
			((Button) this.get(8)).hide();
		} else {
			((Button) this.get(8)).show();
		}
		
		// next button
		if(this.page > projects.size() / 4) {
			((Button) this.get(9)).hide();
		} else {
			((Button) this.get(9)).show();
		}
		
		if(this.page > 0) {
			for(int i = 0; i < 4; i++) {
				Button button = ((Button) this.get(i + 1));
				
				if(i < projects.size()) {
					IProject project = (IProject) projects.get(i);
					button.setSignal("open", project).setLabel("Open " + project.getName()).show();
				} else {
					button.hide();
				}
			}
		} else {
			((Button) this.get(1)).setSignal("browse", "https://azentreprise.org").setLabel("AZEntreprise.org").show();
			((Button) this.get(2)).setSignal("browse", "https://azentreprise.org/Arionide/bugreport.php").setLabel("Arionide bug report").show();
			((Button) this.get(3)).setSignal("browse", "https://azentreprise.org/Arionide/tutorials.php").setLabel("Arionide tutorials").show();
			((Button) this.get(4)).setSignal("browse", "https://azentreprise.org/Arionide").setLabel("Arionide community").show();
		}
	}
	
	private void setupFocus() {
		if(this.page > 0) {
			if(this.page == this.getMaxPage()) {
				this.getAppManager().getFocusManager().request(this.getAppManager().getWorkspace().getProjectList().size() % 4);
			} else {
				this.getAppManager().getFocusManager().request(1);
			}
		} else {
			this.getAppManager().getFocusManager().request(5);
		}
		
		this.getAppManager().getEventDispatcher().fire(new InvalidateLayoutEvent());
	}
	
	private int getMaxPage() {
		return (this.getAppManager().getWorkspace().getProjectList().size() + 3) / 4;
	}
	
	public void show() {
		super.show();
		
		this.getAppManager().getFocusManager().setupCycle(this.makeFocusCycle(View.NATURAL_FOCUS_CYCLE));
		this.setupFocus();
		
		this.loadWorkspace();
	}
	
	public void drawSurface(AppDrawingContext context) {
		if(this.animationAnchor != null) {
			for(int i = 1; i < 5; i++) {
				Rectangle buttonBounds = this.get(i).getBounds();
				
				if(buttonBounds != null) {
	
					((Button) this.get(i)).setOpacity(Math.abs(this.componentsAlpha));
	
					if(this.transformWidth < 0.0f) {
						int delta = (int) (this.animationAnchor.width * (this.transformWidth + 1.0f));
						
						buttonBounds.x = this.animationAnchor.x + delta;
						buttonBounds.width = this.animationAnchor.width - delta;
					} else {
						buttonBounds.x = this.animationAnchor.x;
						buttonBounds.width = (int) (this.transformWidth * this.animationAnchor.width);
					}
				}
			}
			
			for(int i = 8; i < 10; i++) {
				((Button) this.get(i)).setOpacity(Math.abs(this.componentsAlpha)); // prev
			}
		}
		
		super.drawSurface(context);
	}
	
	private void makeHorizontalSwipe(SwipeDirection direction, Consumer<Void> completionHandler) {

		if(this.animationAnchor != null) return;

		this.getAppManager().getFocusManager().request(-1);
		
		this.animationAnchor = (Rectangle) this.get(1).getBounds().clone(); // any of the buttons since they all have the same x-pos and width

		float sign = direction.equals(SwipeDirection.LEFT) ? 1.0f : -1.0f;
		
		this.transformWidth *= sign;
		
		this.transformWidthAnimation.startAnimation(500, after -> {
			this.animationAnchor = null;
			this.transformWidth = 1.0f;
		}, -sign);
		
		this.componentsAlphaAnimation.startAnimation(500, after -> {
			this.componentsAlpha = Button.defaultAlpha;
			completionHandler.accept(null);
		}, -Button.defaultAlpha);
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ClickEvent) {
			
			ClickEvent click = (ClickEvent) event;
			
			if(click.isTargetting(this, "open")) {
				Object[] data = click.getData();
				
				if(data.length > 0) {
					Object element = data[0];
					
					if(element instanceof IProject) {
						this.getAppManager().getWorkspace().loadProject((IProject) element);
						this.openView(Views.code);
					}
				}
			} else if(click.isTargetting(this, "browse")) {
				try {
					Desktop.getDesktop().browse(new URL((String) click.getData()[0]).toURI());
				} catch (Exception e) {
					System.err.println("Could not open link");
				}
			} else if(click.isTargetting(this, "new")) {
				this.openView(Views.newProject);
			} else if(click.isTargetting(this, "connect")) {
				// TODO
			} else if(click.isTargetting(this, "import")) {
				// TODO
			} else if(click.isTargetting(this, "prev")) {
				this.makeHorizontalSwipe(SwipeDirection.RIGHT, nil -> this.setupFocus());
				this.getAppManager().getSystemTimer().schedule(this, 250L);
				this.page--;
			} else if(click.isTargetting(this, "next")) {
				this.makeHorizontalSwipe(SwipeDirection.LEFT, nil -> this.setupFocus());
				this.getAppManager().getSystemTimer().schedule(this, 250L);
				this.page++;
			}
		} else if(event instanceof TimerEvent) {
			if(((TimerEvent) event).isTargetting(this)) {
				this.loadWorkspace();
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(ClickEvent.class, TimerEvent.class);
	}
}