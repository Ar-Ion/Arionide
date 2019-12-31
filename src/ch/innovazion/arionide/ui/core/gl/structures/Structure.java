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
package ch.innovazion.arionide.ui.core.gl.structures;

import java.util.Arrays;
import java.util.List;

import org.joml.Vector4f;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.BufferGenerator;
import ch.innovazion.arionide.ui.core.gl.BufferObject;

public class Structure extends BufferObject<StructureContext, StructureSettings> {

	private final int layers;
	
	private final StructureTessellator tessellator;
	private final StructureIndicesGenerator indicesGenerator;
	
	public Structure(int layers) {
		super(new StructureSettings());
		
		this.layers = layers;
		
		this.tessellator = new StructureTessellator(layers);
		this.indicesGenerator = new StructureIndicesGenerator(layers);
		
		setBufferInitializer(this.tessellator, (context) -> setupFloatAttribute(context.getPositionAttribute(), 3, 0, 0));
	}
	
	protected void update(GL4 gl, StructureContext context, StructureSettings settings) {
		Vector4f color = settings.getColor();
		
		gl.glUniformMatrix4fv(context.getModelUniform(), 1, false, settings.getModel());
		gl.glUniformMatrix4fv(context.getViewUniform(), 1, false, settings.getView());
		gl.glUniformMatrix4fv(context.getProjectionUniform(), 1, false, settings.getProjection());
		gl.glUniform4f(context.getColorUniform(), color.x, color.y, color.z, color.w);
		gl.glUniform1f(context.getAmbientFactorUniform(), settings.getAmbientFactor());
	}

	protected void renderObject(GL4 gl) {
		gl.glDrawElements(GL4.GL_TRIANGLE_STRIP, 4 * layers * (layers - 1), GL4.GL_UNSIGNED_INT, 0);		
	}

	protected List<BufferGenerator> getGenerators() {
		return Arrays.asList(this.tessellator, this.indicesGenerator);
	}

	protected int getBufferCount() {
		return 2;
	}
}