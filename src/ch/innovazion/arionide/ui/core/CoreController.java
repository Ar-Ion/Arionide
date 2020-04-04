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
package ch.innovazion.arionide.ui.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Vector3f;

import ch.innovazion.arionide.coders.CameraInfo;
import ch.innovazion.arionide.coders.Coder;
import ch.innovazion.arionide.debugging.Debug;
import ch.innovazion.arionide.events.dispatching.IEventDispatcher;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Project;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.managers.CodeManager;
import ch.innovazion.arionide.project.managers.HostStructureStack;
import ch.innovazion.arionide.ui.core.geom.CodeGeometry;
import ch.innovazion.arionide.ui.core.geom.CoreGeometry;
import ch.innovazion.arionide.ui.core.geom.CurrentCodeGeometry;
import ch.innovazion.arionide.ui.core.geom.GeometryException;
import ch.innovazion.arionide.ui.core.geom.HierarchicalGeometry;
import ch.innovazion.arionide.ui.core.geom.WorldElement;
import ch.innovazion.arionide.ui.topology.Bounds;

public class CoreController {
	
	
	private final UserController user;
	
	private final Geometry coreGeometry;
	private final CurrentCodeGeometry mainCodeGeometry;
	private List<CodeGeometry> codeGeometries;
	//private final Geometry inheritanceGeometry;
	
	
	private final AtomicBoolean requestMenuReset = new AtomicBoolean();
	private final AtomicBoolean requestProjectInitialisation = new AtomicBoolean();
	private final AtomicBoolean requestProjectFinalisation = new AtomicBoolean();
	private final AtomicInteger requestTeleportation = new AtomicInteger(-1);
	private final AtomicInteger requestFocus = new AtomicInteger(-1);

	private int requestedDestination;
	private int requestedFocus;
	
	
	private Project project;
	private boolean active;
	private RenderingScene scene = RenderingScene.HIERARCHY;
	private Bounds bounds;
	private WorldElement selection;
	private boolean binding;
	
	private MenuManager menu;
	
	private Vector3f translationVector = new Vector3f();
	private Vector3f glPosition = new Vector3f();

	
	public CoreController() {		
		this.user = new UserController(this);
		
		this.coreGeometry = new CoreGeometry();
		this.mainCodeGeometry = new CurrentCodeGeometry();
		this.codeGeometries = new ArrayList<>();
	}

	public void initMenuManager(IEventDispatcher dispatcher) {
		this.menu = new MenuManager(dispatcher);
	}
	
	protected void updateStatic() {
		if(requestProjectFinalisation.compareAndSet(true, false)) {	// Close project
			project = null;
			scene = null;
			active = false;
			
			menu.go("/");
			menu.setProject(null);
			
			codeGeometries.clear();
		} 
		
		if(requestProjectInitialisation.compareAndSet(true, false)) { // Open project
			project.getStructureManager().getHostStack().reset();

			long seed = project.getProperty("seed", Coder.integerDecoder);
			
			coreGeometry.updateSeed(seed);
			
			mainCodeGeometry.setProject(project);
			coreGeometry.setProject(project);
			
			coreGeometry.requestReconstruction();
			
			setScene(RenderingScene.HIERARCHY);
			
			requestMenuReset.set(true);
			
			menu.go("/");
			menu.setProject(project);
			
			wake();
		}
		
		try {
			if(coreGeometry.processEventQueue()) {
				// onDiscontinuityCrossed();
			}
		
			for(Geometry geometry : codeGeometries) {					
				geometry.processEventQueue();
			}
		} catch (GeometryException exception) {
			Debug.exception(exception);
		}
	}
	
	protected void updateDynamics() {		
		if(requestTeleportation.compareAndSet(0, -1)) {
			WorldElement destination = coreGeometry.getElementByID(requestedDestination);
			
			Vector3f center = destination.getCenter();
			float size = destination.getSize();
			
			user.setPosition(center.add(size * 0.5f, size * 0.125f, 0.0f));
			
			onDiscontinuityCrossed();
			
			if(requestFocus.compareAndSet(-1, 1)) {
				requestedFocus = -1; // Focus on the first code element
			}
		} else {
			requestTeleportation.getAndUpdate(x -> x < 0 ? x : x-1);
		}
		
		if(requestFocus.compareAndSet(0, -1)) {
			WorldElement focus = coreGeometry.getElementByID(requestedFocus);

			CodeManager manager = project.getStructureManager().getCodeManager();
						
			if(focus != null) {
				user.setFocus(focus);
			} else if(manager.hasCode()) {
				manager.getCurrentCode().list().stream().findFirst().ifPresent((e) -> {
					user.setFocus(mainCodeGeometry.getElementByID(e.getID()));
				});
			}
		} else {
			requestFocus.getAndUpdate(x -> x < 0 ? x : x-1);
		}
		
		if(requestMenuReset.compareAndSet(true, false)) {
			if(project.getStructureManager().getCodeManager().hasCode()) {
				menu.selectCode(null);
			} else {
				menu.selectStructure(null);
			}
		}
	}
		
