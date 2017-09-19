package org.azentreprise.arionide.ui.core.opengl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.Storage;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.menu.edition.Coloring;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class CodeGeometry {
	
	private static final float structRelSize = 0.1f;
	private static final float structRelDistance = 0.2f;
	private static final float axisEntropy = 0.1f;
	
	private final Random random = new Random();
	private final List<WorldElement> elements = new ArrayList<>();
	
	protected void buildGeometry(Project project, WorldElement element) {
		Storage storage = project.getStorage();
		storage.loadData(element.getID());
		this.build(element, storage.getStructureMeta(), storage.getCurrentData(), element.getSize() * structRelSize);
	}
	
	private void build(WorldElement parent, Map<Integer, StructureMeta> meta, List<HierarchyElement> code, float size) {
		Vector3f axis = parent.getAxis();
		Vector3f position = parent.getCenter();
		StructureMeta parentMeta = meta.get(parent.getID());
		
		for(HierarchyElement element : code) {
			StructureMeta structMeta = meta.get(element.getID());
			
			if(structMeta != null) {
				Vector4f color = new Vector4f(Coloring.getColorByID(structMeta.getColorID()), 0.5f);
				Vector3f spotColor = new Vector3f(Coloring.getColorByID(structMeta.getSpotColorID()));
				
				WorldElement.setAxisGenerator(() -> WorldElement.RANDOM_GENERATOR.get().cross(axis));
				WorldElement object = new WorldElement(element.getID(), structMeta.getName(), position, color, spotColor, size, structMeta.isAccessAllowed());
				this.elements.add(object);
				
				this.build(parent, meta, code, size);
				
				position.add(axis);
				this.applyDerivation(axis);
			}
		}
	}
	
	private void applyDerivation(Vector3f axis) {
		Vector3f derivated = new Vector3f(this.random.nextFloat() - 0.5f, this.random.nextFloat() - 0.5f, this.random.nextFloat() - 0.5f);
		derivated.normalize(axis.length() * axisEntropy);
		axis.add(derivated);
	}
	
	protected List<WorldElement> getElements() {
		return this.elements;
	}
}