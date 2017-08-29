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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.events.ActionEvent;
import org.azentreprise.arionide.events.ActionType;
import org.azentreprise.arionide.events.ClickEvent;
import org.azentreprise.arionide.events.Event;
import org.azentreprise.arionide.events.EventHandler;
import org.azentreprise.arionide.events.MenuEvent;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.events.MoveEvent;
import org.azentreprise.arionide.events.PressureEvent;
import org.azentreprise.arionide.events.WheelEvent;
import org.azentreprise.arionide.events.dispatching.IEventDispatcher;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.shaders.Shaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.sampling.UniformSampling;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;

public class OpenGLCoreRenderer implements CoreRenderer, EventHandler {	
	private static final int stars = 4096;
	
	private static final int sphereLongitudes = 64;
	private static final int sphereLatitudes = 64;

	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	private static final int worldToggle = KeyEvent.VK_R;
	private static final int spawnKey = KeyEvent.VK_C;
	private static final int fullscreen = KeyEvent.VK_F;

	private static final float initialAcceleration = 0.01f;
	private static final float ambientEnergyConservation = 0.9f;

	private static final float fov = (float) Math.toRadians(90.0f);
	
	private static final float skyDistance = 32.0f;
			
	private final OpenGLDrawingContext context;
	private final IEventDispatcher dispatcher;
	private final WorldGeometry geometry;
	private final Robot robot;
	
	private float zNear = 1.0f;
	private float zFar = 64.0f;
	
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
	private int ambientFactor;
			
	private Project project;
	private Matrix4f matrix = new Matrix4f();
	
	private Rectangle bounds;
	private boolean isInWorld = false;
	private boolean isControlDown = false;
	
	private List<WorldElement> inside = Collections.synchronizedList(new ArrayList<>());
	private List<WorldElement> buffer = new ArrayList<>();
	private WorldElement current;
	private WorldElement lookingAt;
	private WorldElement selected;
	
	private boolean needUpdate = false;

	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private Vector3f player = new Vector3f();
	
	private final Vector3f spawn = new Vector3f(0.0f, 0.0f, 0.0f);
		
	private Vector3f velocity = new Vector3f();
	private Vector3f acceleration = new Vector3f();
	private double generalAcceleration = initialAcceleration;
	
	private FloatBuffer modelData = FloatBuffer.allocate(16);
	private FloatBuffer viewData = FloatBuffer.allocate(16);
	private FloatBuffer projectionData = FloatBuffer.allocate(16);

