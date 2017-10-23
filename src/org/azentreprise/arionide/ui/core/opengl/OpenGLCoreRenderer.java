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
import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.azentreprise.arionide.coders.Coder;
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
import org.azentreprise.arionide.ui.menu.code.Code;
import org.azentreprise.arionide.ui.shaders.Shaders;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.sampling.UniformSampling;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;

public class OpenGLCoreRenderer implements CoreRenderer, EventHandler {	
	private static final int stars = 2048;
	
	private static final int structureRenderingQuality = 32;

	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	private static final int worldToggle = KeyEvent.VK_R;
	private static final int spawnKey = KeyEvent.VK_C;

	private static final float initialAcceleration = 0.005f;
	private static final float spaceFriction = 0.075f;

	private static final float fov = (float) Math.toRadians(60.0f);
	
	private static final float skyDistance = 32.0f;
			
	private final OpenGLDrawingContext context;
	private final IEventDispatcher dispatcher;
	private final WorldGeometry worldGeometry;
	private final CodeGeometry codeGeometry;
	private final Robot robot;
	
	private final List<ScaledRenderingInfo> structures = new ArrayList<>();
	private int skyVAO;	
	
	private float zNear = 1.0f;
	private float zFar = 64.0f;
	
	private int shader;
	
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
	
	private boolean needMenuUpdate = false;

	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private Vector3f player = new Vector3f();
	private Vector3f sun = new Vector3f(0.0f, skyDistance, 0.0f);
	
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
		this.worldGeometry = new WorldGeometry(dispatcher);
		this.codeGeometry = new CodeGeometry();
						
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
		this.initShaders(gl);
		
		int positionAttribute = gl.glGetAttribLocation(this.shader, "position");
		
		IntBuffer vertexArrays = IntBuffer.allocate(1 + 2 * structureRenderingQuality);
		IntBuffer buffers = IntBuffer.allocate(1 + 4 * structureRenderingQuality);
		
		gl.glGenVertexArrays(vertexArrays.capacity(), vertexArrays);
		gl.glGenBuffers(buffers.capacity(), buffers);
		
		/* Sky */
		this.beginUsingVertexArray(gl, this.skyVAO = vertexArrays.get(0));
		this.initBufferObject(gl, buffers.get(0), GL4.GL_ARRAY_BUFFER, stars, this::createSkyShapeData);
		this.endUsingVertexArray(gl, positionAttribute);
		
