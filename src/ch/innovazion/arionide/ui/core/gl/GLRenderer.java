/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2019 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.core.gl;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.coders.CameraInfo;
import ch.innovazion.arionide.coders.Coder;
import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.events.ActionEvent;
import ch.innovazion.arionide.events.ActionType;
import ch.innovazion.arionide.events.ClickEvent;
import ch.innovazion.arionide.events.Event;
import ch.innovazion.arionide.events.EventHandler;
import ch.innovazion.arionide.events.MessageEvent;
import ch.innovazion.arionide.events.MessageType;
import ch.innovazion.arionide.events.MoveEvent;
import ch.innovazion.arionide.events.PressureEvent;
import ch.innovazion.arionide.events.ProjectCloseEvent;
import ch.innovazion.arionide.events.ProjectEvent;
import ch.innovazion.arionide.events.ProjectOpenEvent;
import ch.innovazion.arionide.events.WheelEvent;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.lang.Data;
import ch.innovazion.arionide.lang.Reference;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.MainMenus;
import ch.innovazion.arionide.menu.code.CodeBrowser;
import ch.innovazion.arionide.menu.code.ReferenceEditor;
import ch.innovazion.arionide.menu.code.TypeEditor;
import ch.innovazion.arionide.menu.structure.StructureBrowser;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.project.managers.CodeManager;
import ch.innovazion.arionide.project.managers.HostStructureStack;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.OpenGLContext;
import ch.innovazion.arionide.ui.core.CoreRenderer;
import ch.innovazion.arionide.ui.core.RenderingScene;
import ch.innovazion.arionide.ui.core.TeleportInfo;
import ch.innovazion.arionide.ui.core.geom.CodeGeometry;
import ch.innovazion.arionide.ui.core.geom.Connection;
import ch.innovazion.arionide.ui.core.geom.CoreGeometry;
import ch.innovazion.arionide.ui.core.geom.CurrentCodeGeometry;
import ch.innovazion.arionide.ui.core.geom.Geometry;
import ch.innovazion.arionide.ui.core.geom.GeometryException;
import ch.innovazion.arionide.ui.core.geom.HierarchicalGeometry;
import ch.innovazion.arionide.ui.core.geom.MergedGeometry;
import ch.innovazion.arionide.ui.core.geom.WorldElement;
import ch.innovazion.arionide.ui.core.gl.fx.FBOFrame;
import ch.innovazion.arionide.ui.core.gl.fx.FBOFrameContext;
import ch.innovazion.arionide.ui.core.gl.links.Link;
import ch.innovazion.arionide.ui.core.gl.links.LinkContext;
import ch.innovazion.arionide.ui.core.gl.links.LinkSettings;
import ch.innovazion.arionide.ui.core.gl.stars.GeneralStarsSettings;
import ch.innovazion.arionide.ui.core.gl.stars.Stars;
import ch.innovazion.arionide.ui.core.gl.stars.StarsContext;
import ch.innovazion.arionide.ui.core.gl.structures.GeneralStructureSettings;
import ch.innovazion.arionide.ui.core.gl.structures.Structure;
import ch.innovazion.arionide.ui.core.gl.structures.StructureContext;
import ch.innovazion.arionide.ui.core.gl.structures.StructureSettings;
import ch.innovazion.arionide.ui.render.PrimitiveFactory;
import ch.innovazion.arionide.ui.render.Text;
import ch.innovazion.arionide.ui.shaders.Shaders;
import ch.innovazion.arionide.ui.shaders.preprocessor.DummySettings;
import ch.innovazion.arionide.ui.topology.Bounds;
import ch.innovazion.arionide.ui.topology.Point;

public class GLRenderer implements CoreRenderer, EventHandler {
		
	private static final List<Integer> qualities = Arrays.asList(16, 20, 24, 28, 32);
	
	private static final int openProjectOP = 1 << 0;
	private static final int closeProjectOP = 1 << 1;
	
	private static final int smallStarsCount = 2048;
	private static final int bigStarsCount = 128;
	
	private static final int forward = KeyEvent.VK_W;
	private static final int backward = KeyEvent.VK_S;
	private static final int left = KeyEvent.VK_A;
	private static final int right = KeyEvent.VK_D;
	private static final int up = KeyEvent.VK_SPACE;
	private static final int down = KeyEvent.VK_SHIFT;
	private static final int worldToggle = KeyEvent.VK_R;
	private static final int spawnKey = KeyEvent.VK_C;

	private static final float initialAcceleration = 0.005f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	private static final float spaceFriction = 0.075f;

	private static final float fov = (float) Math.toRadians(60.0f);
	
	private static final float skyDistance = 32.0f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	
	private static final float timeResolution = 0.00000005f;
	
			
	private final OpenGLContext context;
	private final IEventDispatcher dispatcher;
	private final Geometry coreGeometry;
	private final CurrentCodeGeometry mainCodeGeometry;
	private List<CodeGeometry> codeGeometries;
	//private final Geometry inheritanceGeometry;
		
