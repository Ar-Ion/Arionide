/*package org.azentreprise.arionide.ui.core.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.debugging.IAm;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.core.RenderingScene;
import org.azentreprise.arionide.ui.menu.edition.Coloring;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class InheritanceGeometry extends HierarchicalGeometry {	
	private static final float structRelSizeInheritance = 0.5f;
			
	private List<HierarchyElement> inheritanceBuffer = Arrays.asList();
	private List<HierarchyElement> callGraphBuffer = Arrays.asList();
	
	private InheritanceGenerator inheritanceGenerator;

	private WorldElement selected;
	
	public InheritanceGeometry() {
		super(0.5f, 2.5f);
	}
	
	@IAm("constructing the world's geometry")
	protected void construct(List<WorldElement> elements, List<Connection> connections) {
		WorldElement.setSeed(this.getSeed());

		Storage storage = this.getProject().getStorage();
		
		// this.inheritanceGenerator = new InheritanceGenerator(storage.getInheritance(), this::loadInheritance);
		
		Map<Integer, StructureMeta> metaData = storage.getStructureMeta();

		WorldElement.setAxisGenerator(() -> new Vector3f(0.0f, 1.0f, 0.0f));
		WorldElement.setBaseGenerator((axis) -> new Vector3f(1.0f, 1.0f, 0.0f));
		this.build(this.inheritance, this.inheritanceBuffer, metaData, structRelSizeInheritance, 2.5f);
	}
	
	private void build(List<WorldElement> list, List<HierarchyElement> elements, Map<Integer, StructureMeta> metaData, float structRelSize, float subStructDistCenterRelSize) {
		synchronized(list) {
			WorldElement main = new WorldElement(-1, null, null, new Vector3f(), new Vector4f(), new Vector3f(), -1.0f, true);
			float virtualSize = structInitialSize / structRelSize;
			boolean flag = false;
			this.build(main, list, elements, metaData, virtualSize, structRelSize, subStructDistCenterRelSize, flag);
			
			list.sort((a, b) -> Integer.compare(a.getID(), b.getID())); // Allows binary search
		}
	}
	
	private void build(WorldElement parent, List<WorldElement> list, List<HierarchyElement> elements, Map<Integer, StructureMeta> metaData, float size, float structRelSize, float subStructDistCenterRelSize, boolean flag) {
		if(elements != null && elements.size() > 0) {
			size *= structRelSize;

			Quaternionf quaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / (elements.size() - (elements.contains(new HierarchyElement(-1, null)) ? 1 : 0)), parent.getAxis()));
			Vector3f base = elements.size() != 1 || size != structInitialSize ? parent.getBaseVector() : new Vector3f();
			
			for(HierarchyElement element : elements) {
				if(element.getID() < 0) {
					flag = true;
					continue;
				} // cf. Inheritance Generator
				
				Vector3f position = new Vector3f(base.rotate(quaternion)).mul(subStructDistCenterRelSize * size / structRelSize).add(parent.getCenter());
				
				StructureMeta structMeta = metaData.get(element.getID());

				if(structMeta != null) {
					Vector4f color = new Vector4f(Coloring.getColorByID(structMeta.getColorID()), 0.6f);
					Vector3f spotColor = new Vector3f(Coloring.getColorByID(structMeta.getSpotColorID()));
					boolean access = structMeta.isAccessAllowed();

					if(list == this.inheritance) {
						access = false;

						if(flag) {
							spotColor = new Vector3f(0.0f, 0.0f, 0.0f); // Children
						} else if(parent.getID() < 0) {
							spotColor = new Vector3f(0.5f, 0.5f, 0.5f); // Reference
						} else {
							spotColor = new Vector3f(1.0f, 1.0f, 1.0f); // Parents
						}
					}
					
					WorldElement object = new WorldElement(element.getID(), structMeta.getName(), structMeta.getComment(), position, color, spotColor, size, access);
					list.add(object);
					
					this.build(object, list, element.getChildren(), metaData, size, structRelSize, subStructDistCenterRelSize, flag);
				}
			}
		}
	}

	private void loadInheritance(List<HierarchyElement> elements) {
		if(this.currentProject != null) {
			this.inheritanceBuffer = new ArrayList<>(elements);
			this.buildGeometry(this.currentProject);
		}
	}
	
	protected void loadScene(RenderingScene scene) {
		this.currentScene = scene;
		
		switch(scene) {
			case INHERITANCE:
				if(this.selected != null) {
					this.current = this.inheritance;
					this.inheritanceGenerator.generate(this.selected.getID());
				} else {
					this.dispatcher.fire(new MessageEvent("Please select a structure to view its inheritance", MessageType.ERROR));
				}
				
				break;
			case HIERARCHY:
				this.current = this.hierarchy;
				break;
			case CALLGRAPH:
				if(this.selected != null) {
					this.current = this.callGraph;
				} else {
					this.dispatcher.fire(new MessageEvent("Please first select a structure to view its call graph", MessageType.ERROR));
				}
				
				break;
		}
	}
	
	protected void select(WorldElement element) {
		if(this.selected != element) {
			this.selected = element;
			
			if(this.currentScene.equals(RenderingScene.INHERITANCE)) {
				this.current = this.inheritance;
				this.inheritanceGenerator.generate(element.getID());
			}
		}
	}
	
	protected float getSizeForGeneration(int count) {
		return this.current != this.hierarchy ? -666.0f : (float) Math.pow(structRelSizeHierarchy, count);
	}

	public List<WorldElement> getCollisions(Vector3f player) {
		synchronized(this.current) {
			List<WorldElement> collisions = new ArrayList<>();
			
			for(WorldElement element : this.current) {
				if(element != null && element.collidesWith(player)) {
					collisions.add(element);
				}
			}
			
			return collisions;
		}
	}
	
	public List<WorldElement> getElements() {
		return this.current;
	}

	public List<Connection> getConnections() {
		return this.currentConnections;
	}
	
	protected abstract List<HierarchyElement> getHierarchyElements();
}*/
