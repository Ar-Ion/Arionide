/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.ui.AppDrawingContext;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.OpenGLContext;
import ch.innovazion.arionide.ui.core.CoreController;
import ch.innovazion.arionide.ui.core.RenderingScene;
import ch.innovazion.arionide.ui.core.UserController;
import ch.innovazion.arionide.ui.core.geom.Connection;
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

public class GLRenderer {
		
	private static final List<Integer> qualities = Arrays.asList(16, 20, 24, 28, 32);

	private static final int smallStarsCount = 2048;
	private static final int bigStarsCount = 128;
	
	private static final float fov = (float) Math.toRadians(60.0f);
	
	private static final float skyDistance = 32.0f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
		
	
	private final CoreController controller;
	
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

	private boolean fxEnabled = false;
	
	private Bounds bounds;

	private float zNear = 1.0f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	private float zFar = 100.0f * HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN;
	
	private Vector3f sun = new Vector3f(0.0f, 2 * skyDistance, 0.0f);
	
	private int structuresShader;
	private int spaceShader;
	private int fxShader;

	public GLRenderer(CoreController controller) {
		this.controller = controller;
	}
	
	public void init(AppDrawingContext context) {
		assert context instanceof OpenGLContext;
		
		OpenGLContext glContext = (OpenGLContext) context;
		GL4 gl = glContext.getRenderer();
		
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
		
		LinkContext linkContext = new LinkContext(gl, structuresShader);
		link.init(gl, linkContext, allocator);
		
		if(fxEnabled) {
			FBOFrameContext fxContext = new FBOFrameContext(gl, fxShader);
			fx.init(gl, fxContext, allocator);
		}
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
			
			gl.glAttachShader(structuresShader, structuresVert);
			gl.glAttachShader(structuresShader, structuresFrag);
			
			gl.glAttachShader(spaceShader, spaceVert);
			gl.glAttachShader(spaceShader, spaceFrag);
			
			gl.glAttachShader(fxShader, fxVert);
			gl.glAttachShader(fxShader, fxFrag);
		} catch(IOException exception) {
			Debug.exception(exception);
		}
		
		gl.glBindFragDataLocation(structuresShader, 0, "outColor");
		gl.glBindFragDataLocation(spaceShader, 0, "outColor");
		gl.glBindFragDataLocation(fxShader, 0, "outColor");

