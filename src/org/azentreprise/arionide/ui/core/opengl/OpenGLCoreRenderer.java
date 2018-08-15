/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
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
import java.awt.Robot;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.Reference;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLContext;
import org.azentreprise.arionide.ui.core.CoreRenderer;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.code.CodeEditor;
import org.azentreprise.arionide.ui.menu.code.ReferenceEditor;
import org.azentreprise.arionide.ui.menu.code.TypeEditor;
import org.azentreprise.arionide.ui.render.GLCoordinates;
import org.azentreprise.arionide.ui.shaders.Shaders;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

public class OpenGLCoreRenderer implements CoreRenderer, EventHandler {
	
	private static final int spaceSphereQuality = 3;
	
	private static final int structureRenderingQuality = 32;
	private static final boolean crystalMode = false;
	private static final int crystalQuality = 2;
	
	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	private static final int worldToggle = KeyEvent.VK_R;
	private static final int spawnKey = KeyEvent.VK_C;

	private static final double initialAcceleration = 0.005d;
	private static final double spaceFriction = 0.05d;

	private static final double fov = Math.toRadians(60.0d);
	
	private static final double skyDistance = 32.0d;
	
	private static final double timeResolution = 0.00000005d;
	
			
	private final OpenGLContext context;
	private final IEventDispatcher dispatcher;
	private final WorldGeometry worldGeometry;
	private final CodeGeometry codeGeometry;
	private final Robot robot;
	
	private final DoubleBuffer modelData = DoubleBuffer.allocate(16);
	private final DoubleBuffer viewData = DoubleBuffer.allocate(16);
	private final DoubleBuffer projectionData = DoubleBuffer.allocate(16);
	private final DoubleBuffer currentToPreviousViewportData = DoubleBuffer.allocate(16);

	private final FloatBuffer clearColor = FloatBuffer.allocate(4);
	private final FloatBuffer clearDepth = FloatBuffer.allocate(1);
	
	private final Matrix4d viewMatrix = new Matrix4d();
	private final Matrix4d projectionMatrix = new Matrix4d();
	private Matrix4d previousViewProjectionMatrix = new Matrix4d();
	
	private int fxFBO;
	private int fxColorTexture;
	private int fxDepthTexture;
	
	private final List<ScaledRenderingInfo> structures = new ArrayList<>();
	private int spaceVAO;
	private int spaceEBO;
	private int connectionVAO;
	private int connectionVBO;
	private int fxVAO;
	
	private double zNear = 1.0d;
	private double zFar = 100.0d;
	
	private int structuresShader;
	private int spaceShader;
	private int fxShader;
	
	/* Structures uniforms */
	private int structModel;
	private int structView;
	private int structProjection;
	private int color;
	private int camera;
	private int specularColor;
	private int structuresLightPosition;
	private int ambientFactor;
	
	/* Space uniforms */
	private int spaceModel;
	private int spaceView;
	private int spaceProjection;
	private int spaceLightPosition;
	private int windowDimensions;

	/* FX uniforms */
	private int colorTexture;
	private int depthTexture;
	private int currentToPreviousViewportMatrix;
	private int fxLightPosition;
	private int exposure;
	private int pixelSize;

	private Project project;

	private Bounds bounds;
	private boolean isInWorld = false;
	private boolean isControlDown = false;
	private boolean isFastBindingMode = false;
	
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
	
	private long lastPositionUpdate = System.nanoTime();
	