	public OpenGLCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher) {
		this.context = (OpenGLDrawingContext) context;
		this.dispatcher = dispatcher;
		this.geometry = new WorldGeometry();
		
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
			int vert = Shaders.loadShader(gl, "core.vert", GL4.GL_VERTEX_SHADER);
			int frag = Shaders.loadShader(gl, "core.frag", GL4.GL_FRAGMENT_SHADER);
			
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
			this.ambientFactor = gl.glGetUniformLocation(this.shader, "ambientFactor");

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
			
			/* Create the default sphere */
			DoubleBuffer sphere = DoubleBuffer.allocate(sphereLongitudes * (sphereLatitudes  + 1) * 3);
			IntBuffer indices = IntBuffer.allocate(sphereLongitudes * sphereLatitudes * 4);

			for(int latitudeID = 0; latitudeID <= sphereLatitudes; latitudeID++) {
				float alpha = latitudeID * (float) Math.PI / sphereLatitudes;
				
				float sinAlpha = (float) Math.sin(alpha);
				float cosAlpha = (float) Math.cos(alpha);
				
				for(int longitudeID = 0; longitudeID < sphereLongitudes; longitudeID++) {
					float beta = longitudeID * 2 * (float) Math.PI / sphereLongitudes;

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
				
		/* Setup vertex data */
		Vector3f sunPosition = new Vector3f(this.player).add(0.0f, skyDistance, 0.0f);
		
		gl.glUniformMatrix4fv(this.view, 1, false, this.viewData);
		gl.glUniformMatrix4fv(this.projection, 1, false, this.projectionData);
		gl.glUniform3f(this.camera, this.player.x, this.player.y, this.player.z);
				
		gl.glUniform1f(this.ambientFactor, 1.0f);
		gl.glUniform3f(this.lightColor, 1.0f, 1.0f, 1.0f);
		gl.glUniform3f(this.lightPosition, sunPosition.x, sunPosition.y, sunPosition.z);
		
		this.drawStars(gl);

		/* Setup sphere tesselation */

		gl.glBindVertexArray(this.sphereVAO);
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.sphereEBO);
		
		
		if(this.project != null) {
			this.drawElements(gl);
		}
		
		/* Update */
		
		this.velocity.add(this.acceleration);
		
		this.updatePosition();
		
		Stream<WorldElement> elements = this.geometry.getCollisions(this.player);
		
		synchronized(this.inside) {
			elements.forEach((element) -> {
				if(!this.inside.remove(element)) {
					if(this.enterElement(element)) {
						this.buffer.add(element);
					}
				} else {
					this.buffer.add(element);
				}
			});
			
			this.inside.stream().filter(((Predicate<WorldElement>) this::exitElement).negate()).forEach(this.buffer::add);
			
			this.inside.clear();
			this.inside.addAll(this.buffer); // Swap buffers
			this.buffer.clear();
		}
				
		this.updateCamera();
		this.checkForTargetAndUpdateMenu();
		
		this.dispatcher.fire(new MessageEvent(this.player + " | Looking at " + this.getElementName(this.lookingAt) + " (" + (this.lookingAt != null ? this.lookingAt.getID() : -1) + ")", MessageType.DEBUG));
		
		this.velocity.mul(ambientEnergyConservation, this.velocity);
	}
	
	private void updatePosition() {
		this.player.x -= Math.cos(Math.PI / 2 + this.yaw) * this.velocity.x + Math.cos(this.yaw) * this.velocity.z;
		this.player.y += this.velocity.y;
		this.player.z -= Math.sin(Math.PI / 2 + this.yaw) * this.velocity.x + Math.sin(this.yaw) * this.velocity.z;	
	}
	
	private String getElementName(WorldElement element) {
		if(element != null) {
			if(element.getName().isEmpty()) {
				return "a mysterious structure";
			} else {
				return "'" + element.getName() + "'";
			}
		} else {
			return "the space";
		}
	}

	private boolean enterElement(WorldElement element) {		
		this.current = element;
		this.needUpdate = true;
		return true;
	}
	
	private boolean exitElement(WorldElement element) {
		this.current = this.buffer.stream().reduce((a, b) -> a.getSize() < b.getSize() ? a : b).orElse(null);
		this.needUpdate = true;
		return true;
	}
	
	private void updateInfo() {
		this.selected = null;
		this.dispatcher.fire(new MessageEvent("You are in " + this.getElementName(this.current), MessageType.INFO));
	}
	
	/*  
	 	private void repulseFrom(Vector3f center) {
			Vector3f normal = new Vector3f(this.player).sub(center).normalize();
			this.velocity.reflect(normal);
			this.updatePosition();
		}	
	*/
	
	private void checkForTargetAndUpdateMenu() {
		Vector3f cameraDirection = new Vector3f(-this.viewData.get(2), -this.viewData.get(6), -this.viewData.get(10)); // DOF
		float size = this.geometry.getSizeForGeneration(this.inside.size());
		
		WorldElement found = null;
		float distance = 10000.0f;

		List<WorldElement> menuData = new ArrayList<>();
		
		synchronized(this.geometry) {
			for(WorldElement element : this.geometry.getElements()) {
				if(element.getSize() == size && (this.current == null || element.getCenter().distance(this.current.getCenter()) < size * 20.0f)) {
					float currentDistance = this.player.distance(element.getCenter());
	
					menuData.add(element);
					
					if(currentDistance < distance) {
						if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.player))) {
							found = element;
							distance = currentDistance;
						}
					}
				}
			}
		}
		
		if(this.needUpdate) {
			MainMenus.STRUCT_LIST.set(menuData);
			this.dispatcher.fire(new MenuEvent(MainMenus.STRUCT_LIST));
			this.updateInfo();
			this.ajustAcceleration();
			this.needUpdate = false;
		}
				
		this.lookingAt = found;
	}
	
	private void drawStars(GL4 gl) {
		gl.glBindVertexArray(this.skyVAO);
		this.loadMatrixData(this.matrix.identity().translate(this.player).scale(skyDistance), this.modelData);
		gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
		
		gl.glUniform4f(this.color, 1.0f, 666.0f, 1.0f, 1.0f);
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, stars);
		
		gl.glUniform4f(this.color, 666.0f, 1.0f, 1.0f, 1.0f);
		gl.glDrawArrays(GL4.GL_POINTS, 0, stars);
	}
	
	private void drawElements(GL4 gl) {
		gl.glUniform1f(this.ambientFactor, 0.3f);
		
		synchronized(this.geometry) {
			for(WorldElement element : this.geometry.getElements()) {
				this.loadMatrixData(this.matrix.identity().translate(element.getCenter()).scale(element.getSize()), this.modelData);
				gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
	
				if(this.inside.contains(element)) {
					gl.glEnable(GL4.GL_BLEND);
					gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
				} else {
					gl.glDisable(GL4.GL_BLEND);
				}
				
				if(element != this.selected) {
					gl.glUniform1f(this.ambientFactor, 0.3f);
				} else {
					gl.glUniform1f(this.ambientFactor, 0.6f);
				}
				
				gl.glEnable(GL4.GL_DEPTH_TEST);
				
				Vector4f color = element.getColor();
				gl.glUniform4f(this.color, color.x, color.y, color.z, color.w);
				gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, sphereLongitudes * sphereLatitudes * 4, GL4.GL_UNSIGNED_INT, 0);
				
				gl.glDisable(GL4.GL_DEPTH_TEST);
			}
		}
		
		gl.glDisable(GL4.GL_BLEND); // Ensure it is disabled.
	}
	
	public void teleport(Vector3f dest) {
		this.updatePerspective();
		this.player.set(dest);
	}

	public void setScene(RenderingScene scene) {
		// TODO Auto-generated method stub
		
	}

	public void loadProject(Project project) {
		if(project == null) {
			this.isInWorld = false;
			this.context.setCursor(Cursor.getDefaultCursor());
		}
		
		this.project = project;
		this.needUpdate = true;
				
		synchronized(this.geometry) {
			this.geometry.buildGeometry(project);
		}
	}

	public void update(Rectangle bounds) {
		this.bounds = bounds;
		this.updatePerspective();
	}
	
	public List<Integer> getInside() {
		synchronized(this.inside) {
			return this.inside.stream().map(WorldElement::getID).collect(Collectors.toList());
		}
	}
	
	private void ajustAcceleration() {
		this.generalAcceleration = initialAcceleration * this.geometry.getSizeForGeneration(this.inside.size());;
		this.zNear = (float) this.generalAcceleration * 10.0f;
		this.updatePerspective();
	}
	
	private void updatePerspective() {
		if(this.bounds != null) {
			this.loadMatrixData(this.matrix.identity().perspective(fov, (float) (this.bounds.getWidth() / this.bounds.getHeight()), this.zNear, this.zFar), this.projectionData);
		}
	}
	
	private void updateCamera() {
		this.loadMatrixData(this.matrix
				.identity()
				.rotate(-this.pitch, 1.0f, 0.0f, 0.0f)
				.rotate(this.yaw, 0.0f, 1.0f, 0.0f)
				.translate(-this.player.x, -this.player.y, -this.player.z)
		, this.viewData);
	}
	
	/* Hack */
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
		if(this.project == null) {
			return;
		}
		
		if(event instanceof MoveEvent) {
			if(this.isInWorld) {
				Point2D position = ((MoveEvent) event).getPoint();

				this.yaw += (position.getX() - 1.0d) / 10.0f;
				this.pitch -= (position.getY() - 1.0d) / 10.0f;
				
				float halfPI = (float) Math.PI / 2.0f;
				
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
		} else if(event instanceof PressureEvent) {
			PressureEvent pressure = (PressureEvent) event;

			switch(pressure.getKeycode()) {
				case forward:
					this.acceleration.x = (pressure.isDown() ? (float) this.generalAcceleration : 0.0f);
					break;
				case backward:
					this.acceleration.x = (pressure.isDown() ? (float) -this.generalAcceleration : 0.0f);
					break;
				case left:
					this.acceleration.z = (pressure.isDown() ? (float) this.generalAcceleration : 0.0f);
					break;
				case right:
					this.acceleration.z = (pressure.isDown() ? (float) -this.generalAcceleration : 0.0f);
					break;
				case up:
					this.acceleration.y = (pressure.isDown() ? (float) this.generalAcceleration : 0.0f);
					break;
				case down:
					this.acceleration.y = (pressure.isDown() ? (float) -this.generalAcceleration : 0.0f);
					break;
				case worldToggle:
					if(pressure.isDown()) {
						this.isInWorld = !this.isInWorld;
						
						if(this.isInWorld) {
							this.context.setCursor(null);
						} else {
							this.context.setCursor(Cursor.getDefaultCursor());
						}
					}

					break;
				case spawnKey:
					if(this.isControlDown && pressure.isDown()) {
						this.teleport(this.spawn);
					}
					
					break;
				case fullscreen:
					if(pressure.isDown()) {
						this.context.toggleFullscreen();
					}
					
					break;
				case KeyEvent.VK_CONTROL:
					this.isControlDown = pressure.isDown();
					break;
			}
		} else if(event instanceof WheelEvent) {
			if(this.isControlDown) {
				WheelEvent wheel = (WheelEvent) event;
				
				this.generalAcceleration *= Math.pow(1.1d, 2 * wheel.getDelta());
				this.generalAcceleration = Math.min(this.generalAcceleration, 1.0f);

				this.zNear = (float) this.generalAcceleration * 10.0f;
				
				this.updatePerspective();
				
				wheel.abortDispatching();
			}
		} else if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;
			
			if(this.isInWorld && action.getType() == ActionType.CLICK) {
				if(action.isButton(ActionEvent.BUTTON_RIGHT)) {
					if(this.selected != this.lookingAt) {
						this.selected = this.lookingAt;
					} else {
						this.selected = null;
					}
					
					if(this.selected != null) {
						this.dispatcher.fire(new MessageEvent(this.getElementName(this.selected) + " is selected", MessageType.INFO));
						
						MainMenus.STRUCT_EDIT.setCurrent(this.selected);
						
						this.dispatcher.fire(new MenuEvent(MainMenus.STRUCT_EDIT));
					} else {
						this.updateInfo();
						this.dispatcher.fire(new MenuEvent(MainMenus.STRUCT_LIST));
					}
				} else if(action.isButton(ActionEvent.BUTTON_LEFT)) {
					this.dispatcher.fire(new ClickEvent(null, "SCROLL"));
				}
				
				action.abortDispatching();
			}
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(MoveEvent.class, PressureEvent.class, WheelEvent.class, ActionEvent.class);
	}
}