	private final FloatBuffer modelData = FloatBuffer.allocate(16);
	private final FloatBuffer viewData = FloatBuffer.allocate(16);
	private final FloatBuffer projectionData = FloatBuffer.allocate(16);
	private final FloatBuffer currentToPreviousViewportData = FloatBuffer.allocate(16);

	private final FloatBuffer clearColor = FloatBuffer.allocate(4);
	private final FloatBuffer clearDepth = FloatBuffer.allocate(1);
	
	private final Matrix4f viewMatrix = new Matrix4f();
	private final Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f previousViewProjectionMatrix = new Matrix4f();
	
	private final Stars smallStars = new Stars(smallStarsCount, 1.0f);
	private final Stars bigStars = new Stars(bigStarsCount, 2.0f);
	private final GeneralStarsSettings jointStarsSettings = Utils.bind(GeneralStarsSettings.class, smallStars.getSettings(), bigStars.getSettings());
	
	private final Structure[] structures = qualities.stream().map(Structure::new).toArray(Structure[]::new);
	private final GeneralStructureSettings jointStructureSettings = Utils.bind(GeneralStructureSettings.class, Stream.of(structures).map(Structure::getSettings).toArray(GeneralStructureSettings[]::new));
	
	private final Link link = new Link();
	
	private final FBOFrame fx = new FBOFrame();
	
	private final StaticAllocator allocator = new StaticAllocator(Utils.combine(RenderableObject.class, structures, link, smallStars, bigStars, fx));

	private boolean fxEnabled = true;
	
	private float zNear = 1.0f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	private float zFar = 100.0f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	
	private int structuresShader;
	private int spaceShader;
	private int fxShader;

	private Project project;

	private Bounds bounds;
	private boolean isInWorld = false;
	private boolean isControlDown = false;
	private boolean isFastBindingMode = false;
	
	private WorldElement current;
	private WorldElement lookingAt;
	private WorldElement selected;
	
	private RenderingScene scene = null;
	private int syncOperations = 0;
	
	private boolean needMenuReset = false;

	private float yaw = 0.0f;
	private float pitch = 0.0f;
	private Vector3f user = new Vector3f();
	private Vector3f sun = new Vector3f(0.0f, 2 * skyDistance, 0.0f);
	private Vector3f translate = new Vector3f();
	private Vector3f realUser = new Vector3f();
	
	private Vector3f velocity = new Vector3f();
	private Vector3f acceleration = new Vector3f();
	private float generalAcceleration = initialAcceleration;
	private TeleportInfo teleport;
	
	private long lastPositionUpdate = System.nanoTime();
	
	public GLRenderer(AppDrawingContext context, IEventDispatcher dispatcher) {
		this.context = (OpenGLContext) context;
		this.dispatcher = dispatcher;
		this.coreGeometry = new CoreGeometry();
		this.mainCodeGeometry = new CurrentCodeGeometry();
		this.codeGeometries = new ArrayList<>();
		//this.inheritanceGeometry = null;
		
		dispatcher.registerHandler(this);
	}
	
	public void init(GL4 gl) {
		this.initShaders(gl);
				
		allocator.generate(gl);
		
		this.clearColor.put(0, 0.0f).put(1, 0.0f).put(2, 0.0f).put(3, 1.0f);
		this.clearDepth.put(0, 1.0f);

		StarsContext starsContext = new StarsContext(gl, spaceShader);
		smallStars.init(gl, starsContext, allocator);
		bigStars.init(gl, starsContext, allocator);
		
		StructureContext structureContext = new StructureContext(gl, structuresShader);
		
		for(Structure structure : structures) {
			structure.init(gl, structureContext, allocator);
		}
		
		LinkContext context = new LinkContext(gl, structuresShader);
		link.init(gl, context, allocator);
		
		if(fxEnabled) {
			FBOFrameContext fxContext = new FBOFrameContext(gl, fxShader);
			fx.init(gl, fxContext, allocator);
		}

		/* Init player */
		this.ajustAcceleration();
	}
	