	public OpenGLCoreRenderer(AppDrawingContext context, IEventDispatcher dispatcher) {
		this.context = (OpenGLContext) context;
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
		
		int structuresPositionAttribute = gl.glGetAttribLocation(this.structuresShader, "position");
		int spacePositionAttribute = gl.glGetAttribLocation(this.spaceShader, "position");
		int fxPositionAttribute = gl.glGetAttribLocation(this.fxShader, "position");

		IntBuffer vertexArrays = IntBuffer.allocate(3 + structureRenderingQuality);
		IntBuffer buffers = IntBuffer.allocate(4 + 2 * structureRenderingQuality);
		
		gl.glGenVertexArrays(vertexArrays.capacity(), vertexArrays);
		gl.glGenBuffers(buffers.capacity(), buffers);

		/* Space */
		this.beginUsingVertexArray(gl, this.spaceVAO = vertexArrays.get(0));
		this.initBufferObject(gl, buffers.get(0), GL4.GL_ARRAY_BUFFER, spaceSphereQuality, this::createStructureShapeData);
		this.endUsingVertexArray(gl, spacePositionAttribute);

		this.initBufferObject(gl, this.spaceEBO = buffers.get(1), GL4.GL_ELEMENT_ARRAY_BUFFER, spaceSphereQuality, this::createStructureIndicesData);
		
		/* FX */
		GLCoordinates coords = new GLCoordinates(new Bounds(0, 0, 2, 2));		
		this.fxVAO = vertexArrays.get(1);
		
		gl.glBindVertexArray(this.fxVAO);

		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, buffers.get(2));
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, 8 * Double.BYTES, coords.allocDataBuffer(8).putNorth().putSouth().getDataBuffer(), GL4.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(fxPositionAttribute);
		gl.glVertexAttribPointer(fxPositionAttribute, 2, GL4.GL_DOUBLE, false, 0, 0);
		
		/* Connections */
		this.connectionVAO = vertexArrays.get(2);
		this.connectionVBO = buffers.get(3);
		