	public void updateUserDynamics() {
		HostStructureStack stack = project.getStructureManager().getHostStack();

		user.updatePhysics();
		user.detectCollisions(stack, coreGeometry, mainCodeGeometry);
		user.detectFocus(stack, coreGeometry, mainCodeGeometry);
		
		
		/* Code to avoid floating-point precision issues (vertex jittering) */
		int currentStruct = project.getStructureManager().getHostStack().getCurrent();
		WorldElement element = coreGeometry.getElementByID(currentStruct);
	
		if(element != null) {
			//translationVector = element.getCenter();
		} else {
			translationVector = new Vector3f();
		}
		
		computeGLPosition();
	}

	private void computeGLPosition() {
		this.glPosition = user.getPosition().sub(translationVector);
	}
	
	public void invalidateMenu() {
		requestMenuReset.set(true);
	}
	
	public void requestFocus(int target) {
		this.requestedFocus = target;
		requestFocus.set(2); // This request will be processed in two ticks
	}
	
	public void requestTeleportation(int target) {
		this.requestedDestination = target;
		requestTeleportation.set(1); // This request will be processed in one tick
	}
	
	public void setScene(RenderingScene scene) {
		this.scene = scene;

		if(scene != RenderingScene.HIERARCHY) {
			user.setPosition(new Vector3f(0.0f, 0.0f, HierarchicalGeometry.MAKE_THE_UNIVERSE_GREAT_AGAIN * 5.0f));
		} else {
			CameraInfo info = project.getProperty("user", Coder.cameraDecoder);
						
			user.setPosition(new Vector3f(info.getX(), info.getY(), info.getZ()));
			
			user.updateYaw(info.getYaw());
			user.updatePitch(info.getPitch());
		}

		onDiscontinuityCrossed();
	}
	
	public void select(int id) {
		selection = mainCodeGeometry.getElementByID(id);
		
		if(selection == null) {
			selection = coreGeometry.getElementByID(id);
		}
	}

	void invalidateCurrentCode() {
		mainCodeGeometry.requestReconstruction();
	}
	
	void invalidateCode() {
		codeGeometries.forEach(Geometry::requestReconstruction);
	}
	
	void invalidateStructures() {
		invalidateCode();
		coreGeometry.requestReconstruction();
	}
	
	void onProjectOpen(Project project) {
		this.project = project;
		requestProjectInitialisation.set(true);
	}
	
	void onProjectClose() {
		requestProjectFinalisation.set(true);
	}
	
	void onMove() {
		user.normaliseCameraQuaternion();
		
		if(isActive() && scene == RenderingScene.HIERARCHY) {
			Vector3f position = user.getPosition();
			CameraInfo info = new CameraInfo(position.x, position.y, position.z, user.getYaw(), user.getPitch());
			project.setProperty("player", info, Coder.cameraEncoder);
		}
	}
	
	void onLeftClick() {
		WorldElement focus = user.getFocus();
		
		if(focus != null) {
			List<WorldElement> elements = this.mainCodeGeometry.getElements();
			
			if(elements.contains(focus) && (focus.getID() & 0xFF000000) != 0) {
				if(binding) {					
					Map<Integer, Structure> meta = this.project.getStorage().getStructures();
					
					Structure sourceMeta = meta.get(selection.getID() & 0xFFFFFF);
					Structure targetMeta = meta.get(user.getFocus().getID() & 0xFFFFFF);
					
					if(sourceMeta != null && targetMeta != null) {
						Parameter sourceParam = sourceMeta.getSpecification().getParameters().get((selection.getID() >>> 24) - 1);
						Parameter targetParam = targetMeta.getSpecification().getParameters().get((focus.getID() >>> 24) - 1);

						project.getStructureManager().getSpecificationManager().bindParameters(sourceParam, targetParam);
						
						mainCodeGeometry.requestReconstruction();
					}
				} else {
					this.selection = user.getFocus();
				}
				
				this.binding = !binding;
				
				return;
			}
		}
			
		this.binding = false;
		menu.click();
	}
	
