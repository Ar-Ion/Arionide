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
import java.awt.geom.Rectangle2D;
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

import org.azentreprise.arionide.coders.CameraInfo;
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
import org.azentreprise.arionide.ui.primitives.GLCoordinates;
import org.azentreprise.arionide.ui.shaders.Shaders;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.sampling.UniformSampling;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

public class OpenGLCoreRenderer implements CoreRenderer, EventHandler {
	
	private static final int stars = 1024;
	
	private static final int structureRenderingQuality = 32;

	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	private static final int worldToggle = KeyEvent.VK_R;
	private static final int spawnKey = KeyEvent.VK_C;

	private static final double initialAcceleration = 0.005d;
	private static final double spaceFriction = 0.075d;

	private static final double fov = Math.toRadians(60.0d);
	
	private static final double skyDistance = 32.0d;
	
	private static final float timeResolution = 0.1f;
	
			
	private final OpenGLDrawingContext context;
	private final IEventDispatcher dispatcher;
	private final WorldGeometry worldGeometry;
	private final CodeGeometry codeGeometry;
	private final Robot robot;
	
	private final DoubleBuffer modelData = DoubleBuffer.allocate(16);
	private final DoubleBuffer viewData = DoubleBuffer.allocate(16);
	private final DoubleBuffer projectionData = DoubleBuffer.allocate(16);
	
	private final FloatBuffer clearColor = FloatBuffer.allocate(4);
	private final FloatBuffer clearDepth = FloatBuffer.allocate(1);
	
	private int fxFBO;
	private int fxColorBuffer;
	private int fxDepthBuffer;
	
	private final List<ScaledRenderingInfo> structures = new ArrayList<>();
	private int skyVAO;
	private int fxVAO;
	
	private double zNear = 1.0d;
	private double zFar = 64.0d;
	
	private int shader;
	private int fx;
	
	/* Uniforms */
	private int model;
	private int view;
	private int projection;
	private int color;
	private int camera;
	private int specularColor;
	private int lightPosition;
	private int ambientFactor;
	
	private int fxFBOTexture;
	private int fxLightPosition;
	private int fxExposure;

	private Project project;
	
	private Matrix4d modelMatrix = new Matrix4d();
	private Matrix4d viewMatrix = new Matrix4d();
	private Matrix4d projectionMatrix = new Matrix4d();

	private Rectangle bounds;
	private boolean isInWorld = false;
	private boolean isControlDown = false;
	
	private List<WorldElement> inside = Collections.synchronizedList(new ArrayList<>());
	private List<WorldElement> buffer = new ArrayList<>();
	private WorldElement current;
	private WorldElement lookingAt;
	private WorldElement selected;
	
	private RenderingScene scene = null;
	
	private boolean needMenuUpdate = false;

	private double yaw = 0.0d;
	private double pitch = 0.0d;
	private Vector3d player = new Vector3d();
	private Vector3d sun = new Vector3d(0.0d, 2 * skyDistance, 0.0d);
	
	private Vector3d velocity = new Vector3d();
	private Vector3d acceleration = new Vector3d();
	private double generalAcceleration = initialAcceleration;
	
	private long lastPositionUpdate = System.currentTimeMillis();
	
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
		
		this.initFXFramebuffer(gl);
		
		this.clearColor.put(0, 0.0f).put(1, 0.0f).put(2, 0.0f).put(3, 1.0f);
		this.clearDepth.put(0, 1.0f);
		
		int positionAttribute = gl.glGetAttribLocation(this.shader, "position");
		int fxPositionAttribute = gl.glGetAttribLocation(this.fx, "position");

		IntBuffer vertexArrays = IntBuffer.allocate(2 + 2 * structureRenderingQuality);
		IntBuffer buffers = IntBuffer.allocate(2 + 4 * structureRenderingQuality);
		
		gl.glGenVertexArrays(vertexArrays.capacity(), vertexArrays);
		gl.glGenBuffers(buffers.capacity(), buffers);

		/* Sky */
		this.beginUsingVertexArray(gl, this.skyVAO = vertexArrays.get(0));
		this.initBufferObject(gl, buffers.get(0), GL4.GL_ARRAY_BUFFER, stars, this::createSkyShapeData);
		this.endUsingVertexArray(gl, positionAttribute);