	private void initShaders(GL4 gl) {		
		try {
			int structuresVert = Shaders.loadShader(gl, "structures.vert", DummySettings.VERTEX);
			int structuresFrag = Shaders.loadShader(gl, "structures.frag", DummySettings.FRAGMENT);
			int spaceVert = Shaders.loadShader(gl, "space.vert", DummySettings.VERTEX);
			int spaceFrag = Shaders.loadShader(gl, "space.frag", DummySettings.FRAGMENT);
			int fxVert = Shaders.loadShader(gl, "fx.vert", DummySettings.VERTEX);
			int fxFrag = Shaders.loadShader(gl, "fx.frag", DummySettings.FRAGMENT);
			
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
	}
	
	public void render3D(AppDrawingContext context) {
		assert context instanceof OpenGLContext;
		
		OpenGLContext glContext = (OpenGLContext) context;
		GL4 gl = glContext.getRenderer();
		
		this.update();
		this.loadGeometries(true, true);
		this.renderUniverse(gl);
	}
	
	private void loadGeometries(boolean core, boolean auxiliaries) { 
		// Core geometries are considered as auxiliaries since they totally depend upon their containing core geometry.
		
		if(this.project != null) {
			try {
				if(core) {
					this.coreGeometry.processEventQueue();
				}
				
				/* Code to avoid floating-point precision issues (vertex jittering) */
				int currentStruct = project.getDataManager().getHostStack().getCurrent();
				WorldElement element = coreGeometry.getElementByID(currentStruct);
			
				if(element != null) {
					translate = element.getCenter();
				} else {
					translate = new Vector3f();
				}
				
				if(auxiliaries) {
					for(Geometry geometry : this.codeGeometries) {					
						geometry.processEventQueue();
					}
				}
			} catch (GeometryException exception) {
				Debug.exception(exception);
			}
		}
	}
	
	private void prepareCodeGeometry() {		
		List<CodeGeometry> buffer = new ArrayList<>();
		List<WorldElement> geometry = this.coreGeometry.getElements();
				
		int childrenGeneration = this.project.getDataManager().getHostStack().getGeneration();
		int parentGeneration = childrenGeneration - 2;
		
		float maxDistance = this.coreGeometry.getSize(parentGeneration);
		float minSize = this.coreGeometry.getSize(childrenGeneration) * 0.99f;
				
		for(WorldElement element : geometry) {
			if((parentGeneration < 0 || element.getCenter().distance(this.user) < maxDistance) 		// Under a certain distance
			&& element.getSize() > minSize  														// Bigger than a certain threshold
			&& this.project.getStorage().getCode().containsKey(element.getID())) {  				// Possessing code				
				CodeGeometry code = this.codeGeometries.stream().filter(geom -> geom.getContainer().equals(element)).findAny().orElseGet(() -> this.generateCodeGeometry(element));
				
				code.updateContainer(element);
				
				buffer.add(code);
				
				if(element.equals(this.current)) {
					this.mainCodeGeometry.updateCodeGeometry(code);
				}
			}
		}
		
		this.codeGeometries = buffer;
	}
	
	private CodeGeometry generateCodeGeometry(WorldElement container) {
		CodeGeometry code = new CodeGeometry(container);
		code.setProject(project);
		code.updateSeed(coreGeometry.getSeed());
		code.requestReconstruction();
		return code;
	}
	
	private void renderUniverse(GL4 gl) {
		if(this.fxEnabled) {
			fx.bindFBO(gl);
		}
		
		gl.glClearBufferfv(GL4.GL_COLOR, 0, this.clearColor);
        gl.glClearBufferfv(GL4.GL_DEPTH, 0, this.clearDepth);

        this.setupSpace(gl);
        this.renderSpace(gl);
		
		if(this.project != null) {
			this.setupStructures(gl);
			this.renderStructures(gl);
		}
		
		if(this.fxEnabled) {
			this.setupFX(gl);
			this.postProcess(gl);
		}
	}
	
	private void setupSpace(GL4 gl) {
		gl.glUseProgram(this.spaceShader);
		
		this.loadMatrix(new Matrix4f().translate(realUser).scale(skyDistance), this.modelData);
		
		jointStarsSettings.setModel(this.modelData);
		jointStarsSettings.setView(this.viewData);
		jointStarsSettings.setProjection(this.projectionData);
	}
	
	private void renderSpace(GL4 gl) {
		gl.glDepthFunc(GL4.GL_ALWAYS);
		
		smallStars.bind();
		smallStars.render();
		
		bigStars.bind();
		bigStars.render();
	}
	
	private void setupStructures(GL4 gl) {
		gl.glUseProgram(structuresShader);
		
		loadVP(jointStructureSettings);
		loadVP(link.getSettings());

		jointStructureSettings.setCamera(realUser);
	}
	
	private void renderStructures(GL4 gl) {
		gl.glEnable(GL4.GL_BLEND);
		
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		
		MergedGeometry merged = new MergedGeometry();
				
		merged.addGeometry(coreGeometry);
		
		if(this.scene != RenderingScene.INHERITANCE) {			
			for(Geometry geometry : codeGeometries) {
				merged.addGeometry(geometry);
			}
		}

		try {
			merged.setProject(project);
			merged.requestReconstruction();
			merged.processEventQueue();
			merged.sort(user);
		} catch (GeometryException exception) {
			Debug.exception(exception);
		}
		
		renderConnections(gl, merged.getConnections());
		renderStructures0(gl, merged.getElements());

		gl.glDisable(GL4.GL_BLEND);
	}
	
	private void renderStructures0(GL4 gl, List<WorldElement> elements) {	
		for(WorldElement element : elements) {
			boolean depthTest = project.getDataManager().getHostStack().contains(element.getID());
			
			if(depthTest) {
				// Do not trash the depth buffer with out-of-range vertices
				gl.glEnable(GL4.GL_DEPTH_TEST);
			}
			
			double viewHeight = 2.0d * Math.atan(fov / 2.0d) * this.user.distance(element.getCenter());
			int quality = (int) (structures.length * Math.min(1.0f - 10E-5f, 2 * element.getSize() / viewHeight));
			Structure structure = structures[quality];
			StructureSettings settings = structure.getSettings();

			Vector3f unitVector = new Vector3f(0.0f, 1.0f, 0.0f);
			Vector3f delta = new Vector3f(user).sub(element.getCenter());			
			
			float angle = delta.angle(unitVector);
			Matrix4f model = new Matrix4f();
			
			model.translate(new Vector3f(element.getCenter()).sub(translate));
			
			if(angle > Math.ulp(0.0f)) {
				Vector3f axis = unitVector.cross(delta).normalize();
				model.rotate(Geometry.PI + angle, axis); // For vertices sorting
			}
			
			model.scale(element.getSize());
			
			loadMatrix(model, modelData);
			settings.setModel(modelData);
			
			if(element != this.selected) {
				settings.setAmbientFactor(0.5f);
			} else {
				settings.setAmbientFactor(1.0f);
			}
			
			settings.setColor(element.getColor());
						
			structure.bind();
			structure.render();
			
			if(depthTest) {
				gl.glDisable(GL4.GL_DEPTH_TEST);
			}
		}
	}
	
	private void renderConnections(GL4 gl, List<Connection> connections) {
		LinkSettings settings = link.getSettings();
								
		this.loadMatrix(new Matrix4f(), this.modelData);
		settings.setModel(modelData);
		settings.setColor(new Vector4f(1.0f));
		settings.setAmbientFactor(1.0f);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		
		link.bind();
		
		for(Connection connection : connections) {
			WorldElement first = connection.getFirstElement();
			WorldElement second = connection.getSecondElement();
			
			Vector3f deltaFirst = second.getCenter().sub(first.getCenter()).normalize(first.getSize());
			Vector3f deltaSecond = first.getCenter().sub(second.getCenter()).normalize(second.getSize());
			
			this.connect(gl, first.getCenter().add(deltaFirst), second.getCenter().add(deltaSecond));
		}
		
		gl.glDisable(GL4.GL_DEPTH_TEST);
	}
	
	private void connect(GL4 gl, Vector3f first, Vector3f second) {
		first.sub(translate);
		second.sub(translate);
		
		float[] data = new float[] {first.x, first.y, first.z, second.x, second.y, second.z};
		link.updateData("position", FloatBuffer.wrap(data)); // "position" as defined in Link.java
		link.render();
	}
	
	private void setupFX(GL4 gl) {
		gl.glUseProgram(this.fxShader);
		
		/* Load sun position in screen coords */
		
		if(this.pitch > 0.0f) {
			Vector2f point = this.getHVCFrom3D(new Vector3f(this.sun).add(this.user.x, 0, this.user.z), this.projectionMatrix).add(0.5f, 0.5f);
			fx.getSettings().setLightPosition(point);
		} else {
			fx.getSettings().setLightPosition(new Vector2f(-666.0f, -666.0f));
		}
		
		
		fx.getSettings().setPixelSize(new Vector2f(1 / this.bounds.getWidth(), 1 / this.bounds.getHeight()));

		
		Matrix4f viewProjectionMatrix = new Matrix4f(this.projectionMatrix).mul(this.viewMatrix);
		/* Setup FX uniforms */
		this.loadMatrix(this.previousViewProjectionMatrix.mul(new Matrix4f(viewProjectionMatrix).invert()), this.currentToPreviousViewportData);
		fx.getSettings().setC2PVM(currentToPreviousViewportData);
		
		this.previousViewProjectionMatrix = viewProjectionMatrix; // Do not trash the projection matrix: it will be reused!
	}
	
	private void postProcess(GL4 gl) {		
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
		
		fx.bind();
		fx.render();
	}
	
	public void render2D(AppDrawingContext context) {
		if(this.project != null) {
			this.renderLabels(context, this.coreGeometry.getElements());
			
			for(Geometry geometry : this.codeGeometries) {
				this.renderLabels(context, geometry.getElements());
			}
		}
	}
	
	private void renderLabels(AppDrawingContext context, List<WorldElement> elements) {
		// Construct a new perspective with a null near plane (I don't know why it has to be null ?!)
		Matrix4f proj = new Matrix4f().perspective(fov, this.bounds.getWidth() / this.bounds.getHeight(), 0.0f, 1.0f);
		
		for(WorldElement element : elements) {
			/* Context initialization */
			
			int alpha = 0xFF;
			
			if(current == element) {
				continue;
			}
			
			if(current != null && element.getCenter().distance(current.getCenter()) > current.getSize()) {
				alpha = 0x1F;
			}
			
			GL4 gl = ((OpenGLContext) context).getRenderer();
			
			/* World ==> Screen-space calculation */
			Vector3f mainSpaceAnchor = element.getCenter().add(0.0f, 2.0f * element.getSize(), 0.0f).sub(translate);
			Vector3f subSpaceAnchor = element.getCenter().sub(0.0f, element.getSize(), 0.0f).sub(translate);

			boolean renderMain = this.checkRenderability(mainSpaceAnchor);
			boolean renderSub = this.checkRenderability(subSpaceAnchor);
			
			float height = -1.0f;
			
			if(renderMain && element.getName() != null) {
				Vector2f screenAnchor = this.getHVCFrom3D(mainSpaceAnchor, proj).mul(1.0f, -1.0f).add(0.75f, 1.0f);
				height = 1.0f - this.getHVCFrom3D(element.getCenter().sub(translate).add(0.0f, element.getSize(), 0.0f), proj).mul(1.0f).add(screenAnchor).y;
				
				this.renderLabel(context, element.getName(), ApplicationTints.WHITE, alpha, screenAnchor, height);
			}
			
			if(renderSub && element.getDescription() != null && !element.getDescription().equals("?")) {
				String description = element.getDescription().replace(SpecificationElement.VAR, "");
									
				Vector2f screenAnchor = this.getHVCFrom3D(subSpaceAnchor, proj).mul(1.0f, -1.0f).add(0.75f, 1.0f);

				if(height < 0.0f) { // Avoid double calculation (considering both heights approximately the same: lim(dist(player, object) -> infty) {dh -> 0})
					height = 1.0f - this.getHVCFrom3D(element.getCenter().sub(translate).add(0.0f, element.getSize(), 0.0f), proj).mul(1.0f).add(screenAnchor).y;
				}
				
				this.renderLabel(context, description, ApplicationTints.COMMENT_COLOR, alpha, screenAnchor, height * 0.5f);
			}
			
			gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
	
	private void renderLabel(AppDrawingContext context, String label, int color, int alpha, Vector2f screenAnchor, float height) {
		if(height > 0.01f) {
			Vector2f dimensions = new Vector2f(0.5f, height);

			if(screenAnchor.x + dimensions.x > 0  && screenAnchor.y + dimensions.y > 0 && screenAnchor.x < 2.0f && screenAnchor.y < 2.0f) {
				Text text = PrimitiveFactory.instance().newText(label, color, alpha);
				text.updateBounds(new Bounds(screenAnchor.x, screenAnchor.y, dimensions.x, dimensions.y));
				text.prepare(); // Although updating the bounds already toggles the "reprepare" bit, this may be useful for further implementations...
				context.getRenderingSystem().renderDirect(text);
			}
		}
	}
	
	private boolean checkRenderability(Vector3f object) {
		return new Vector3f(object).sub(realUser).normalize().angle(this.getDOF()) < Geometry.PI / 2;
	}
	
	private Vector2f getHVCFrom3D(Vector3f input, Matrix4f projection) { // HVC stands for Homogeneous vector coordinates
		Vector4f point = new Matrix4f(projection).mul(this.viewMatrix).transform(new Vector4f(input, 1.0f));
		return new Vector2f(point.x / point.z, point.y / point.z);
	}

	private void update() {
		if((syncOperations & closeProjectOP) != 0) {
			// Close project
			project = null;
			scene = null;
			isInWorld = false;
			context.setCursorVisible(true);
			
			generalAcceleration = initialAcceleration;
			velocity = new Vector3f();
			
			syncOperations ^= closeProjectOP;
		} 
		
		if((syncOperations & openProjectOP) != 0) {
			// Open project
			setScene(RenderingScene.HIERARCHY);
			loadGeometries(true, false);
			prepareCodeGeometry();
			loadGeometries(false, true);
			needMenuReset = true;
			
			syncOperations ^= openProjectOP;
		}
		
		if(project != null) {
			this.processTeleportation();
		}
		
		this.updateUser();
		this.updateCamera();
		
		if(project != null) {
			this.detectCollisions();
			this.detectSightFocus();
			
			this.resetMenu();
			
			this.dispatcher.fire(new MessageEvent(this.user + " | Looking at " + this.getElementName(this.lookingAt) + " (" + (this.lookingAt != null ? this.lookingAt.getID() : -1) + ")", MessageType.DEBUG));
		}
	}
	
	private void updateUser() {
		long deltaTime = System.nanoTime() - this.lastPositionUpdate;
		this.lastPositionUpdate += deltaTime;
		
		Vector3f acceleration = new Vector3f(this.acceleration).mul(deltaTime * timeResolution);
		
		this.zNear = generalAcceleration * 1.0f;
		this.zFar = initialAcceleration * 10000.0f;
		
		updatePerspective();
				
		this.velocity.x += Math.sin(this.yaw) * acceleration.x - Math.cos(this.yaw) * acceleration.z;
		this.velocity.y += acceleration.y;
		this.velocity.z += -Math.cos(this.yaw) * acceleration.x - Math.sin(this.yaw) * acceleration.z;
				
		this.velocity.mul(1.0f - spaceFriction * deltaTime * timeResolution);
										
		this.user.add(new Vector3f(this.velocity).mul(deltaTime * timeResolution));
		this.realUser = new Vector3f(user).sub(translate);
	}

	private void updateCamera() {		
		this.loadMatrix(this.viewMatrix.identity()
				.rotate((float) -this.pitch, 1.0f, 0.0f, 0.0f)
				.rotate((float) this.yaw, 0.0f, 1.0f, 0.0f)
				.translate(-realUser.x, -realUser.y, -realUser.z), this.viewData);
		
		if(this.project != null && this.scene == RenderingScene.HIERARCHY) {
			CameraInfo info = new CameraInfo(this.user.x, this.user.y, this.user.z, (float) this.yaw, (float) this.pitch);
			this.project.setProperty("player", info, Coder.cameraEncoder);
		}
	}
	
	private void updatePerspective() {
		if(this.bounds != null) {
			this.loadMatrix(this.projectionMatrix.identity().perspective(fov, this.bounds.getWidth() / this.bounds.getHeight(), this.zNear, this.zFar), this.projectionData);
		}
	}
		
	private void ajustAcceleration() {
		if(this.project != null && this.scene != RenderingScene.INHERITANCE) {
			this.generalAcceleration = initialAcceleration * Math.max(10E-17f, this.coreGeometry.getRelativeSize(project.getDataManager().getHostStack().getGeneration()));
		} else {
			this.generalAcceleration = initialAcceleration;
		}
	}
	
	private void detectCollisions() {
		HostStructureStack stack = project.getDataManager().getHostStack();
		MergedGeometry merged = new MergedGeometry();
				
		try {
			merged.addGeometry(coreGeometry);
			merged.addGeometry(mainCodeGeometry);
			merged.setProject(project);
			merged.requestReconstruction();
			merged.processEventQueue();
			merged.sort(user);
		} catch (GeometryException exception) {
			Debug.exception(exception);
		}
		
		List<WorldElement> collisions = merged.getCollisions(user);
		Collections.reverse(collisions);
		Iterator<WorldElement> iterator = collisions.iterator();
		WorldElement last = null;
		int sync = 0;
				
		while(iterator.hasNext()) {
			WorldElement next = iterator.next();
			
			if(stack.contains(next.getID())) {
				sync++;
			} else {
				last = next;
				break;
			}
		}
				
		synchronized(stack) {
			int delta = stack.getGeneration() - sync;
			
			for(int i = 0; i < delta; i++) {
				// Clear the entries which are no longer colliding with the user
				WorldElement current = merged.getElementByID(stack.pop());
				
				if(current != null) {
					exitElement(current);
				}
			}
			
			if(last != null) {
				// Resync with new entries
				if(enterElement(last)) {
					stack.push(last.getID());
				} else {
					last = null;
				}
				
				while(iterator.hasNext()) {
					WorldElement next = iterator.next();
					
					if(enterElement(next)) {
						stack.push(next.getID());
					}
				}
			}
			
			current = merged.getElementByID(stack.getCurrent());
			
			if(delta != 0 || last != null) {
				this.needMenuReset = true;
				prepareCodeGeometry();
				loadGeometries(false, true);
			}
		}
	}
	
	private boolean enterElement(WorldElement element) {
		if(element.isAccessAllowed()) {
			return true;
		} else {
			this.repulseFrom(element.getCenter());
			return false;
		}
	}
	
	private void exitElement(WorldElement element) {
		;
	}
	
 	private void repulseFrom(Vector3f position) {
		Vector3f normal = new Vector3f(this.user).sub(position).normalize();
		
		if(new Vector3f(this.velocity).normalize().dot(normal) < 0.0d) {
			this.velocity.reflect(normal).normalize(this.generalAcceleration * 32.0f);
		}
 	}
	
	private void detectSightFocus() {
		Vector3f cameraDirection = this.getDOF();
		int generation = project.getDataManager().getHostStack().getGeneration();
				
		float size = this.coreGeometry.getSize(generation);
		
		WorldElement found = null;
		float distance = Float.MAX_VALUE;
		
		for(WorldElement element : this.coreGeometry.getElements()) {
			boolean isInsideSameStruct = generation == 0 || this.current.getCenter().distance(element.getCenter()) < this.coreGeometry.getSize(generation - 1);
			boolean isSameSize = Math.abs(element.getSize() - size) < Math.ulp(size);
						
			if(isSameSize && isInsideSameStruct) {
				float currentDistance = this.user.distance(element.getCenter());
				
				if(currentDistance < distance) {
					if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.user))) {
						found = element;
						distance = currentDistance;
					}
				}
			}
		}
				
		for(WorldElement element : this.mainCodeGeometry.getElements()) {
			float currentDistance = this.user.distance(element.getCenter());
			
			if(currentDistance < distance) {
				if(element.collidesWith(new Vector3f(cameraDirection).normalize(currentDistance).add(this.user))) {
					found = element;
					distance = currentDistance;
				}
			}
		}

		this.lookingAt = found;
	}
	