		this.beginUsingVertexArray(gl, this.connectionVAO);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, this.connectionVBO);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, 6 * Double.BYTES, DoubleBuffer.allocate(6), GL4.GL_DYNAMIC_DRAW);
		
		this.endUsingVertexArray(gl, structuresPositionAttribute);
		
		/* Structures */
		if(crystalMode) {
			int vertexArray = vertexArrays.get(3);
			int bufferID = buffers.get(4);
			
			this.structures.add(new ScaledRenderingInfo(vertexArray, bufferID + 1, crystalQuality + 2));
			
			this.beginUsingVertexArray(gl, vertexArray);
			this.initBufferObject(gl, bufferID, GL4.GL_ARRAY_BUFFER, crystalQuality + 2, this::createStructureShapeData);
			this.endUsingVertexArray(gl, structuresPositionAttribute);
			 
			this.initBufferObject(gl, bufferID + 1, GL4.GL_ELEMENT_ARRAY_BUFFER, crystalQuality + 2, this::createStructureIndicesData);
		} else {
			for(int i = 0; i < structureRenderingQuality; i++) {
				int vertexArray = vertexArrays.get(i + 3);
				int bufferID = buffers.get(i * 2 + 4);
				int layers = i + 8;
				
				this.structures.add(new ScaledRenderingInfo(vertexArray, bufferID + 1, layers));
				
				this.beginUsingVertexArray(gl, vertexArray);
				this.initBufferObject(gl, bufferID, GL4.GL_ARRAY_BUFFER, layers, this::createStructureShapeData);
				this.endUsingVertexArray(gl, structuresPositionAttribute);
				 
				this.initBufferObject(gl, bufferID + 1, GL4.GL_ELEMENT_ARRAY_BUFFER, layers, this::createStructureIndicesData);
			}
		}		
		
		/* Init player */
		this.ajustAcceleration();
	}
	
	private void initShaders(GL4 gl) {		
		try {
			int structuresVert = Shaders.loadShader(gl, "structures.vert", GL4.GL_VERTEX_SHADER);
			int structuresFrag = Shaders.loadShader(gl, "structures.frag", GL4.GL_FRAGMENT_SHADER);
			int spaceVert = Shaders.loadShader(gl, "space.vert", GL4.GL_VERTEX_SHADER);
			int spaceFrag = Shaders.loadShader(gl, "space.frag", GL4.GL_FRAGMENT_SHADER);
			int fxVert = Shaders.loadShader(gl, "fx.vert", GL4.GL_VERTEX_SHADER);
			int fxFrag = Shaders.loadShader(gl, "fx.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.structuresShader = gl.glCreateProgram();
			this.spaceShader = gl.glCreateProgram();
			this.fxShader = gl.glCreateProgram();
			
			gl.glAttachShader(this.structuresShader, structuresVert);
			gl.glAttachShader(this.structuresShader, structuresFrag);
			
			gl.glAttachShader(this.spaceShader, spaceVert);
			gl.glAttachShader(this.spaceShader, spaceFrag);
			
			gl.glAttachShader(this.fxShader, fxVert);
			gl.glAttachShader(this.fxShader, fxFrag);
		} catch(IOException exception) {
			Debug.exception(exception);
		}
		
		gl.glBindFragDataLocation(this.structuresShader, 0, "outColor");
		gl.glBindFragDataLocation(this.spaceShader, 0, "outColor");
		gl.glBindFragDataLocation(this.fxShader, 0, "outColor");

		gl.glLinkProgram(this.structuresShader);
		gl.glLinkProgram(this.spaceShader);
		gl.glLinkProgram(this.fxShader);

		this.loadStructuresUniforms(gl);
		this.loadSpaceUniforms(gl);
		this.loadFXUniforms(gl);
	}
	
	private void loadStructuresUniforms(GL4 gl) {
		this.structModel = gl.glGetUniformLocation(this.structuresShader, "model");
		this.structView = gl.glGetUniformLocation(this.structuresShader, "view");
		this.structProjection = gl.glGetUniformLocation(this.structuresShader, "projection");
		this.color = gl.glGetUniformLocation(this.structuresShader, "color");
		this.camera = gl.glGetUniformLocation(this.structuresShader, "camera");
		this.specularColor = gl.glGetUniformLocation(this.structuresShader, "specularColor");
		this.structuresLightPosition = gl.glGetUniformLocation(this.structuresShader, "lightPosition");
		this.ambientFactor = gl.glGetUniformLocation(this.structuresShader, "ambientFactor");
	}
	
	private void loadSpaceUniforms(GL4 gl) {
		this.spaceModel = gl.glGetUniformLocation(this.spaceShader, "model");
		this.spaceView = gl.glGetUniformLocation(this.spaceShader, "view");
		this.spaceProjection = gl.glGetUniformLocation(this.spaceShader, "projection");
		this.spaceLightPosition = gl.glGetUniformLocation(this.spaceShader, "lightPosition");
		this.windowDimensions = gl.glGetUniformLocation(this.spaceShader, "windowDimensions");
	}
	
	private void loadFXUniforms(GL4 gl) {
		this.colorTexture = gl.glGetUniformLocation(this.fxShader, "colorTexture");
		this.depthTexture = gl.glGetUniformLocation(this.fxShader, "depthTexture");
		this.currentToPreviousViewportMatrix = gl.glGetUniformLocation(this.fxShader, "currentToPreviousViewportMatrix");
		this.fxLightPosition = gl.glGetUniformLocation(this.fxShader, "lightPosition");
		this.exposure = gl.glGetUniformLocation(this.fxShader, "exposure");
		this.pixelSize = gl.glGetUniformLocation(this.fxShader, "pixelSize");
	}
	
	private void initFXFramebuffer(GL4 gl) {
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenFramebuffers(1, buffer);
		this.fxFBO = buffer.get(0);
		
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, this.fxFBO);
		gl.glDrawBuffer(GL4.GL_COLOR_ATTACHMENT0);

		this.fxColorTexture = this.initColorBuffer(gl);
		this.fxDepthTexture = this.initDepthBuffer(gl);
		
		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, this.fxColorTexture, 0);
		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, this.fxDepthTexture, 0);
		
		if(gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER) != GL4.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Failed to initialize the FX framebuffer");
		}
	
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}
	
	private int initColorBuffer(GL4 gl) {
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, buffer);
		int colorTextureID = buffer.get(0);
		
		gl.glActiveTexture(GL4.GL_TEXTURE2);
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, colorTextureID);

		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, 128, 128, 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, null);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_MIRRORED_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_MIRRORED_REPEAT);
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return colorTextureID;
	}
	
	private int initDepthBuffer(GL4 gl) {
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, buffer);
		int depthTextureID = buffer.get(0);
		
		gl.glActiveTexture(GL4.GL_TEXTURE3);

		gl.glBindTexture(GL4.GL_TEXTURE_2D, depthTextureID);
		
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT32, 128, 128, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
				
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		return depthTextureID;
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
	
	public void render3D(AppDrawingContext context) {
		assert context instanceof OpenGLContext;
		
		OpenGLContext glContext = (OpenGLContext) context;
		GL4 gl = glContext.getRenderer();
		
		this.renderUniverse(gl);
		this.update();
	}
	
	private void renderUniverse(GL4 gl) {
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, this.fxFBO);
		
		gl.glClearBufferfv(GL4.GL_COLOR, 0, this.clearColor);
        gl.glClearBufferfv(GL4.GL_DEPTH, 0, this.clearDepth);

        this.setupSpace(gl);
        this.renderSpace(gl);
		
		if(this.project != null) {
			this.setupStructures(gl);
			this.renderStructures(gl);
		}
		
		this.setupFX(gl);
		this.postProcess(gl);
	}
	
	private void setupSpace(GL4 gl) {
		gl.glUseProgram(this.spaceShader);
		
		this.loadMatrix(new Matrix4d().translate(this.player).scale(skyDistance), this.modelData);
		
		gl.glUniformMatrix4dv(this.spaceModel, 1, false, this.modelData);
		gl.glUniformMatrix4dv(this.spaceView, 1, false, this.viewData);
		gl.glUniformMatrix4dv(this.spaceProjection, 1, false, this.projectionData);
		gl.glUniform3d(this.spaceLightPosition, this.sun.x, this.sun.y, this.sun.z);
		
		if(this.bounds != null) {
			gl.glUniform2i(this.windowDimensions, this.bounds.getWidthAsInt(), this.bounds.getHeightAsInt());
		}
	}
	
	private void renderSpace(GL4 gl) {
		gl.glDepthFunc(GL4.GL_ALWAYS);

		gl.glBindVertexArray(this.spaceVAO);
		gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, this.spaceEBO);
		gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, 4 * spaceSphereQuality * (spaceSphereQuality - 1), GL4.GL_UNSIGNED_INT, 0);
	}
	
	private void setupStructures(GL4 gl) {
		gl.glUseProgram(this.structuresShader);
		
		gl.glUniformMatrix4dv(this.structView, 1, false, this.viewData);
		gl.glUniformMatrix4dv(this.structProjection, 1, false, this.projectionData);
		gl.glUniform3d(this.camera, this.player.x, this.player.y, this.player.z);
		gl.glUniform1f(this.ambientFactor, 1.0f);
		gl.glUniform3f(this.specularColor, 1.0f, 1.0f, 1.0f);
		gl.glUniform3d(this.structuresLightPosition, this.sun.x, this.sun.y, this.sun.z);
	}
	
	private void renderStructures(GL4 gl) {
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glEnable(GL4.GL_BLEND);
		
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		
		this.renderStructures0(gl, new ArrayList<>(this.worldGeometry.getElements()), false);
		this.renderConnections(gl, this.worldGeometry.getConnections());

		if(this.scene != RenderingScene.INHERITANCE) {
			this.renderStructures0(gl, new ArrayList<>(this.codeGeometry.getElements()), true); // We need to preserve the original ordering of the list
			this.renderConnections(gl, this.codeGeometry.getConnections());
		}
		
		gl.glDisable(GL4.GL_BLEND);
		gl.glDisable(GL4.GL_DEPTH_TEST);
	}
	
	private void renderStructures0(GL4 gl, List<WorldElement> elements, boolean specialResolve) {		
		synchronized(elements) {
			elements.sort((a, b) -> Double.compare(b.getCenter().distance(this.player) - b.getSize(), a.getCenter().distance(this.player) - a.getSize()));
			
			Map<String, List<WorldElement>> references = new HashMap<>();
			
			for(WorldElement element : elements) {
				/* Rendering */
				Vector3d unitVector = new Vector3d(0.0d, 1.0d, 0.0d);
				Vector3d delta = new Vector3d(this.player).sub(element.getCenter());
				
				double angle = Math.PI + delta.angle(unitVector);
				Vector3d axis = unitVector.cross(delta).normalize();
								
				this.loadMatrix(new Matrix4d().translate(element.getCenter()).rotate(angle, axis).scale(element.getSize()), this.modelData); // For sorting
				gl.glUniformMatrix4dv(this.structModel, 1, false, this.modelData);
												
				if(element != this.selected) {
					gl.glUniform1f(this.ambientFactor, 0.5f);
				} else {
					gl.glUniform1f(this.ambientFactor, 1.0f);
				}
				
				Vector4f color = element.getColor();
				Vector3f spot = element.getSpotColor();
				
				if(this.inside.contains(element)) {
					gl.glUniform4f(this.color, color.x, color.y, color.z, color.w / 2);
				} else {
					gl.glUniform4f(this.color, color.x, color.y, color.z, color.w);
				}
				
				gl.glUniform3f(this.specularColor, spot.x, spot.y, spot.z);
				
				double viewHeight = 2.0d * Math.atan(fov / 2.0d) * this.player.distance(element.getCenter());
				int quality = (int) (this.structures.size() * Math.min(1.0d - 10E-5d, 2 * element.getSize() / viewHeight));
				
				ScaledRenderingInfo info = this.structures.get(quality);
				
				gl.glBindVertexArray(info.getVAO());
				gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, info.getEBO());
				gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, 4 * info.getLayers() * (info.getLayers() - 1), GL4.GL_UNSIGNED_INT, 0);
				
				/* X-Resolve: Dynamic references rendering */
				String component = element.getDescription();

				if(specialResolve && component != null) {					
					Vector3d first = element.getCenter();
										
					this.loadMatrix(new Matrix4d(), this.modelData);
					gl.glUniformMatrix4dv(this.structModel, 1, false, this.modelData);
					gl.glDisable(GL4.GL_DEPTH_TEST);
					gl.glUniform4f(this.color, 1.0f, 1.0f, 1.0f, 1.0f);
					gl.glBindVertexArray(this.connectionVAO);
										
					try {
						int structRef = Integer.parseInt(component);
						
						/* Resolved to a structure reference */
						
						List<WorldElement> world = this.worldGeometry.getElements(); // Multi-threading incoherence is not a problem because of the high inertia of the system: synchronization is not needed
						
						int id = Arrays.<WorldElement>binarySearch(world.toArray(new WorldElement[0]), new WorldElement(structRef, null, null, null, null, null, -1.0d, false), (a, b) -> Integer.compare(a.getID(), b.getID()));
					
						WorldElement second = world.get(id);
						
						Vector4f connectionColor = second.getColor();
						
						gl.glUniform4f(this.color, connectionColor.x, connectionColor.y, connectionColor.z, connectionColor.w);
						
						if(this.selected != element && this.selected != second) {
							gl.glUniform1f(this.ambientFactor, 0.0f);
						} else {
							gl.glUniform1f(this.ambientFactor, 1.0f);
						}
						
						this.connect(gl, first, second.getCenter());
					} catch(NumberFormatException e) {
						if(component.startsWith(SpecificationElement.VAR)) {
							/* Resolved to a variable reference */
							
							List<WorldElement> list = references.get(component);
							
							Random colorGenerator = new Random(component.hashCode());
							
							/* High contrast colors */
							int red = 0x80 + colorGenerator.nextInt(0x80);
							int green = 0x80 + colorGenerator.nextInt(0x80);
							int blue = 0x80 + colorGenerator.nextInt(0x80);

							Vector4f connectionColor = new Vector4f(red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f);
							
							gl.glUniform4f(this.color, connectionColor.x, connectionColor.y, connectionColor.z, connectionColor.w);
							
							if(list != null) {
								for(WorldElement second : list) {
									if(this.selected != null && this.selected.getDescription() != null && this.selected.getDescription().contentEquals(component)) {
										gl.glUniform1f(this.ambientFactor, 1.0f);
									} else {
										gl.glUniform1f(this.ambientFactor, 0.0f);
									}
									
									this.connect(gl, first, second.getCenter());
								}
							} else {
								list = new ArrayList<>();
								references.put(component, list);
							}
							
							list.add(element);
						}
					}
					
					gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
					gl.glEnable(GL4.GL_DEPTH_TEST);
				}
			}
		}
	}
	
	private void renderConnections(GL4 gl, List<Connection> connections) {
		synchronized(connections) {
						
			connections.sort((a, b) -> Double.compare(a.getFirstElement().getCenter().distance(this.player), b.getFirstElement().getCenter().distance(this.player)));
			
			this.loadMatrix(new Matrix4d(), this.modelData);
			gl.glUniformMatrix4dv(this.structModel, 1, false, this.modelData);
			gl.glDepthFunc(GL4.GL_LEQUAL);
			gl.glUniform4f(this.color, 1.0f, 1.0f, 1.0f, 1.0f);
			gl.glUniform1f(this.ambientFactor, 1.0f);
			gl.glBindVertexArray(this.connectionVAO);

			for(Connection connection : connections) {
				WorldElement first = connection.getFirstElement();
				WorldElement second = connection.getSecondElement();
				
				this.connect(gl, first.getCenter(), second.getCenter());
			}
		}
	}
	
	private void connect(GL4 gl, Vector3d first, Vector3d second) {
		double[] unwrapped = new double[] {first.x, first.y, first.z, second.x, second.y, second.z};
		
		DoubleBuffer data = DoubleBuffer.wrap(unwrapped);
						
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, this.connectionVBO);
		gl.glBufferSubData(GL4.GL_ARRAY_BUFFER, 0, 6 * Double.BYTES, data);
		gl.glDrawArrays(GL4.GL_LINES, 0, 2);
	}
	
	private void setupFX(GL4 gl) {
		gl.glUseProgram(this.fxShader);
		
		/* Load sun position in screen coords */
		
		if(this.pitch > 0.0d) {
			Vector2d point = this.getHVCFrom3D(new Vector3d(this.sun).add(this.player.x, 0, this.player.z), this.projectionMatrix);
			gl.glUniform2f(this.fxLightPosition, (float) point.x + 0.5f, (float) point.y + 0.5f);
		} else {
			gl.glUniform2f(this.fxLightPosition, -123.0f, -123.0f);
		}
		
		/* Setup FX uniforms */
		
		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, this.fxColorTexture);
		gl.glUniform1i(this.colorTexture, 2);
		
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, this.fxDepthTexture);
		gl.glUniform1i(this.depthTexture, 3);
		
		gl.glUniform1f(this.exposure, 0.001f);
		
		if(this.bounds != null) {
			gl.glUniform2f(this.pixelSize, 1 / this.bounds.getWidth(), 1 / this.bounds.getHeight());
		}
		
		this.loadMatrix(this.previousViewProjectionMatrix.mul(new Matrix4d(this.projectionMatrix).mul(this.viewMatrix).invert()), this.currentToPreviousViewportData);
		gl.glUniformMatrix4dv(this.currentToPreviousViewportMatrix, 1, false, this.currentToPreviousViewportData);
		
		this.previousViewProjectionMatrix = new Matrix4d(this.projectionMatrix).mul(this.viewMatrix); // Do not trash the projection matrix: it will be reused!
	}
	
	private void postProcess(GL4 gl) {		
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		gl.glBindVertexArray(this.fxVAO);
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	public void render2D(AppDrawingContext context) {		
		this.renderLabels(context, this.worldGeometry.getElements());
		this.renderLabels(context, this.codeGeometry.getElements());
	}
	
	private void renderLabels(AppDrawingContext context, List<WorldElement> elements) {
		synchronized(elements) {
			// Construct a new perspective with a null near plane (I don't know why it has to be null ?!)
			Matrix4d proj = new Matrix4d().perspective(fov, this.bounds.getWidth() / this.bounds.getHeight(), 0.0d, 1.0d);

			for(WorldElement element : elements) {
				/* Context initialization */
				if(!this.inside.isEmpty()) {
					WorldElement enclosing = this.inside.get(0);
					
					if(enclosing == element) {
						continue;
					}
					
					if(element.getCenter().distance(enclosing.getCenter()) > 2 * enclosing.getSize()) {
						//context.setAlpha(0x1F);
					} else {
						//context.setAlpha(0xFF);
					}
				} else {
					//context.setAlpha(0xFF);
				}
				
				
				/* Might be useful after rework of GLTextRenderer;
				
				GL4 gl = (GL4) GLContext.getCurrentGL();
				gl.glBlendFunc(GL4.GL_ONE_MINUS_DST_COLOR, GL4.GL_ONE_MINUS_SRC_COLOR);
				
				 */
				
				/* World ==> Screen-space calculation */
				Vector3d mainSpaceAnchor = element.getCenter().add(0.0d, 2.0d * element.getSize(), 0.0d);
				Vector3d subSpaceAnchor = element.getCenter().sub(0.0d, element.getSize(), 0.0d);

				boolean renderMain = this.checkRenderability(mainSpaceAnchor);
				boolean renderSub = this.checkRenderability(subSpaceAnchor);
				
				double height = -1.0d;
				
				if(renderMain && element.getName() != null) {
					Vector2d screenAnchor = this.getHVCFrom3D(mainSpaceAnchor, proj).mul(1.0d, -1.0d).add(0.75d, 1.0d);
					height = 1.0d - this.getHVCFrom3D(element.getCenter().add(0.0d, element.getSize(), 0.0d), proj).mul(1.0d).add(screenAnchor).y;

					this.renderLabel(context, element.getName(), 0xFFFFFF, screenAnchor, height);
				}
								
				if(renderSub && element.getDescription() != null && !element.getDescription().contentEquals("?")) {
					String description = element.getDescription().replace(SpecificationElement.VAR, "");
										
					Vector2d screenAnchor = this.getHVCFrom3D(subSpaceAnchor, proj).mul(1.0d, -1.0d).add(0.75d, 1.0d);

					if(height < 0.0d) { // Avoid double calculation (considering both heights approximately the same: lim(dist(player, object) -> infty) {dh -> 0})
						height = 1.0d - this.getHVCFrom3D(element.getCenter().add(0.0d, element.getSize(), 0.0d), proj).mul(1.0d).add(screenAnchor).y;
					}
					
					this.renderLabel(context, description, 0x888888, screenAnchor, height);
				}
			}
		}
	}
	
	private void renderLabel(AppDrawingContext context, String label, int color, Vector2d screenAnchor, double height) {
		if(height > 0.01d) {
			Vector2d dimensions = new Vector2d(0.5d, height);
			
			if(screenAnchor.x + dimensions.x > 0  && screenAnchor.y + dimensions.y > 0 && screenAnchor.x < 2.0 && screenAnchor.y < 2.0) {
				// context.setColor(color);
				context.getPrimitives().drawText(label, new Bounds((float) screenAnchor.x, (float) screenAnchor.y, (float) dimensions.x, (float) dimensions.y));
			}
		}
	}
	
	private boolean checkRenderability(Vector3d object) {
		return new Vector3d(object).sub(this.player).angle(this.getDOF()) < Math.PI / 2;
	}
	
	private Vector2d getHVCFrom3D(Vector3d input, Matrix4d projection) { // HVC stands for Homogeneous vector coordinates
		Vector4d point = new Matrix4d(projection).mul(this.viewMatrix).transform(new Vector4d(input, 1.0d));
		return new Vector2d(point.x / point.z, point.y / point.z);
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
		
		long deltaTime = System.nanoTime() - this.lastPositionUpdate;
		this.lastPositionUpdate += deltaTime;
		
		this.player.add(new Vector3d(this.velocity).mul(deltaTime * timeResolution));
	}
	
	private void updateCamera() {
		this.loadMatrix(this.viewMatrix.identity()
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
			this.loadMatrix(this.projectionMatrix.identity().perspective(fov, this.bounds.getWidth() / this.bounds.getHeight(), this.zNear, this.zFar), this.projectionData);
		}
	}
		
	private void ajustAcceleration() {
		if(this.scene != RenderingScene.INHERITANCE) {
			this.generalAcceleration = initialAcceleration * Math.max(10E-10D, this.worldGeometry.getSizeForGeneration(this.inside.size()));
		} else {
			this.generalAcceleration = initialAcceleration;
		}
		
		this.zNear = this.generalAcceleration;

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
		Vector3d cameraDirection = this.getDOF();
		double size = this.worldGeometry.getSizeForGeneration(this.inside.size());
		
		WorldElement found = null;
		double distance = 10000.0d;

		List<WorldElement> menuData = new ArrayList<>();
		
		synchronized(this.worldGeometry.getElements()) {
			for(WorldElement element : this.worldGeometry.getElements()) {
				boolean insideConstraint = this.current == null || this.current.getCenter().distance(element.getCenter()) < this.worldGeometry.getSizeForGeneration(this.inside.size() - 1);
				
				boolean sameSize = Math.abs(element.getSize() - size) < 10E-10D;
				
				if((sameSize && insideConstraint) || size < -42.0d) {
					double currentDistance = this.player.distance(element.getCenter());
	
					if(sameSize) {
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
	
	private Vector3d getDOF() {
		return new Vector3d(-this.viewData.get(2), -this.viewData.get(6), -this.viewData.get(10));
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
			if(element.getName() == null || element.getName().isEmpty()) {
				return "a mysterious structure";
			} else {
				return "'" + element.getName() + "'";
			}
		} else {
			return "the space";
		}
	}
	
	/* Hack */
	private void loadMatrix(Matrix4d matrix, DoubleBuffer modelData) {
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
			
			synchronized(code) {
				int i = 0;
				
				for(WorldElement obj : code) {
					if(obj.getID() == lookAt) {
						lookAtElement = obj;
						lookAt = i; // Changes lookAt variable (reuse) !!!
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
			
			CodeEditor menu = MainMenus.getCodeEditor();
			
			menu.setTargetInstruction(lookAt);
			
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

	public void update(Bounds bounds) {
		this.bounds = bounds;

		this.updatePerspective();
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		/*
		 * Resize buffers
		 */
		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, this.fxColorTexture);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, bounds.getWidthAsInt(), bounds.getHeightAsInt(), 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, null);
		
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, this.fxDepthTexture);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT32, bounds.getWidthAsInt(), bounds.getHeightAsInt(), 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
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
				Point position = ((MoveEvent) event).getPoint();
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
	
	private void updateMouse(Point position) {
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
			this.robot.mouseMove(this.bounds.getXAsInt() + this.bounds.getWidthAsInt() / 2, this.bounds.getYAsInt() + this.bounds.getHeightAsInt() / 2);
		}
	}
	
	private void updateAcceleration(double wheelDelta) {
		this.generalAcceleration = Math.min(this.generalAcceleration * Math.pow(1.01d, 2 * wheelDelta), 1.0d);
		
		this.zNear = this.generalAcceleration;

		this.updatePerspective();
	}
	
	private void onLeftClick() {
		if(this.lookingAt != null) {
			List<WorldElement> elements = this.codeGeometry.getElements();
			
			synchronized(elements) {
				if(elements.contains(this.lookingAt) && (this.lookingAt.getID() & 0xFF000000) != 0) {
					if(this.isFastBindingMode) {
						MainMenus.getStructureList().show();
						
						Map<Integer, StructureMeta> meta = this.project.getStorage().getStructureMeta();
						
						StructureMeta sourceMeta = meta.get(this.selected.getID() & 0xFFFFFF);
						StructureMeta targetMeta = meta.get(this.lookingAt.getID() & 0xFFFFFF);
						
						if(sourceMeta != null && targetMeta != null) {
							SpecificationElement sourceParam = sourceMeta.getSpecification().getElements().get((this.selected.getID() >>> 24) - 1);
							SpecificationElement targetParam = targetMeta.getSpecification().getElements().get((this.lookingAt.getID() >>> 24) - 1);

							sourceParam.setValue(targetParam.getValue());
							
							this.project.getStorage().saveStructureMeta();
							
							System.out.println(targetParam);
							
							this.dispatcher.fire(new MessageEvent("Parameter successfully bound", MessageType.SUCCESS));
							
							this.codeGeometry.buildGeometry(this.project, this.current);
						} else {
							this.dispatcher.fire(new MessageEvent("A problem occured during fast binding", MessageType.ERROR));
						}
					} else {
						this.selected = this.lookingAt;
						this.dispatcher.fire(new MessageEvent("Fast binding: left-click on the target", MessageType.SUCCESS));	
					}
					
					this.isFastBindingMode = !this.isFastBindingMode;
					
					return;
				}
			}
		} else {
			this.isFastBindingMode = false;
		}
		
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
					int id = this.selected.getID();
					
					if((id & 0xFF000000) == 0) {
						int index = elements.indexOf(this.selected);
						
						if(index >= 0) {
							CodeEditor menu = MainMenus.getCodeEditor();
							menu.setTargetInstruction(index);
							menu.show();
						}
					} else {
						int instructionID = this.selected.getID() & 0xFFFFFF;
						int paramID = (this.selected.getID() >>> 24) - 1;
						
						StructureMeta instructionMeta = this.project.getStorage().getStructureMeta().get(instructionID);
				
						if(instructionMeta != null) {							
							SpecificationElement spec = instructionMeta.getSpecification().getElements().get(paramID);
						
							if(spec != null) {
								if(spec instanceof Data) {
									TypeEditor menu = MainMenus.getTypeEditor();
									menu.setTarget((Data) spec);
									menu.show();
								} else if(spec instanceof Reference) {
									ReferenceEditor menu = MainMenus.getReferenceEditor();
									menu.setTarget((Reference) spec);
									menu.show();
								} else {
									throw new RuntimeException("Strange object found");
								}		
							}
						}
					}
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