		/* Structures */
		for(int i = 0; i < structureRenderingQuality; i++) {
			int vertexArray = vertexArrays.get(i + 1);
			int buffer = buffers.get(i * 2 + 1);
			int layers = i + 8;
			
			this.structures.add(new ScaledRenderingInfo(vertexArray, buffer + 1, layers));
			
			this.beginUsingVertexArray(gl, vertexArray);
			this.initBufferObject(gl, buffer, GL4.GL_ARRAY_BUFFER, layers, this::createStructureShapeData);
			this.endUsingVertexArray(gl, positionAttribute);
			
			this.initBufferObject(gl, buffer + 1, GL4.GL_ELEMENT_ARRAY_BUFFER, layers, this::createStructureIndicesData);
		}
		
		
		/* Init player */
		this.ajustAcceleration();
	}
	
	private void initShaders(GL4 gl) {		
		try {
			int vert = Shaders.loadShader(gl, "core.vert", GL4.GL_VERTEX_SHADER);
			int frag = Shaders.loadShader(gl, "core.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.shader = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
		} catch(IOException exception) {
			Debug.exception(exception);
		}
		
		gl.glBindFragDataLocation(this.shader, 0, "outColor");
		
		gl.glLinkProgram(this.shader);

		this.loadUniforms(gl);
	}
	
	private void loadUniforms(GL4 gl) {
		this.model = gl.glGetUniformLocation(this.shader, "model");
		this.view = gl.glGetUniformLocation(this.shader, "view");
		this.projection = gl.glGetUniformLocation(this.shader, "projection");
		this.color = gl.glGetUniformLocation(this.shader, "color");
		this.camera = gl.glGetUniformLocation(this.shader, "camera");
		this.lightColor = gl.glGetUniformLocation(this.shader, "lightColor");
		this.lightPosition = gl.glGetUniformLocation(this.shader, "lightPosition");
		this.ambientFactor = gl.glGetUniformLocation(this.shader, "ambientFactor");
	}
	
	private void beginUsingVertexArray(GL4 gl, int arrayID) {
		gl.glBindVertexArray(arrayID);
	}
	
	private void endUsingVertexArray(GL4 gl, int positionAttribute) {
		gl.glEnableVertexAttribArray(positionAttribute);
		gl.glVertexAttribPointer(positionAttribute, 3, GL4.GL_DOUBLE, false, 3 * Double.BYTES, 0);
	}
	
	private void initBufferObject(GL4 gl, int bufferID, int bufferType, int param, Function<Integer, Buffer> shapeDataSupplier) {
		Buffer buffer = shapeDataSupplier.apply(param);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, bufferID);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, buffer.capacity() * Double.BYTES, buffer.flip(), GL4.GL_STATIC_DRAW);
	}
	
	private Buffer createSkyShapeData(int triangles) {
		DoubleBuffer sky = DoubleBuffer.allocate(6 * triangles);
		
		new UniformSampling.Sphere(System.currentTimeMillis(), triangles, (x, y, z) -> sky.put(x).put(y).put(z).put(1.0).put(1.0).put(1.0));
		
		return sky;
	}
	
	private Buffer createStructureShapeData(int layers) {
		DoubleBuffer sphere = DoubleBuffer.allocate(layers * layers * 3);
				
		for(double phi = 0.0d; phi < Math.PI; phi += Math.PI / (layers - 1.0d)) {
			for(double theta = 0.0d; theta < 2.0d * Math.PI; theta += 2.0d * Math.PI / (layers - 1.0d)) {
				sphere.put(Math.sin(phi) * Math.cos(theta));
				sphere.put(Math.cos(phi));
				sphere.put(Math.sin(phi) * Math.sin(theta));
			}
		}
				
		return sphere;
	}
	
	private Buffer createStructureIndicesData(int vertices) {
		IntBuffer indices = IntBuffer.allocate(vertices * vertices * 4);
		
		for(int latitudeID = 0; latitudeID < vertices; latitudeID++) {
			for(int longitudeID = 0; longitudeID < vertices; longitudeID++) {
				int latitudeElementID = latitudeID * vertices;
				int nextLatitudeElementID = (latitudeID + 1) * vertices;
				int nextLongitudeElementID = ((longitudeID + 1) % vertices);

				indices.put(latitudeElementID + longitudeID);
				indices.put(latitudeElementID + nextLongitudeElementID);
				indices.put(nextLatitudeElementID + longitudeID);
				indices.put(nextLatitudeElementID + nextLongitudeElementID);
			}
		}
				
		return indices;
	}
	
	
	public void render(AppDrawingContext context) {
		assert context instanceof OpenGLDrawingContext;
		
		OpenGLDrawingContext glContext = (OpenGLDrawingContext) context;
		GL4 gl = glContext.getRenderer();
		
		this.renderWorld(gl);
		this.update();
	}
	
	private void renderWorld(GL4 gl) {	    
		gl.glUseProgram(this.shader);
		
		/* Setup vertex data */
		Vector3f sunPosition = new Vector3f(this.player).add(this.sun);
		
		gl.glUniformMatrix4fv(this.view, 1, false, this.viewData);
		gl.glUniformMatrix4fv(this.projection, 1, false, this.projectionData);
		gl.glUniform3f(this.camera, this.player.x, this.player.y, this.player.z);
				
		gl.glUniform1f(this.ambientFactor, 1.0f);
		gl.glUniform3f(this.lightColor, 1.0f, 1.0f, 1.0f);
		gl.glUniform3f(this.lightPosition, sunPosition.x, sunPosition.y, sunPosition.z);
		
		this.renderStars(gl);

		if(this.project != null) {
			this.renderScene(gl);
		}
	}
	
	private void renderStars(GL4 gl) {
		gl.glBindVertexArray(this.skyVAO);

		this.setupModel(gl, this.matrix.identity().translate(this.player).scale(skyDistance));
		
		/* Sun */
		gl.glUniform4f(this.color, 1.0f, 666.0f, 1.0f, 1.0f);
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, stars);
		
		/* Stars */
		gl.glUniform4f(this.color, 666.0f, 1.0f, 1.0f, 1.0f);
		gl.glDrawArrays(GL4.GL_POINTS, 0, stars);
	}
	
	private void renderScene(GL4 gl) {
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		
		this.renderScene0(gl, this.worldGeometry.getElements());
		this.renderScene0(gl, this.codeGeometry.getElements());
	}
	
	private void renderScene0(GL4 gl, List<WorldElement> elements) {
		synchronized(elements) {
			for(WorldElement element : elements) {
				this.setupModel(gl, this.matrix.identity().translate(element.getCenter()).scale(element.getSize()));
	
				boolean blending = this.inside.contains(element);
				
				if(blending) {
					gl.glEnable(GL4.GL_BLEND);
				}
				
				if(element != this.selected && !blending) {
					gl.glUniform1f(this.ambientFactor, 0.3f);
				} else {
					gl.glUniform1f(this.ambientFactor, 0.6f);
				}
				
				gl.glEnable(GL4.GL_DEPTH_TEST);
				
				Vector4f color = element.getColor();
				Vector3f spot = element.getSpotColor();
				
				gl.glUniform4f(this.color, color.x, color.y, color.z, color.w);
				gl.glUniform3f(this.lightColor, spot.x, spot.y, spot.z);
				
				double viewHeight = 2.0d * Math.atan(fov / 2.0d) * this.player.distance(element.getCenter());
				int quality = (int) ((this.structures.size()) * Math.min(0.999d, 2 * element.getSize() / viewHeight));
				
				ScaledRenderingInfo info = this.structures.get(quality);
				
				gl.glBindVertexArray(info.getVAO());
				gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, info.getEBO());
				gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, 4 * info.getLayers() * (info.getLayers() - 1), GL4.GL_UNSIGNED_INT, 0);
				gl.glDisable(GL4.GL_DEPTH_TEST);
				
				if(blending) {
					gl.glDisable(GL4.GL_BLEND);
				}
			}
		}
	}
	
	private void setupModel(GL4 gl, Matrix4f matrix) {
		this.loadMatrixData(matrix, this.modelData);
		gl.glUniformMatrix4fv(this.model, 1, false, this.modelData);
	}
	
	
	private void update() {		
		this.updatePlayer();
		this.updateCamera();

		this.checkForCollisions();
		this.checkForTarget();
		
		this.updateMenu();
		
		this.dispatcher.fire(new MessageEvent(this.player + " | Looking at " + this.getElementName(this.lookingAt) + " (" + (this.lookingAt != null ? this.lookingAt.getID() : -1) + ")", MessageType.DEBUG));
	}
	
	private void updatePlayer() {
		this.velocity.x += (float) -(Math.cos(Math.PI / 2 + this.yaw) * this.acceleration.x + Math.cos(this.yaw) * this.acceleration.z);
		this.velocity.y += this.acceleration.y;
		this.velocity.z += (float) -(Math.sin(Math.PI / 2 + this.yaw) * this.acceleration.x + Math.sin(this.yaw) * this.acceleration.z);
		
		this.velocity.mul(1.0f - spaceFriction);
		
		this.player.add(this.velocity);
	}
	
	private void updateCamera() {
		this.loadMatrixData(this.matrix
				.identity()
				.rotate(-this.pitch, 1.0f, 0.0f, 0.0f)
				.rotate(this.yaw, 0.0f, 1.0f, 0.0f)
				.translate(-this.player.x, -this.player.y, -this.player.z)
		, this.viewData);
	}
	
	private void updatePerspective() {
		if(this.bounds != null) {
			this.loadMatrixData(this.matrix.identity().perspective(fov, (float) (this.bounds.getWidth() / this.bounds.getHeight()), this.zNear, this.zFar), this.projectionData);
		}
	}
		
	private void ajustAcceleration() {
		this.generalAcceleration = initialAcceleration * Math.max(0.000000001d, this.worldGeometry.getSizeForGeneration(this.inside.size()));
		this.zNear = (float) this.generalAcceleration * 10.0f;
		this.updatePerspective();
	}
	
	private void checkForCollisions() {
		List<WorldElement> worldElements = this.worldGeometry.getCollisions(this.player);
		worldElements.addAll(this.codeGeometry.getCollisions(this.player));
		
		synchronized(this.inside) {
			for(WorldElement element : worldElements) {
				if(!this.inside.remove(element)) {
					if(this.enterElement(element)) {
						this.buffer.add(element);
					}
				} else {
					this.buffer.add(element);
				}
			}

			this.inside.stream().filter(((Predicate<WorldElement>) this::exitElement).negate()).forEach(this.buffer::add);
			
			this.inside.clear();
			this.inside.addAll(this.buffer); // Swap buffers
			this.buffer.clear();
		}
	}

	private void checkForTarget() {
		Vector3f cameraDirection = new Vector3f(-this.viewData.get(2), -this.viewData.get(6), -this.viewData.get(10)); // DOF
		float size = this.worldGeometry.getSizeForGeneration(this.inside.size());
		
		WorldElement found = null;
		float distance = 10000.0f;

		List<WorldElement> menuData = new ArrayList<>();
		
		synchronized(this.worldGeometry.getElements()) {
			for(WorldElement element : this.worldGeometry.getElements()) {
				boolean insideConstraint = this.current == null || this.current.getCenter().distance(element.getCenter()) < this.worldGeometry.getSizeForGeneration(this.inside.size() - 1);
				if((element.getSize() == size && insideConstraint) || size < -42.0f) {
					float currentDistance = this.player.distance(element.getCenter());
	
					if(element.getSize() == size) {
						menuData.add(element);
					}
					
					if(currentDistance < distance) {
						if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.player))) {
							found = element;
							distance = currentDistance;
						}
					}
				}
			}
		}
		
		synchronized(this.codeGeometry.getElements()) {
			for(WorldElement element : this.codeGeometry.getElements()) {
				float currentDistance = this.player.distance(element.getCenter());
				if(currentDistance < distance) {
					if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.player))) {
						found = element;
						distance = currentDistance;
					}
				}
			}
		}
		
		MainMenus.getStructureList().set(menuData);

		this.lookingAt = found;
	}
	
	private boolean enterElement(WorldElement element) {
		if(element.isAccessAllowed()) {
			this.current = element;
			this.needMenuUpdate = true;
			return true;
		} else {
			this.repulseFrom(element.getCenter());
			return false;
		}
	}
	
	private boolean exitElement(WorldElement element) {
		this.current = this.buffer.stream().reduce((a, b) -> a.getSize() < b.getSize() ? a : b).orElse(null);
		this.needMenuUpdate = true;
		return true;
	}
	
	private void updateMenu() {
		if(this.needMenuUpdate) {
			MainMenus.getStructureList().setMenuCursor(0);
			this.dispatcher.fire(new MenuEvent(MainMenus.getStructureList()));
			this.ajustAcceleration();
			
			this.selected = null;
			this.needMenuUpdate = false;
			
			this.codeGeometry.buildGeometry(this.project, this.current);
		}
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
	
 	private void repulseFrom(Vector3f center) {
		Vector3f normal = new Vector3f(this.player).sub(center).normalize();
		
		if(new Vector3f(this.velocity).normalize().dot(normal) < 0.0f) {
			this.velocity.reflect(normal).normalize((float) this.generalAcceleration * 32.0f);
		}
 	}

	public void teleport(Vector3f dest) {
		this.updatePerspective();
		this.player.set(dest);
	}

	public void setScene(RenderingScene scene) {
		this.worldGeometry.loadScene(scene);
	}
	
	public void selectInstruction(int id) {
		List<WorldElement> elements = this.codeGeometry.getElements();
		
		synchronized(elements) {
			elements.stream().filter(e -> e.getID() == id).findAny().ifPresent(e -> this.selected = e);
		}
	}

	public void loadProject(Project project) {
		if(project == null) {
			this.isInWorld = false;
			this.context.setCursor(Cursor.getDefaultCursor());
			return;
		}
				
		this.project = project;
		this.needMenuUpdate = this.selected == null;
				
		long seed = project.getProperty("seed", Coder.integerDecoder);
		
		this.codeGeometry.setGenerationSeed(seed);
		this.worldGeometry.setGenerationSeed(seed);

		this.worldGeometry.buildGeometry(project);
		
		if(this.current != null && this.current.getID() >= 0) {
			this.codeGeometry.buildGeometry(this.project, this.current);
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

	public <T extends Event> void handleEvent(T event) {
		if(this.project == null) {
			return;
		}
		
		if(event instanceof MoveEvent) {
			if(this.isInWorld) {
				Point2D position = ((MoveEvent) event).getPoint();
				this.updateMouse(position);
				event.abortDispatching();
			}
		} else if(event instanceof PressureEvent) {
			PressureEvent pressure = (PressureEvent) event;

			if(this.isInWorld || pressure.getKeycode() == worldToggle) {
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
							
							this.acceleration.set(0.0f, 0.0f, 0.0f);
							
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
					case KeyEvent.VK_CONTROL:
						this.isControlDown = pressure.isDown();
						break;
				}
			}
		} else if(event instanceof WheelEvent) {
			if(this.isControlDown) {
				WheelEvent wheel = (WheelEvent) event;
				this.updateAcceleration(wheel.getDelta());
				wheel.abortDispatching();
			}
		} else if(event instanceof ActionEvent) {
			ActionEvent action = (ActionEvent) event;
			
			if(this.isInWorld && action.getType() == ActionType.CLICK) {
				if(action.isButton(ActionEvent.BUTTON_LEFT)) {
					this.onLeftClick();
				} else if(action.isButton(ActionEvent.BUTTON_RIGHT)) {
					this.onRightClick();
				}
				
				action.abortDispatching();
			}
		}
	}
	
	private void updateMouse(Point2D position) {
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
			this.robot.setAutoDelay(0);
			this.robot.setAutoWaitForIdle(false);
			this.robot.mouseMove(this.bounds.x + this.bounds.width / 2, this.bounds.y + this.bounds.height / 2);
		}
	}
	
	private void updateAcceleration(double wheelDelta) {
		this.generalAcceleration = Math.min(this.generalAcceleration * Math.pow(1.1d, 2 * wheelDelta), 1.0f);
		this.zNear = (float) this.generalAcceleration * 10.0f;
		this.updatePerspective();
	}
	
	private void onLeftClick() {
		this.dispatcher.fire(new ClickEvent(null, "menuScroll"));
	}
	
	private void onRightClick() {
		if(MainMenus.getStructureEditor().getColoring().isActive()) {
			/* Validate choice */
			this.dispatcher.fire(new ClickEvent(null, "menuScroll"));
		}

		if(this.selected != this.lookingAt) {
			this.selected = this.lookingAt; // Note that lookingAt can be null.
		} else {
			this.selected = null;
		}
		
		if(this.selected != null) {
			List<WorldElement> elements = this.codeGeometry.getElements();
			
			synchronized(elements) {
				if(elements.contains(this.selected)) {
					Code code = MainMenus.getCode();
					code.setCurrent(this.current);
					code.show();
					code.select(elements.indexOf(this.selected));
				} else {
					this.worldGeometry.select(this.selected);
					
					MainMenus.getStructureEditor().setCurrent(this.selected);
					
					this.dispatcher.fire(new MessageEvent(this.getElementName(this.selected) + " is selected", MessageType.INFO));
					this.dispatcher.fire(new MenuEvent(MainMenus.getStructureEditor()));
				}
			}
		} else {
			this.dispatcher.fire(new MenuEvent(MainMenus.getStructureList()));
		}
	}

	public List<Class<? extends Event>> getHandleableEvents() {
		return Arrays.asList(MoveEvent.class, PressureEvent.class, WheelEvent.class, ActionEvent.class);
	}
}