	void onRightClick() {
		this.selection = user.getFocus();
		
		if(selection != null) {
			List<WorldElement> code = this.mainCodeGeometry.getElements();
			int id = selection.getID();

			if(code.contains(selection)) {				
				if((id & 0xFF000000) == 0) {
					// In the case of a code instruction

				} else {
					// In the case of a specification element
					int instructionID = selection.getID() & 0xFFFFFF;
					int paramID = (selection.getID() >>> 24) - 1;
					
					Structure instructionMeta = project.getStorage().getStructures().get(instructionID);
			
					if(instructionMeta != null) {							
						Parameter param = instructionMeta.getSpecification().getParameters().get(paramID);
					
						if(param != null) {
							// TODO
						}
					}
				}
			} else {
				Structure struct = project.getStorage().getStructures().get(id);
				menu.selectStructure(struct);
			}
		} else {
			menu.selectStructure(null);
		}
	}
	
	void onDiscontinuityCrossed() {
		requestMenuReset.set(true);
		
		try {
			coreGeometry.processEventQueue();
		} catch (GeometryException exception) {
			Debug.exception(exception);
		}

		List<CodeGeometry> buffer = new ArrayList<>();
		List<WorldElement> geometry = coreGeometry.getElements();
				
		WorldElement current = coreGeometry.getElementByID(project.getStructureManager().getHostStack().getCurrent());
		float characteristicLength;
		
		if(current != null) {
			characteristicLength = current.getSize();
		} else {
			characteristicLength = coreGeometry.getSize(0);
		}
		
		for(WorldElement element : geometry) {
			if(isResolvable(element, characteristicLength, 100.0f)) {
				CodeGeometry code = codeGeometries.stream() // Motion continuity: reuse geometries
												  .filter(geom -> geom.getContainer().equals(element))
												  .findAny()
												  .orElseGet(() -> this.generateCodeGeometry(element));

				buffer.add(code);
				
				if(element.equals(current)) {
					mainCodeGeometry.updateCodeGeometry(code);
				}
			}
		}
		
		this.codeGeometries = buffer;
		
		user.resetAcceleration();
		user.accelerate(Math.max(10E-17f, coreGeometry.getRelativeSize(project.getStructureManager().getHostStack().getGeneration())));
		
		computeGLPosition();
	}
	
	private boolean isResolvable(WorldElement element, float characteristicLength, float factor) {
		float distance = element.getCenter().distance(user.getPosition());
		float size = element.getSize();
		return characteristicLength / factor < size && distance < characteristicLength * factor;
	}

	private CodeGeometry generateCodeGeometry(WorldElement container) {
		CodeGeometry code = new CodeGeometry(container);
		
		code.setProject(project);
		code.updateSeed(coreGeometry.getSeed());
		code.requestReconstruction();
		
		return code;
	}
	
	public void updateBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	public void wake() {
		active = true;
	}
	
	public void sleep() {
		active = false;
	}
	
	void toggleActivity() {
		active = !active;
		
		if(!active) {
			user.moveX(0);
			user.moveY(0);
			user.moveZ(0);
		}
	}
	
	void reset() {
		user.reset();
	}
	
	void moveX(int factor) {
		user.moveX(factor);
	}
	
	void moveY(int factor) {
		user.moveY(factor);
	}
	
	void moveZ(int factor) {
		user.moveZ(factor);
	}
	
	void updateYaw(float radiansPerUnitLength) {
		user.updateYaw(radiansPerUnitLength * bounds.getWidth());
	}
	
	void updatePitch(float radiansPerUnitLength) {
		user.updatePitch(radiansPerUnitLength * bounds.getHeight());
	}
	
	void accelerate(double rate) {
		user.accelerate(rate);
	}
	
	
	public boolean isReady() {
		return project != null;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public UserController getUserController() {
		return user;
	}
	
	public MenuManager getMenuManager() {
		return menu;
	}

	public Geometry getCodeGeometry() {
		return mainCodeGeometry;
	}
	
	public List<CodeGeometry> getCodeGeometries() {
		return codeGeometries;
	}
	
	public Geometry getStructuresGeometry() {
		return coreGeometry;
	}
	
	public WorldElement getSelection() {
		return selection;
	}
	
	public HostStructureStack getHostStack() {
		return project.getStructureManager().getHostStack();
	}
	
	public Vector3f getGLPosition() {
		return new Vector3f(glPosition);
	}
	
	public Vector3f getTranslationVector() {
		return new Vector3f(translationVector);
	}
	
	public RenderingScene getCurrentScene() {
		return scene;
	}
}