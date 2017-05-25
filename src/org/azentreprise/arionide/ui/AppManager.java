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
package org.azentreprise.arionide.ui;

import java.awt.Graphics2D;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.primitives.Resources;

public class AppManager {
	
	private final AppDrawingContext drawingContext;
	
	private Arionide theInstance;
	private IWorkspace workspace;
	private Resources resources;
	private CoreRenderer renderer;
	private LayoutManager layoutManager;
	
	private View view;
	private View auxView;
	
	public AppManager(AppDrawingContext drawingContext) {
		this.drawingContext = drawingContext;
	}

	public void draw(Graphics2D g2d) {
		g2d.drawImage(this.resources.getBackground(), 0, 0, null);
		
		if(this.view != null) {
			this.view.draw(g2d);
		}
		
		if(this.auxView != null) {
			this.auxView.draw(g2d);
		}
	}
	
	public void loadUI(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer, LayoutManager manager) {
		this.theInstance = theInstance;
		this.workspace = workspace;
		this.resources = resources;
		this.renderer = renderer;
		this.layoutManager = manager;
		
		// this.showView(new MainView(), true);
	}
	
	public void showView(View view, boolean transition) {
		if(this.view != view && view != null) {
			if(this.view != null) {
				this.view.getAlphaAnimation().stopAnimation();
			}
			
			if(this.auxView != null) {
				this.auxView.getAlphaAnimation().stopAnimation();
			}
			
			this.layoutManager.unregister(this.view);
			
			if(transition) {
				if(this.view != null) {
					this.auxView = this.view;
					this.view = view;
					
					this.view.getAlphaAnimation().startAnimation(30, 1.0f);
					this.auxView.getAlphaAnimation().startAnimation(30, 0.0f);
				} else {
					this.view = view;
					this.view.getAlphaAnimation().startAnimation(30, 1.0f);
				}
			} else {
				this.view = view;
			}
		}
	}
	
	public AppDrawingContext getDrawingContext() {
		return this.drawingContext;
	}
	
	public void shutdown() {
		this.theInstance.shutdown();
	}
}