		gl.glLinkProgram(structuresShader);
		gl.glLinkProgram(spaceShader);
		gl.glLinkProgram(fxShader);
	}
	
	public void render3D(AppDrawingContext context) {
		assert context instanceof OpenGLContext;
		
		OpenGLContext glContext = (OpenGLContext) context;
		GL4 gl = glContext.getRenderer();
		
		renderUniverse(gl);
	}
	
	private void renderUniverse(GL4 gl) {
		if(fxEnabled) {
			fx.bindFBO(gl);
		}
		
		gl.glClearBufferfv(GL4.GL_COLOR, 0, clearColor);
        gl.glClearBufferfv(GL4.GL_DEPTH, 0, clearDepth);

        setupSpace(gl);
        renderSpace(gl);
		
		if(controller.isReady()) {
			setupStructures(gl);
			renderStructures(gl);
		}
		
		if(fxEnabled) {
			setupFX(gl);
			postProcess(gl);
		}
	}
	
	private void setupSpace(GL4 gl) {
		gl.glUseProgram(spaceShader);
		
		loadMatrix(new Matrix4f().translate(controller.getGLPosition()).scale(skyDistance), modelData);
		
		jointStarsSettings.setModel(modelData);
		jointStarsSettings.setView(viewData);
		jointStarsSettings.setProjection(projectionData);
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
		
		loadViewProjection(jointStructureSettings);
		loadViewProjection(link.getSettings());

		jointStructureSettings.setCamera(controller.getGLPosition());
	}
	
	private void renderStructures(GL4 gl) {
		gl.glEnable(GL4.GL_BLEND);
		
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		
		MergedGeometry merged = new MergedGeometry();
				
		merged.addGeometry(controller.getStructuresGeometry());
		
		if(controller.getCurrentScene() != RenderingScene.INHERITANCE) {			
			for(Geometry geometry : controller.getCodeGeometries()) {
				merged.addGeometry(geometry);
			}
		}

		try {
			merged.requestReconstruction();
			merged.processEventQueue();
			merged.sort(controller.getUserController().getPosition());
		} catch (GeometryException exception) {
			Debug.exception(exception);
		}
		
		renderConnections(gl, merged.getConnections());
		renderStructures0(gl, merged.getElements());

		gl.glDisable(GL4.GL_BLEND);
	}
	
	private void renderStructures0(GL4 gl, List<WorldElement> elements) {	
		for(WorldElement element : elements) {
			boolean depthTest = controller.getHostStack().contains(element.getID());
			
			if(depthTest) {
				// Do not trash the depth buffer with out-of-range vertices
				gl.glEnable(GL4.GL_DEPTH_TEST);
			}
			
			Vector3f delta = controller.getUserController().getPosition().sub(element.getCenter());

			double viewHeight = 2.0d * Math.atan(fov / 2.0d) * delta.length();
			int quality = (int) (structures.length * Math.min(1.0f - 10E-5f, 2 * element.getSize() / viewHeight));
			Structure structure = structures[quality];
			StructureSettings settings = structure.getSettings();

			Vector3f unitVector = new Vector3f(0.0f, 1.0f, 0.0f);
			
			float angle = delta.angle(unitVector);
			Matrix4f model = new Matrix4f();
			
			model.translate(element.getCenter().sub(controller.getTranslationVector()));
			
			if(angle > Math.ulp(0.0f)) {
				Vector3f axis = unitVector.cross(delta).normalize();
				model.rotate(Geometry.PI + angle, axis); // For vertices sorting
			}
			
			model.scale(element.getSize());
			
			loadMatrix(model, modelData);
			settings.setModel(modelData);
			
			if(element != controller.getSelection()) {
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
		first.sub(controller.getTranslationVector());
		second.sub(controller.getTranslationVector());
		
		float[] data = new float[] {first.x, first.y, first.z, second.x, second.y, second.z};
		link.updateData("position", FloatBuffer.wrap(data)); // "position" as defined in Link.java
		link.render();
	}
	
	private void setupFX(GL4 gl) {
		gl.glUseProgram(this.fxShader);
		
		/* Load sun position in screen coords */
		
		if(controller.getUserController().getPitch() > 0.0f) {
			Vector3f user = controller.getUserController().getPosition();
			Vector2f point = this.getHVCFrom3D(new Vector3f(this.sun).add(user.x, 0, user.z), this.projectionMatrix).add(0.5f, 0.5f);
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
		this.renderLabels(context, controller.getStructuresGeometry().getElements());
		
		for(Geometry geometry : controller.getCodeGeometries()) {
			this.renderLabels(context, geometry.getElements());
		}
	}
	
	private void renderLabels(AppDrawingContext context, List<WorldElement> elements) {
		// Construct a new perspective with a null near plane (I don't know why it has to be null ?!)
		Matrix4f proj = new Matrix4f().perspective(fov, this.bounds.getWidth() / this.bounds.getHeight(), 0.0f, 1.0f);
		
		Vector3f translation = controller.getTranslationVector();
		int currentID = controller.getHostStack().getCurrent();
		WorldElement current = controller.getStructuresGeometry().getElementByID(currentID);

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
			Vector3f mainSpaceAnchor = element.getCenter().add(0.0f, 2.0f * element.getSize(), 0.0f).sub(translation);
			Vector3f subSpaceAnchor = element.getCenter().sub(0.0f, element.getSize(), 0.0f).sub(translation);

			boolean renderMain = this.checkRenderability(mainSpaceAnchor);
			boolean renderSub = this.checkRenderability(subSpaceAnchor);
			
			float height = -1.0f;
			
			if(renderMain && element.getName() != null) {
				Vector2f screenAnchor = this.getHVCFrom3D(mainSpaceAnchor, proj).mul(1.0f, -1.0f).add(0.75f, 1.0f);
				height = 1.0f - this.getHVCFrom3D(element.getCenter().sub(translation).add(0.0f, element.getSize(), 0.0f), proj).mul(1.0f).add(screenAnchor).y;
				
				this.renderLabel(context, element.getName(), ApplicationTints.WHITE, alpha, screenAnchor, height);
			}
			
			if(renderSub && element.getDescription() != null && !element.getDescription().equals("?")) {
				String description = element.getDescription().replace(SpecificationElement.VAR, "");
									
				Vector2f screenAnchor = this.getHVCFrom3D(subSpaceAnchor, proj).mul(1.0f, -1.0f).add(0.75f, 1.0f);

				if(height < 0.0f) { // Avoid double calculation (considering both heights approximately the same: lim(dist(player, object) -> infty) {dh -> 0})
					height = 1.0f - this.getHVCFrom3D(element.getCenter().sub(translation).add(0.0f, element.getSize(), 0.0f), proj).mul(1.0f).add(screenAnchor).y;
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
		return new Vector3f(object).sub(controller.getGLPosition()).normalize().angle(this.getCameraDirection()) < Geometry.PI / 2;
	}
	
	private Vector2f getHVCFrom3D(Vector3f input, Matrix4f projection) { // HVC stands for Homogeneous vector coordinates
		Vector4f point = new Matrix4f(projection).mul(this.viewMatrix).transform(new Vector4f(input, 1.0f));
		return new Vector2f(point.x / point.z, point.y / point.z);
	}
	
	public void updateViewport(Bounds bounds) {
		this.bounds = bounds;
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		fx.resizeBuffers(gl, bounds.getWidthAsInt(), bounds.getHeightAsInt());
	}
	
	public void updateCamera() {
		UserController user = controller.getUserController();
		
		this.zNear = user.getAcceleration() * 3.0f;		

		loadMatrix(viewMatrix.identity()
				.rotate(-user.getPitch(), 1.0f, 0.0f, 0.0f)
				.rotate(user.getYaw(), 0.0f, 1.0f, 0.0f)
				.translate(controller.getGLPosition().negate()), this.viewData);
		
		loadMatrix(projectionMatrix.identity().perspective(fov, bounds.getWidth() / bounds.getHeight(), zNear, zFar), projectionData);
	}
	
	public Vector3f getCameraDirection() {
		return new Vector3f(-this.viewData.get(2), -this.viewData.get(6), -this.viewData.get(10));
	}

	/*
	 * Loads the view and the projection matrix in a buffer ready to be uploaded to GPU memory (the loaded shader must be Classic3DSettings-compatible)
	 */
	private void loadViewProjection(General3DSettings settings) {
		settings.setView(viewData);
		settings.setProjection(projectionData);
	}

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
}