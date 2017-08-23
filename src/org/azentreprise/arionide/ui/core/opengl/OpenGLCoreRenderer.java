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
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.WriteEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.primitives.OpenGLPrimitives;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.sampling.UniformSampling;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;

public class OpenGLCoreRenderer implements CoreRenderer, EventHandler {
	
	private static final float PI = 3.141593f;
	private static final float halfPI = PI / 2.0f;
	
	private static final int stars = 512;
	
	private static final int sphereLongitudes = 64;
	private static final int sphereLatitudes = 64;

	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_BACK_QUOTE;
	
	private static final float acceleration = 0.05f;
	private static final float ambientEnergyConservation = 0.9f;

	private static final float fov = (float) Math.toRadians(45.0f);
	private static final float zNear = 0.1f;
	private static final float zFar = 256.0f;
	
	private static final Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);

	private final AppDrawingContext context;
	private final IEventDispatcher dispatcher;
	private final Robot robot;
	
	private int shader;
	
	private int skyVAO;	
	private int sphereVAO;
	private int sphereEBO;
	
	/* Uniforms */
	private int model;
	private int view;
	private int projection;
	private int color;
	private int camera;
	private int lightColor;
	private int lightPosition;
	private int ambient;
			
	private Project project;
	private Matrix4f matrix = new Matrix4f();
	
	private Rectangle bounds;
	private boolean isInWorld = false;
	private long firstCtrl = 0L;
	
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private Vector3f player = new Vector3f();
	
	private final Vector3f spawn = new Vector3f(0.0f, 0.0f, -5.0f);
	private final Vector3f sun = new Vector3f(0.0f, 16.0f, 0.0f);
		
	private float straightVelocity = 0.0f;
	private float lateralVelocity = 0.0f;
	private float verticalVelocity = 0.0f;
	
	private FloatBuffer modelData = FloatBuffer.allocate(16);
	private FloatBuffer viewData = FloatBuffer.allocate(16);
	private FloatBuffer projectionData = FloatBuffer.allocate(16);

	public OpenGLCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher) {
		this.context = context;
		this.dispatcher = dispatcher;
		
		dispatcher.registerHandler(this);
		
		Robot robot = null;
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			; // What can we do...
		} finally {
			this.robot = robot;			
		}
	}
	
	public void init(GL4 gl) {
		try {
			/* Shader initialization */
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
			this.color = gl.glGetUniformLocation(this.shader, "color");
			this.camera = gl.glGetUniformLocation(this.shader, "camera");
			this.lightColor = gl.glGetUniformLocation(this.shader, "lightColor");
			this.lightPosition = gl.glGetUniformLocation(this.shader, "lightPosition");
			this.ambient = gl.glGetUniformLocation(this.shader, "ambient");

			/* Allocate buffers */
			IntBuffer vaos = IntBuffer.allocate(2);
			IntBuffer buffers = IntBuffer.allocate(3);
			gl.glGenVertexArrays(2, vaos);
			gl.glGenBuffers(3, buffers);
			
			this.skyVAO = vaos.get(0);
			this.sphereVAO = vaos.get(1);
			this.sphereEBO = buffers.get(0);
			int skyVBO = buffers.get(1);
			int sphereVBO = buffers.get(2);
			
			/* Create the sky */
			DoubleBuffer sky = DoubleBuffer.allocate(6 * stars);
			
			Random rand = new Random();
			
			new UniformSampling.Sphere(rand.nextLong(), stars, (x, y, z) -> sky.put(x).put(y).put(z).put(1.0).put(1.0).put(1.0));
			
			gl.glBindVertexArray(this.skyVAO);
			
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, skyVBO);
			gl.glBufferData(GL4.GL_ARRAY_BUFFER, sky.capacity() * Double.BYTES, sky.flip(), GL4.GL_STATIC_DRAW);
			this.enablePosition(gl);
			
			/* Create the default sphere. */
			DoubleBuffer sphere = DoubleBuffer.allocate(sphereLongitudes * (sphereLatitudes + 1) * 3);
			IntBuffer indices = IntBuffer.allocate(sphereLongitudes * sphereLatitudes * 4);

			for(int latitudeID = 0; latitudeID <= sphereLatitudes; latitudeID++) {
				float alpha = latitudeID * PI / sphereLatitudes;
				
				float sinAlpha = (float) Math.sin(alpha);
				float cosAlpha = (float) Math.cos(alpha);
				
				for(int longitudeID = 0; longitudeID < sphereLongitudes; longitudeID++) {
					float beta = longitudeID * 2 * PI / sphereLongitudes;

					sphere.put(sinAlpha * Math.cos(beta));
					sphere.put(cosAlpha);
					sphere.put(sinAlpha * Math.sin(beta));

					if(latitudeID < sphereLatitudes) {
						int latitudeElementID = latitudeID * sphereLongitudes;
						int nextLatitudeElementID = (latitudeID + 1) * sphereLongitudes;
						int nextLongitudeElementID = ((longitudeID + 1) % sphereLongitudes);

						indices.put(latitudeElementID + longitudeID);
						indices.put(latitudeElementID + nextLongitudeElementID);
						indices.put(nextLatitudeElementID + longitudeID);
						indices.put(nextLatitudeElementID + nextLongitudeElementID);
					}
				}
			}

			gl.glBindVertexArray(this.sphereVAO);
						
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, sphereVBO);
			gl.glBufferData(GL4.GL_ARRAY_BUFFER, sphere.capacity() * Double.BYTES, sphere.flip(), GL4.GL_STATIC_DRAW);
			this.enablePosition(gl);

			gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.sphereEBO);
			gl.glBufferData(GL4.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * Integer.BYTES, indices.flip(), GL4.GL_STATIC_DRAW);
			
			this.teleport(this.spawn);
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	private void enablePosition(GL4 gl) {
		int position = gl.glGetAttribLocation(this.shader, "position");
		gl.glEnableVertexAttribArray(position);
		gl.glVertexAttribPointer(position, 3, GL4.GL_DOUBLE, false, 3 * Double.BYTES, 0);
	}

	public void render(AppDrawingContext context) {
		assert context instanceof OpenGLDrawingContext;
		
		OpenGLDrawingContext glContext = (OpenGLDrawingContext) context;
		GL4 gl = glContext.getRenderer();
		
		gl.glUseProgram(this.shader);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		
		/* Setup vertex data */
		
		gl.glUniformMatrix4fv(this.view, 1, false, this.viewData);
		gl.glUniformMatrix4fv(this.projection, 1, false, this.projectionData);
		gl.glUniform3f(this.camera, this.player.x, this.player.y, this.player.z);
		
		/* Setup sky lighting */
		
		gl.glUniform1f(this.ambient, 1.0f);
		gl.glUniform3f(this.lightColor, 1.0f, 1.0f, 1.0f);
		gl.glUniform3f(this.color, 666.0f, 1.0f, 1.0f);
		
		/* Drawing the sky */
		
		gl.glBindVertexArray(this.skyVAO);
		
		this.loadMatrixData(this.matrix.identity().scale(zFar * 2.0f / 3.0f), this.modelData);
		gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
		gl.glDrawArrays(GL4.GL_POINTS, 0, stars);
		
		/* Setup sphere tesselation */

		gl.glBindVertexArray(this.sphereVAO);
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.sphereEBO);
		
		/* Drawing the sun */
		
		gl.glUniform3f(this.lightPosition, this.sun.x, this.sun.y, this.sun.z);
		gl.glUniform3f(this.color, 1.0f, 1.0f, 1.0f);
		this.loadMatrixData(this.matrix.identity().translate(this.sun).scale(0.2f), this.modelData);
		gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
		gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, sphereLongitudes * sphereLatitudes * 4, GL4.GL_UNSIGNED_INT, 0);

		/* Setup elements lighting */
		gl.glUniform1f(this.ambient, 0.5f);
		gl.glUniform3f(this.color, 0.0f, 0.5f, 0.5f);
		
		/* Drawing the elements */
		this.loadMatrixData(this.matrix.identity(), this.modelData);
		gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
		
		gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, sphereLongitudes * sphereLatitudes * 4, GL4.GL_UNSIGNED_INT, 0);

		gl.glUniform3f(this.color, 1.0f, 1.0f, 1.0f);
		
		gl.glDisable(GL4.GL_BLEND);
		gl.glDisable(GL4.GL_DEPTH_TEST);
		
		/* Updating the camera */
		
		this.player.x += Math.cos(halfPI + this.yaw) * this.straightVelocity + Math.cos(this.yaw) * this.lateralVelocity;
		this.player.y += this.verticalVelocity;
		this.player.z += Math.sin(halfPI + this.yaw) * this.straightVelocity + Math.sin(this.yaw) * this.lateralVelocity;
		
		this.checkPosition();
		
		this.updateCamera();
		
		this.straightVelocity *= ambientEnergyConservation;
		this.lateralVelocity *= ambientEnergyConservation;
		this.verticalVelocity *= ambientEnergyConservation;
	}
	
	private void checkPosition() {
		if(this.player.length() >= zFar / 3.0d) {
			this.teleport(this.spawn);
			this.dispatcher.fire(new MessageEvent("Careful! Getting lost in the universe is a bad idea...", MessageType.WARN));
		}
	}
	
	private void teleport(Vector3f dest) {
		this.player.set(dest);
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
				.rotate(-this.pitch, 1.0f, 0.0f, 0.0f)
				.rotate(this.yaw, 0.0f, 1.0f, 0.0f)
				.translate(this.player.x, -this.player.y, this.player.z)
		, this.viewData);
	}
	
	public void lookAt(Vector3f object) {
		this.loadMatrixData(this.matrix
				.identity()
				.lookAt(this.player, object, upVector)
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
			if(this.isInWorld) {
				Point2D position = ((MoveEvent) event).getPoint();

				this.yaw += (position.getX() - 1.0d) / 10.0f;
				this.pitch -= (position.getY() - 1.0d) / 10.0f;
				
				if(this.pitch > halfPI) {
					this.pitch = halfPI;
				} else if(this.pitch < -halfPI) {
					this.pitch = -halfPI;
				}
				
				this.yaw %= 4 * halfPI;
				
				if(this.robot != null) {
					this.robot.mouseMove(this.bounds.x + this.bounds.width / 2, this.bounds.y + this.bounds.height / 2);
				}
				
				event.abortDispatching();
			}
		} else if(event instanceof WriteEvent) {
			WriteEvent write = (WriteEvent) event;

			switch(write.getKeycode()) {
				case forward:
					this.straightVelocity += acceleration;
					break;
				case backward:
					this.straightVelocity -= acceleration;
					break;
				case left:
					this.lateralVelocity += acceleration;
					break;
				case right:
					this.lateralVelocity -= acceleration;
					break;
				case up:
					this.verticalVelocity += acceleration;
					break;
				case down:
					this.verticalVelocity -= acceleration;
					break;
			}
			
			if(write.getKeycode() == KeyEvent.VK_CONTROL) {
				long time = System.currentTimeMillis();
				
				if(time - this.firstCtrl < 500L) {
					this.isInWorld = !this.isInWorld;
					
					if(this.isInWorld) {
						this.context.setCursor(null);
					} else {
						this.context.setCursor(Cursor.getDefaultCursor());
					}
				}
				
				this.firstCtrl = time;
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(MoveEvent.class, WriteEvent.class);
	}
}