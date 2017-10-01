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
	
	private static final float structRelSize = 0.05f;
	private static final float structRelDistance = 0.1f;
	private static final float axisEntropy = 0.5f;
	private static final float axisCorrection = 1.0f;
	private static final float axisCorrectionFlexibility = 5.0f;

	private final Random random = new Random();
	private final List<WorldElement> elements = new ArrayList<>();
	
	protected void buildGeometry(Project project, WorldElement element) {
		Storage storage = project.getStorage();
		storage.loadData(element.getID());
		
		this.elements.clear();
		
		this.build(element, storage.getStructureMeta(), storage.getCurrentData(), element.getSize() * structRelSize);
		
	}
	
	private void build(WorldElement parent, Map<Integer, StructureMeta> meta, List<HierarchyElement> code, float size) {
		Vector3f axis = parent.getAxis();
		Vector3f position = parent.getCenter();
		StructureMeta parentMeta = meta.get(parent.getID());
		
		for(HierarchyElement element : code) {
			StructureMeta structMeta = meta.get(element.getID());
			
			if(structMeta != null) {
				int index = structMeta.getComment().indexOf("code@");
				
				if(index > 0) {
					StructureMeta resolved = meta.get(Integer.parseInt(structMeta.getName().substring(index)));
					
					Vector4f color = new Vector4f(Coloring.getColorByID(resolved.getColorID()), 0.5f);
					Vector3f spotColor = new Vector3f(Coloring.getColorByID(resolved.getSpotColorID()));
	
					axis.normalize(parent.getSize() * structRelDistance);
					
					WorldElement.setAxisGenerator(() -> WorldElement.RANDOM_GENERATOR.get().cross(axis));
					WorldElement object = new WorldElement(element.getID(), resolved.getName(), new Vector3f(position), color, spotColor, size, resolved.isAccessAllowed());
					this.elements.add(object);
					
					this.build(parent, meta, element.getChildren(), size);
					position.add(axis);
					this.applyDerivation(axis, new Vector3f(position).sub(parent.getCenter()).div(parent.getSize()).mul(2.0f));
				}
			}
		}
	}
	
	private void applyDerivation(Vector3f axis, Vector3f relPos) {
		float length = relPos.length();
		
		Vector3f entropy = new Vector3f(this.random.nextFloat() - 0.5f, this.random.nextFloat() - 0.5f, this.random.nextFloat() - 0.5f);
		Vector3f correction = new Vector3f(axis).reflect(new Vector3f(relPos.negate().normalize()));
		
		entropy.normalize(axis.length() * axisEntropy);
		correction.normalize(axis.length() * axisCorrection * (float) Math.pow(length, axisCorrectionFlexibility));

		axis.add(entropy);
		axis.add(correction);
	}
	
	protected List<WorldElement> getElements() {
		return this.elements;
	}
}