	private Vector3f getDOF() {
		return new Vector3f(-this.viewData.get(2), -this.viewData.get(6), -this.viewData.get(10));
	}
	
	private void resetMenu() {
		if(needMenuReset) {
			if(project.getDataManager().getCodeManager().hasCode()) {
				CodeBrowser menu = MainMenus.getCodeBrowser();
								
				menu.show();
				menu.select(-1);
			} else {
				StructureBrowser menu = MainMenus.getStructureList();
				
				menu.show();
				menu.select(0);
			}
			
			ajustAcceleration();
			
			needMenuReset = false;
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
	
	/*
	 * Loads the view and the projection matrix in a buffer ready to be uploaded to GPU memory (the loaded shader must be Classic3DSettings-compatible)
	 */
	private void loadVP(General3DSettings settings) {
		settings.setView(viewData);
		settings.setProjection(projectionData);
	}
	
	/* Hack because put(FloatBuffer) does not work */
	private void loadMatrix(Matrix4f matrix, FloatBuffer modelData) {
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

 	public void teleport(TeleportInfo info) {
 		this.teleport = info;
 		info.updateLifeTime(500L);
 	}
 	
	private void processTeleportation() {
		if(teleport != null && teleport.isAlive()) {
			WorldElement teleportElement = coreGeometry.getElementByID(teleport.getDestination());
			WorldElement lookAtElement = mainCodeGeometry.getElementByID(teleport.getFocus());

			if(teleportElement != null) {
				Vector3f center = teleportElement.getCenter();
				float size = teleportElement.getSize();
				
				processTeleportation(center.mul(1 + 0.5f * size / center.length()).add(0.0f, size * 0.125f, 0.0f));
				teleport.updateDestination(-1);
				
				detectCollisions();
			}
			
			requestFullReconstruction();
											
			loadGeometries(true, false);
			prepareCodeGeometry();
			loadGeometries(false, true);
						
			needMenuReset = true;
			resetMenu();
			
			CodeManager manager = project.getDataManager().getCodeManager();
			
			if(lookAtElement != null) {
				processLookAt(lookAtElement);
			} else if(manager.hasCode()) {
				manager.getCurrentCode().list().stream().findFirst().ifPresent((e) -> {
					processLookAt(mainCodeGeometry.getElementByID(e.getID()));
				});
			}
			
			if(!teleport.isAlive()) {
				teleport = null;
			}
 		}
	}
	
	private void processTeleportation(Vector3f position) {
		user.set(position);
	}
	
	private void processLookAt(WorldElement element) {
		CodeBrowser menu = MainMenus.getCodeBrowser();
		
		menu.setSelectedID(element.getID());
		menu.show();
			
		Vector3f lookAtVector = element.getCenter().sub(user).normalize();
		
		this.yaw = Geometry.PI - (float) Math.atan2(lookAtVector.x, lookAtVector.z);
		this.pitch = (float) Math.asin(lookAtVector.y);
		
		this.selected = element;
		
		this.teleport.updateFocus(-1);
	}
	
	public void setScene(RenderingScene scene) {
		assert project != null;

		this.scene = scene;
				
		project.getDataManager().getHostStack().reset();
		codeGeometries.clear();
		
		long seed = project.getProperty("seed", Coder.integerDecoder);
		
		this.coreGeometry.updateSeed(seed);
		
		this.mainCodeGeometry.setProject(project);
		this.coreGeometry.setProject(project);
		
		this.coreGeometry.requestReconstruction();
		
		if(scene != RenderingScene.HIERARCHY) {
			this.processTeleportation(new Vector3f(0.0f, 0.0f, HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN * 5.0f));
			
			this.yaw = 0.0f;
			this.pitch = 0.0f;
		} else {
			CameraInfo info = this.project.getProperty("player", Coder.cameraDecoder);
			
			this.processTeleportation(new Vector3f(info.getX(), info.getY(), info.getZ()));
			
			this.yaw = info.getYaw();
			this.pitch = info.getPitch();
		}

		this.ajustAcceleration();
	}
	
	public void select(int id) {
		selected = mainCodeGeometry.getElementByID(id);
		
		if(selected == null) {
			selected = coreGeometry.getElementByID(id);
		}
	}

	public void update(Bounds bounds) {
		this.bounds = bounds;
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		/*
		 * Resize buffers
		 */
		
		fx.resizeBuffers(gl, bounds.getWidthAsInt(), bounds.getHeightAsInt());
	}

	public <T extends Event> void handleEvent(T event) {
		if(event instanceof ProjectEvent) {
			if(event instanceof ProjectOpenEvent) {
				project = ((ProjectEvent) event).getProject();
				syncOperations |= openProjectOP;
			} else if(event instanceof ProjectCloseEvent) {
				syncOperations |= closeProjectOP;
			}
		}
		
		if(project == null) {
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
						this.acceleration.x = (pressure.isDown() ? this.generalAcceleration : 0.0f);
						break;
					case backward:
						this.acceleration.x = (pressure.isDown() ? -this.generalAcceleration : 0.0f);
						break;
					case left:
						this.acceleration.z = (pressure.isDown() ? this.generalAcceleration : 0.0f);
						break;
					case right:
						this.acceleration.z = (pressure.isDown() ? -this.generalAcceleration : 0.0f);
						break;
					case up:
						this.acceleration.y = (pressure.isDown() ? this.generalAcceleration : 0.0f);
						break;
					case down:
						this.acceleration.y = (pressure.isDown() ? -this.generalAcceleration : 0.0f);
						break;
					case worldToggle:
						if(pressure.isDown()) {
							this.isInWorld = !this.isInWorld;
							
							this.acceleration.set(0.0f, 0.0f, 0.0f);
							
							if(this.isInWorld) {
								this.context.setCursorVisible(false);
							} else {
								this.context.setCursorVisible(true);
							}
						}
	
						break;
					case spawnKey:
						if(this.isControlDown && pressure.isDown()) {
							this.processTeleportation(new Vector3f(0.0f, 0.0f, 0.0f));
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
				updateAcceleration(wheel.getDelta());
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
		this.yaw += (position.getX() - 1.0f) * bounds.getWidth() * 0.0001f;
		this.pitch -= (position.getY() - 1.0f) * bounds.getHeight() * 0.0001f;
		
		float halfPI = Geometry.PI / 2.0f;
		
		if(this.pitch > halfPI) {
			this.pitch = halfPI;
		} else if(this.pitch < -halfPI) {
			this.pitch = -halfPI;
		}
				
		this.yaw %= 4.0f * halfPI;
				
		this.context.moveCursor(this.bounds.getWidthAsInt() / 2, this.bounds.getHeightAsInt() / 2);
	}
	
	private void updateAcceleration(double wheelDelta) {
		this.generalAcceleration = this.generalAcceleration * (float) Math.pow(1.01f, 2 * wheelDelta);
	}
	
	private void onLeftClick() {
		if(this.lookingAt != null) {
			List<WorldElement> elements = this.mainCodeGeometry.getElements();
			
			if(elements.contains(this.lookingAt) && (this.lookingAt.getID() & 0xFF000000) != 0) {
				if(this.isFastBindingMode) {
					MainMenus.getStructureList().show();
					
					Map<Integer, StructureMeta> meta = this.project.getStorage().getStructureMeta();
					
					StructureMeta sourceMeta = meta.get(this.selected.getID() & 0xFFFFFF);
					StructureMeta targetMeta = meta.get(this.lookingAt.getID() & 0xFFFFFF);
					
					if(sourceMeta != null && targetMeta != null) {
						SpecificationElement sourceParam = sourceMeta.getSpecification().getElements().get((this.selected.getID() >>> 24) - 1);
						SpecificationElement targetParam = targetMeta.getSpecification().getElements().get((this.lookingAt.getID() >>> 24) - 1);

						this.dispatcher.fire(this.project.getDataManager().getSpecificationManager().bind(sourceParam, targetParam));
						
						this.mainCodeGeometry.requestReconstruction();
					} else {
						this.dispatcher.fire(new MessageEvent("A problem occured during fast binding", MessageType.ERROR));
					}
				} else {
					this.selected = this.lookingAt;
					this.dispatcher.fire(new MessageEvent("Fast binding: left-click on the target", MessageType.SUCCESS));	
				}
				
				this.isFastBindingMode = !this.isFastBindingMode;
			}
		} else {
			this.isFastBindingMode = false;
			this.dispatcher.fire(new ClickEvent(null, "menuScroll"));
		}
	}
	
	private void onRightClick() {
		if(this.selected != this.lookingAt) {
			this.selected = this.lookingAt; // Note that lookingAt can be null.
		} else {
			this.selected = null;
		}
		
		if(this.selected != null) {
			List<WorldElement> code = this.mainCodeGeometry.getElements();
			
			if(code.contains(this.selected)) {
				int id = this.selected.getID();
				
				if((id & 0xFF000000) == 0) {
					// In the case of a code instruction
					CodeBrowser menu = MainMenus.getCodeBrowser();
					menu.setSelectedID(id);
					menu.show();
				} else {
					// In the case of a specification element
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
				Browser menu = MainMenus.getStructureList();
				menu.setSelectedID(selected.getID());
				menu.show();
			}
		} else {
			Browser menu = MainMenus.getStructureList();
			menu.select(0);
			menu.show();
		}
	}
	
	public boolean isActive() {
		return isInWorld;
	}
	
	public void requestFullReconstruction() {
		coreGeometry.requestReconstruction();
				
		for(CodeGeometry geometry : codeGeometries) {
			geometry.requestReconstruction();
		}
	}
	
	public Geometry getCodeGeometry() {
		return mainCodeGeometry;
	}
	
	public Geometry getStructuresGeometry() {
		return coreGeometry;
	}
	
	public Set<Class<? extends Event>> getHandleableEvents() {
		return Utils.asSet(MoveEvent.class, PressureEvent.class, WheelEvent.class, ActionEvent.class, ProjectOpenEvent.class, ProjectCloseEvent.class);
	}
}