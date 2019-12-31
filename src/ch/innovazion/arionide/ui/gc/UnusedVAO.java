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
package ch.innovazion.arionide.ui.gc;

import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;

public class UnusedVAO implements Trashable {

	private final IntBuffer vao;
	private final IntBuffer buffers;
	
	public UnusedVAO(int vao, int... buffers) {
		this.vao = IntBuffer.wrap(new int[] {vao});
		this.buffers = IntBuffer.wrap(buffers);
	}
	
	public void burn(TrashContext context) {
		GL4 gl = ((GLTrashContext) context).getGL();
		
		gl.glDeleteBuffers(this.buffers.limit(), this.buffers);
		gl.glDeleteVertexArrays(1, this.vao);
	}

	public boolean checkContextCompatibility(TrashContext context) {
		return context instanceof GLTrashContext;
	}
}