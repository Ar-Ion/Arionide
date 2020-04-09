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
package ch.innovazion.arionide.ui.core.geom;

import java.util.List;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import ch.innovazion.arionide.lang.symbols.Actor;
import ch.innovazion.arionide.lang.symbols.Node;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.project.mutables.MutableActor;
import ch.innovazion.arionide.ui.ApplicationTints;
import ch.innovazion.arionide.ui.core.Geometry;

public class CoreGeometry extends HierarchicalGeometry {
	public CoreGeometry() {
		super(0.1f, 0.75f);
	}
	
	protected List<HierarchyElement> getHierarchyElements() {
		return this.getProject().getStorage().getHierarchy();
	}
	
	protected WorldElement constructElement(Structure struct, Vector3f position, float size, List<WorldElement> output) {
		if(struct instanceof MutableActor) {
			Actor actor = ((MutableActor) struct).getWrapper();
			
			float nodeSize = size / 40.0f;
			
			constructNodes(actor.getConstants().getNodes(), struct.getIdentifier(), position, new Vector3f(0.2f, 0.4f, 0.0f).mul(size), nodeSize, new Vector4f(ApplicationTints.getColorByName("Inchworm"), 0.5f), output);
			constructNodes(actor.getProperties().getNodes(), struct.getIdentifier(), position, new Vector3f(0.2f, 0.5f, 0.0f).mul(size), nodeSize, new Vector4f(ApplicationTints.getColorByName("Cerulean"), 0.5f), output);
			constructNodes(actor.getState().getNodes(), struct.getIdentifier(), position, new Vector3f(0.2f, 0.6f, 0.0f).mul(size), nodeSize, new Vector4f(ApplicationTints.getColorByName("Apricot"), 0.5f), output);
		}
		
		return super.constructElement(struct, position, size, output);
	}
	
	private void constructNodes(List<Node> nodes, int sourceID, Vector3f center, Vector3f nodePosition, float size, Vector4f color, List<WorldElement> output) {
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		
		if(nodes.size() == 0) {
			return;
		} else if(nodes.size() == 1) {
			Node node = nodes.get(0);
			
			System.out.println(node.getClass());
			
			System.out.println(node.getLabel());
			
			Vector3f position = new Vector3f(up).mul(nodePosition.dot(up)); // Projection along y
			WorldElement element = factory.make(((128 << 24) | sourceID), node.getLabel(), node.getDisplayValue(), position.add(center), color, color, size, false);
			
			output.add(element);
			
			return;
		}
		
		Quaternionf quaternion = new Quaternionf(new AxisAngle4f(Geometry.PI * 2.0f / nodes.size(), new Vector3f(0.0f, 1.0f, 0.0f)));
				
		for(int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			
			WorldElement element = factory.make((((i + 128) & 0xFF) << 24) | sourceID, node.getLabel(), node.getDisplayValue(), new Vector3f(nodePosition).add(center), color, color, size, false);
			
			output.add(element);
			
			nodePosition.rotate(quaternion);
		}
	}
}