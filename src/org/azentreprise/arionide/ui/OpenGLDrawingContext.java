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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;

import org.azentreprise.arionide.Arionide;
import org.azentreprise.arionide.IWorkspace;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.resources.Resources;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.primitives.IPrimitives;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class OpenGLDrawingContext implements AppDrawingContext, GLEventListener {
	
	private final GLProfile profile = GLProfile.get(GLProfile.GL4);
	private final GLCapabilities caps = new GLCapabilities(this.profile);
	private final GLWindow window = GLWindow.create(this.caps);
	
	private final FPSAnimator animator = new FPSAnimator(this.window, 60, true);
	
	public OpenGLDrawingContext(IEventDispatcher dispatcher, int width, int height) {
		this.window.addGLEventListener(this);
		this.window.setSize(width, height);
		this.window.setTitle("Arionide");
		
		this.window.addWindowListener(new WindowAdapter() {
            public void windowDestroyNotify(WindowEvent arg0) {
				System.exit(0);
			}
		});
	}

	public void load(Arionide theInstance, IWorkspace workspace, Resources resources, CoreRenderer renderer,
			LayoutManager manager) {
		this.window.setVisible(true);
		this.animator.start();
	}

	public void draw(Graphics2D g2d) {
		
	}

	public Dimension getSize() {
		return new Dimension(0, 0);
	}

	public void setupRenderingProperties() {
		
	}

	public void setCursor(Cursor cursor) {
		
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPrimitives getPrimitives() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontAdapter getFontAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDrawingColor(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pushOpacity(float newOpacity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void popOpacity() {
		// TODO Auto-generated method stub
		
	}
}