		/* FX */
		GLCoordinates coords = new GLCoordinates(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d));		
		this.fxVAO = vertexArrays.get(1);
		
		gl.glBindVertexArray(this.fxVAO);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(1));
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, 8 * Double.BYTES, coords.allocDataBuffer(8).putNorth().putSouth().getDataBuffer(), GL4.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(fxPositionAttribute);
		gl.glVertexAttribPointer(fxPositionAttribute, 2, GL4.GL_DOUBLE, false, 0, 0);
				
		/* Structures */
		for(int i = 0; i < structureRenderingQuality; i++) {
			int vertexArray = vertexArrays.get(i + 2);
			int buffer = buffers.get(i * 2 + 2);
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
			int fxVert = Shaders.loadShader(gl, "corefx.vert", GL4.GL_VERTEX_SHADER);
			int fxFrag = Shaders.loadShader(gl, "corefx.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.shader = gl.glCreateProgram();
			this.fx = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
						
			gl.glAttachShader(this.fx, fxVert);
			gl.glAttachShader(this.fx, fxFrag);
		} catch(IOException exception) {
			Debug.exception(exception);
		}
		
		gl.glBindFragDataLocation(this.shader, 0, "outColor");
		gl.glBindFragDataLocation(this.fx, 0, "outColor");

		gl.glLinkProgram(this.shader);
		gl.glLinkProgram(this.fx);

		this.loadUniforms(gl);
		this.loadFXUniforms(gl);
	}
	
	private void loadUniforms(GL4 gl) {
		this.model = gl.glGetUniformLocation(this.shader, "model");
		this.view = gl.glGetUniformLocation(this.shader, "view");
		this.projection = gl.glGetUniformLocation(this.shader, "projection");
		this.color = gl.glGetUniformLocation(this.shader, "color");
		this.camera = gl.glGetUniformLocation(this.shader, "camera");
		this.specularColor = gl.glGetUniformLocation(this.shader, "specularColor");
		this.lightPosition = gl.glGetUniformLocation(this.shader, "lightPosition");
		this.ambientFactor = gl.glGetUniformLocation(this.shader, "ambientFactor");
	}
	
	private void loadFXUniforms(GL4 gl) {
		this.fxFBOTexture = gl.glGetUniformLocation(this.fx, "texture");
		this.fxLightPosition = gl.glGetUniformLocation(this.fx, "lightPosition");
		this.fxExposure = gl.glGetUniformLocation(this.fx, "exposure");
	}
	
	private void initFXFramebuffer(GL4 gl) {
		this.fxColorBuffer = this.initColorBuffer(gl);
		this.fxDepthBuffer = this.initDepthBuffer(gl);
		
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenFramebuffers(1, buffer);
		this.fxFBO = buffer.get(0);
		
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, this.fxFBO);
		
		gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, GL4.GL_TEXTURE_2D, this.fxColorBuffer, 0);
		gl.glFramebufferRenderbuffer(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, GL4.GL_RENDERBUFFER, this.fxDepthBuffer);
		
		if(gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER) != GL4.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Failed to initialize the FX framebuffer");
		}
	
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	
	private int initColorBuffer(GL4 gl) {
		gl.glActiveTexture(GL4.GL_TEXTURE2);

		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, buffer);
		int colorBufferID = buffer.get(0);
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, colorBufferID);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);

		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 1024, 1024, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return colorBufferID;
	}
	
	private int initDepthBuffer(GL4 gl) {
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenRenderbuffers(1, buffer);
		int depthBufferID = buffer.get(0);
		
		gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, depthBufferID);
		gl.glRenderbufferStorage(GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT16, 512, 512);
		gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
		
		return depthBufferID;
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
		
		new UniformSampling.Sphere(System.currentTimeMillis(), triangles, (x, y, z) -> sky.put(x).put(y).put(z).put(1.0d).put(1.0d).put(1.0d));
		
		return sky;
	}
	
	private Buffer createStructureShapeData(int layers) {
		DoubleBuffer sphere = DoubleBuffer.allocate(2*layers * layers * 3);
				
		for(double theta = 0.0d; theta < Math.PI + 10E-4; theta += Math.PI / (layers - 1)) {
			for(double phi = 0.0d; phi < 2.0d * Math.PI - 10E-4; phi += Math.PI / layers) {
				sphere.put(Math.sin(theta) * Math.cos(phi));
				sphere.put(Math.cos(theta));
				sphere.put(Math.sin(theta) * Math.sin(phi));
			}
		}
		
		return sphere;
	}
	
	private Buffer createStructureIndicesData(int layers) {
		IntBuffer indices = IntBuffer.allocate(2*layers * layers * 2);
		
		for(int latitudeID = 0; latitudeID < layers; latitudeID++) {
			for(int longitudeID = 0; longitudeID < 2*layers; longitudeID++) {
				indices.put(latitudeID * 2*layers + longitudeID);
				indices.put((latitudeID + 1) * 2*layers + longitudeID);
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
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, this.fxFBO);
		
		gl.glClearBufferfv(GL4.GL_COLOR, 0, this.clearColor);
        gl.glClearBufferfv(GL4.GL_DEPTH, 0, this.clearDepth);
		
		gl.glUseProgram(this.shader);
		
		Vector3d sunPosition = new Vector3d(this.player).add(this.sun);
		
		gl.glUniformMatrix4dv(this.view, 1, false, this.viewData);
		gl.glUniformMatrix4dv(this.projection, 1, false, this.projectionData);
		gl.glUniform3d(this.camera, this.player.x, this.player.y, this.player.z);
				
		gl.glUniform1f(this.ambientFactor, 1.0f);
		gl.glUniform3f(this.specularColor, 1.0f, 1.0f, 1.0f);
		gl.glUniform3d(this.lightPosition, sunPosition.x, sunPosition.y, sunPosition.z);
		
		this.renderStars(gl);

		if(this.project != null) {
			this.renderScene(gl);
		}
		
		/* Post-processing FX */
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		gl.glClearBufferfv(GL4.GL_COLOR, 0, this.clearColor);
        gl.glClearBufferfv(GL4.GL_DEPTH, 0, this.clearDepth);
				
		gl.glUseProgram(this.fx);
		
		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, this.fxColorBuffer);
		
		/* Load sun position in screen coords */
		
		Vector4d point = new Matrix4d(this.projectionMatrix).mul(this.viewMatrix).transform(new Vector4d(new Vector3d(this.sun).add(this.player.x, 0, this.player.z), 1.0d));
				
		/* Setup FX uniforms */
		gl.glUniform1i(this.fxFBOTexture, 2);
		gl.glUniform2f(this.fxLightPosition, (float) (point.x / point.z) + 0.5f, (float) (point.y / point.z) + 0.5f);
		gl.glUniform1f(this.fxExposure, 0.001f / (4 * this.inside.size() + 1));
		
		gl.glBindVertexArray(this.fxVAO);
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	private void renderStars(GL4 gl) {
		gl.glBindVertexArray(this.skyVAO);

		this.setupModel(gl, this.modelMatrix.identity().translate(this.player).scale(skyDistance));
		
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
		
		if(this.scene != RenderingScene.INHERITANCE) {
			this.renderScene0(gl, this.codeGeometry.getElements());
		}
	}
	
	private void renderScene0(GL4 gl, List<WorldElement> elements) {
		synchronized(elements) {
			for(WorldElement element : elements) {
				this.setupModel(gl, this.modelMatrix.identity().translate(element.getCenter()).scale(element.getSize()));
	
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
				gl.glUniform3f(this.specularColor, spot.x, spot.y, spot.z);
				
				double viewHeight = 2.0d * Math.atan(fov / 2.0d) * this.player.distance(element.getCenter());
				int quality = (int) (this.structures.size() * Math.min(1.0d - 10E-3d, 2 * element.getSize() / viewHeight));
				
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
	
	private void setupModel(GL4 gl, Matrix4d matrix4d) {
		this.loadMatrixData(matrix4d, this.modelData);
		gl.glUniformMatrix4dv(this.model, 1, false, this.modelData);
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
		this.velocity.x += -Math.cos(Math.PI / 2 + this.yaw) * this.acceleration.x - Math.cos(this.yaw) * this.acceleration.z;
		this.velocity.y += this.acceleration.y;
		this.velocity.z += -Math.sin(Math.PI / 2 + this.yaw) * this.acceleration.x - Math.sin(this.yaw) * this.acceleration.z;
		
		this.velocity.mul(1.0f - spaceFriction);
		
		long deltaTime = System.currentTimeMillis() - this.lastPositionUpdate;
		this.lastPositionUpdate += deltaTime;
		
		this.player.add(new Vector3d(this.velocity).mul(deltaTime * timeResolution));
	}
	
	private void updateCamera() {
		this.loadMatrixData(this.viewMatrix
				.identity()
				.rotate((float) -this.pitch, 1.0f, 0.0f, 0.0f)
				.rotate((float) this.yaw, 0.0f, 1.0f, 0.0f)
				.translate(-this.player.x, -this.player.y, -this.player.z), this.viewData);
		
		if(this.project != null && this.scene == RenderingScene.HIERARCHY) {
			CameraInfo info = new CameraInfo(this.player.x, this.player.y, this.player.z, (float) this.yaw, (float) this.pitch);
			this.project.setProperty("player", info, Coder.cameraEncoder);
		}
	}
	
	private void updatePerspective() {
		if(this.bounds != null) {
			this.loadMatrixData(this.projectionMatrix.identity().perspective(fov, this.bounds.getWidth() / this.bounds.getHeight(), this.zNear, this.zFar), this.projectionData);
		}
	}
		
	private void ajustAcceleration() {
		if(this.scene != RenderingScene.INHERITANCE) {
			this.generalAcceleration = initialAcceleration * Math.max(10E-10D, this.worldGeometry.getSizeForGeneration(this.inside.size()));
		} else {
			this.generalAcceleration = initialAcceleration;
		}
		
		this.zNear = this.generalAcceleration * 10.0f;
		
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
		Vector3d cameraDirection = new Vector3d(-this.viewData.get(2), -this.viewData.get(6), -this.viewData.get(10)); // DOF
		double size = this.worldGeometry.getSizeForGeneration(this.inside.size());
		
		WorldElement found = null;
		double distance = 10000.0d;

		List<WorldElement> menuData = new ArrayList<>();
		
		synchronized(this.worldGeometry.getElements()) {
			for(WorldElement element : this.worldGeometry.getElements()) {
				boolean insideConstraint = this.current == null || this.current.getCenter().distance(element.getCenter()) < this.worldGeometry.getSizeForGeneration(this.inside.size() - 1);
				if((element.getSize() == size && insideConstraint) || size < -42.0d) {
					double currentDistance = this.player.distance(element.getCenter());
	
					if(element.getSize() == size) {
						menuData.add(element);
					}
					
					if(currentDistance < distance) {
						if(element.collidesWith(new Vector3d(cameraDirection).normalize(currentDistance).add(this.player))) {
							found = element;
							distance = currentDistance;
						}
					}
				}
			}
		}
		
		synchronized(this.codeGeometry.getElements()) {
			for(WorldElement element : this.codeGeometry.getElements()) {
				double currentDistance = this.player.distance(element.getCenter());
				if(currentDistance < distance) {
					if(element.collidesWith(new Vector3d(cameraDirection).normalize(currentDistance).add(this.player))) {
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
	private void loadMatrixData(Matrix4d matrix, DoubleBuffer modelData) {
		modelData.put(0, matrix.m00());
		modelData.put(1, matrix.m01());
		modelData.put(2, matrix.m02());
		modelData.put(3, matrix.m03());
		modelData.put(4, matrix.m10());
		modelData.put(5, matrix.m11());
		modelData.put(6, matrix.m12());
		modelData.put(7, matrix.m13());
		modelData.put(8, matrix.m20());
		modelData.put(9, matrix.m21());
		modelData.put(10, matrix.m22());
		modelData.put(11, matrix.m23());
		modelData.put(12, matrix.m30());
		modelData.put(13, matrix.m31());
		modelData.put(14, matrix.m32());
		modelData.put(15, matrix.m33());
	}
	
 	private void repulseFrom(Vector3d object) {
		Vector3d normal = new Vector3d(this.player).sub(object).normalize();
		
		if(new Vector3d(this.velocity).normalize().dot(normal) < 0.0d) {
			this.velocity.reflect(normal).normalize(this.generalAcceleration * 32.0d);
		}
 	}

	public void teleport(Vector3d position) {
		this.updatePerspective();
		this.player.set(position);
		
		this.checkForCollisions();
		this.ajustAcceleration();
	}
	
	public void teleport(String identifier) {
		String[] elements = identifier.split(":");
		
		assert elements.length == 2;
				
		try {
			int teleport = Integer.parseInt(elements[0]);
			WorldElement teleportElement = null;
			List<WorldElement> world = this.worldGeometry.getElements();
			
			synchronized(world) {
				for(WorldElement obj : world) {
					if(obj.getID() == teleport) {
						teleportElement = obj;
						break;
					}
				}
			}
			
			this.teleport(teleportElement.getCenter().add(0.0d, teleportElement.getSize() * 0.8d, 0.0d));
			
			this.checkForCollisions();
			
			int lookAt = Integer.parseInt(elements[1]);
			WorldElement lookAtElement = null;
			this.codeGeometry.buildGeometry(this.project, teleportElement);
			List<WorldElement> code = this.codeGeometry.getElements();
			
			synchronized(world) {
				int i = 0;
				
				for(WorldElement obj : code) {
					if(obj.getID() == lookAt) {
						lookAtElement = obj;
						lookAt = i; // Changes lookAt variable !!!
						break;
					}
					
					i++;
				}
			}
			
			if(lookAtElement != null) {
				Vector3d lookAtVector = lookAtElement.getCenter().sub(this.player).normalize();
				
				this.yaw = Math.PI - Math.atan2(lookAtVector.x, lookAtVector.z);
				this.pitch = Math.asin(lookAtVector.y);
				
				this.selected = lookAtElement;
			}
			
			this.needMenuUpdate = false;
			
			Code menu = MainMenus.getCode();
			menu.setCurrent(teleportElement);
			menu.show();
			menu.select(lookAt);
		} catch(NumberFormatException e) {
			;
		}
	}

	public void setScene(RenderingScene scene) {
		this.scene = scene;
		this.worldGeometry.loadScene(scene);
		
		if(scene != RenderingScene.HIERARCHY) {
			this.teleport(new Vector3d(0.0d, 0.0d, 5.0d));
			
			this.yaw = 0.0d;
			this.pitch = 0.0d;
		} else {
			CameraInfo info = this.project.getProperty("player", Coder.cameraDecoder);
			
			this.teleport(new Vector3d(info.getX(), info.getY(), info.getZ()));
			
			this.yaw = info.getYaw();
			this.pitch = info.getPitch();
		}
		
		synchronized(this.inside) {
			this.inside.clear();
		}
		
		this.ajustAcceleration();
	}
	
	public void selectInstruction(int id) {
		List<WorldElement> elements = this.codeGeometry.getElements();
		
		synchronized(elements) {
			elements.stream().filter(e -> e.getID() == id).findAny().ifPresent(e -> this.selected = e);
		}
	}

	public void loadProject(Project project) {
		this.project = project;

		if(project == null) {
			this.isInWorld = false;
			this.context.setCursor(Cursor.getDefaultCursor());
			return;
		}
				
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
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, this.fxColorBuffer);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, bounds.width, bounds.height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, this.fxDepthBuffer);
		gl.glRenderbufferStorage(GL4.GL_RENDERBUFFER, GL4.GL_DEPTH_COMPONENT16, bounds.width, bounds.height);
		gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, 0);
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
						this.acceleration.x = (pressure.isDown() ? this.generalAcceleration : 0.0d);
						break;
					case backward:
						this.acceleration.x = (pressure.isDown() ? -this.generalAcceleration : 0.0d);
						break;
					case left:
						this.acceleration.z = (pressure.isDown() ? this.generalAcceleration : 0.0d);
						break;
					case right:
						this.acceleration.z = (pressure.isDown() ? -this.generalAcceleration : 0.0d);
						break;
					case up:
						this.acceleration.y = (pressure.isDown() ? this.generalAcceleration : 0.0d);
						break;
					case down:
						this.acceleration.y = (pressure.isDown() ? -this.generalAcceleration : 0.0d);
						break;
					case worldToggle:
						if(pressure.isDown()) {
							this.isInWorld = !this.isInWorld;
							
							this.acceleration.set(0.0d, 0.0d, 0.0d);
							
							if(this.isInWorld) {
								this.context.setCursor(null);
							} else {
								this.context.setCursor(Cursor.getDefaultCursor());
							}
						}
	
						break;
					case spawnKey:
						if(this.isControlDown && pressure.isDown()) {
							this.teleport(new Vector3d(0.0d, 0.0d, 0.0d));
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
		this.yaw += (position.getX() - 1.0d) * 0.1d;
		this.pitch -= (position.getY() - 1.0d) * 0.1d;
		
		double halfPI = Math.PI / 2.0d;
		
		if(this.pitch > halfPI) {
			this.pitch = halfPI;
		} else if(this.pitch < -halfPI) {
			this.pitch = -halfPI;
		}
		
		this.yaw %= 4.0d * halfPI;
		
		if(this.robot != null) {
			this.robot.setAutoDelay(0);
			this.robot.setAutoWaitForIdle(false);
			this.robot.mouseMove(this.bounds.x + this.bounds.width / 2, this.bounds.y + this.bounds.height / 2);
		}
	}
	
	private void updateAcceleration(double wheelDelta) {
		this.generalAcceleration = Math.min(this.generalAcceleration * Math.pow(1.1d, 2 * wheelDelta), 1.0d);
		this.zNear = (float) this.generalAcceleration * 10.0d;
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