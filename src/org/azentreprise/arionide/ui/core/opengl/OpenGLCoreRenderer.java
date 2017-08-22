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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.core.opengl;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.primitives.OpenGLPrimitives;
import org.azentreprise.arionide.ui.primitives.VAOManager;
import org.joml.Matrix4f;

import com.jogamp.nativewindow.util.Rectangle;
import com.jogamp.opengl.GL4;
import com.sun.glass.events.KeyEvent;

public class OpenGLCoreRenderer implements CoreRenderer, EventHandler {
	
	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	
	private static final float fov = (float) Math.toRadians(45.0f);
	private static final float zNear = 1.0f;
	private static final float zFar = 10.0f;
	
	private Robot robot;
	
	private int shader;
	
	/* Uniforms */
	private int model;
	private int view;
	private int projection;
		
	private VAOManager manager;
	
	private final double[] vertices = {
	        -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,

	        -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,

	        -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,

	         0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,

	        -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f,

	        -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	         0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f,  0.5f, 1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f, -0.5f, 1.0f, 1.0f, 1.0f,

	        -1.0f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f,
	         1.0f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f,
	         1.0f,  1.0f, -0.5f, 1.0f, 1.0f, 1.0f,
	         1.0f,  1.0f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -1.0f,  1.0f, -0.5f, 1.0f, 1.0f, 1.0f,
	        -1.0f, -1.0f, -0.5f, 1.0f, 1.0f, 1.0f
	    };
	
	private Project project;
	private Matrix4f matrix = new Matrix4f();
	
	private Rectangle bounds;
	
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private float x = 0.0f;
	private float y = 0.0f;
	private float z = -5.0f;
	
	private float straightVelocity = 0.0f;
	private float lateralVelocity = 0.0f;
	
	private FloatBuffer modelData = FloatBuffer.allocate(16);
	private FloatBuffer viewData = FloatBuffer.allocate(16);
	private FloatBuffer projectionData = FloatBuffer.allocate(16);

	public OpenGLCoreRenderer() {
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void init(GL4 gl, IEventDispatcher dispatcher) {
		try {
			int vert = OpenGLPrimitives.loadShader(gl, "core.vert", GL4.GL_VERTEX_SHADER);
			int frag = OpenGLPrimitives.loadShader(gl, "core.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.shader = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
			
			gl.glBindFragDataLocation(this.shader, 0, "outColor");
			
			gl.glLinkProgram(this.shader);
			
			this.model = gl.glGetUniformLocation(this.shader, "model");
			this.view = gl.glGetUniformLocation(this.shader, "view");
			this.projection = gl.glGetUniformLocation(this.shader, "projection");

			dispatcher.registerHandler(this);
			this.manager = new VAOManager(gl, this.shader);
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void render(AppDrawingContext context) {
		assert context instanceof OpenGLDrawingContext;
		
		OpenGLDrawingContext glContext = (OpenGLDrawingContext) context;
		GL4 gl = glContext.getRenderer();
		
		gl.glUseProgram(this.shader);
		
		gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
		gl.glUniformMatrix4fv(this.view, 1, false, this.viewData);
		gl.glUniformMatrix4fv(this.projection, 1, false, this.projectionData);
		
		this.manager.loadVAO(0, () -> DoubleBuffer.wrap(this.vertices), (name, id) -> {
			switch(name) {
				case "position":
					gl.glVertexAttribPointer(id, 3, GL4.GL_DOUBLE, false, 6 * Double.BYTES, 0);
					break;
				case "color":
					gl.glVertexAttribPointer(id, 3, GL4.GL_DOUBLE, false, 6 * Double.BYTES, 3 * Double.BYTES);
					break;
			}
		}, "position", "color");

		this.updateCamera();
		
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 36);
	}

	@Override
	public void setScene(RenderingScene scene) {
		// TODO Auto-generated method stub
		
	}

	public void loadProject(Project project) {
		this.project = project;
	}

	public void update(Rectangle bounds) {
		this.bounds = bounds;
		this.loadMatrixData(this.matrix.identity().perspective(fov, (float) (bounds.getWidth() / bounds.getHeight()), zNear, zFar), this.projectionData);
		this.loadMatrixData(this.matrix.identity(), this.modelData);
	}
	
	public void updateCamera() {
		this.loadMatrixData(this.matrix
				.identity()
				.rotate(90 + this.pitch, 1.0f, 0.0f, 0.0f)
				.rotate(this.yaw, 0.0f, 1.0f, 0.0f)
				.translate(this.x, this.y, this.z)
				
		, this.viewData);
	}
	
	public void lookAt(float x, float y, float z) {
		this.loadMatrixData(this.matrix
				.identity()
				.lookAt(this.x, this.y, this.z, x, y, z, 0.0f, 1.0f, 0.0f)
		, this.viewData);
	}
	
	private void loadMatrixData(Matrix4f matrix, FloatBuffer target) {
		target.put(0, matrix.m00());
		target.put(1, matrix.m01());
		target.put(2, matrix.m02());
		target.put(3, matrix.m03());
		target.put(4, matrix.m10());
		target.put(5, matrix.m11());
		target.put(6, matrix.m12());
		target.put(7, matrix.m13());
		target.put(8, matrix.m20());
		target.put(9, matrix.m21());
		target.put(10, matrix.m22());
		target.put(11, matrix.m23());
		target.put(12, matrix.m30());
		target.put(13, matrix.m31());
		target.put(14, matrix.m32());
		target.put(15, matrix.m33());
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof MoveEvent) {
			Point2D position = ((MoveEvent) event).getPoint();

			this.yaw += (position.getX() - 1.0d) / 10.0f;
			this.pitch += (position.getY() - 1.0d) / 10.0f;
									
			this.robot.mouseMove(this.bounds.getX() + this.bounds.getWidth() / 2, this.bounds.getY() + this.bounds.getHeight() / 2);
		} else if(event instanceof WriteEvent) {
			WriteEvent write = (WriteEvent) event;

			switch(write.getKeycode()) {
			case forward:
				this.straightVelocity += 0.1f;
				break;
			case backward:
				this.straightVelocity += 0.1f;
				break;
			case left:
				this.lateralVelocity += 0.1f;
				break;
			case right:
				this.lateralVelocity += 0.1f;
				break;
			case up:
				this.y += 0.01f;
				break;
			case down:
				this.y -= 0.01f;
				break;
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(MoveEvent.class, WriteEvent.class);
	}

	@Override
	public void update(java.awt.Rectangle bounds) {
		// TODO Auto-generated method stub
		
	}
}