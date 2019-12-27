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
package ch.innovazion.arionide.ui.core.gl.fx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;

import com.jogamp.opengl.GL4;

import ch.innovazion.arionide.ui.core.gl.RenderableObject;
import ch.innovazion.arionide.ui.core.gl.StaticAllocator;

public class FBOFrame extends RenderableObject<FBOFrameContext, FBOFrameSettings> {

	private final FloatBuffer buffer = FloatBuffer.wrap(new float[] { -1.0f,  1.0f, 
																 	   1.0f,  1.0f, 
																      -1.0f, -1.0f, 
																	   1.0f, -1.0f });
	
	private int fbo = -1;
	private int colorBuffer = -1;
	private int depthBuffer = -1;
	
	public FBOFrame() {
		super(new FBOFrameSettings());
	}
	
	public void init(GL4 gl, FBOFrameContext context, StaticAllocator allocator) {
		super.init(gl, context, allocator);
		
		IntBuffer buffer = IntBuffer.allocate(1);
		gl.glGenFramebuffers(1, buffer);
		this.fbo = buffer.get(0);
		
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo);
		gl.glDrawBuffer(GL4.GL_COLOR_ATTACHMENT0);

		IntBuffer textures = IntBuffer.allocate(2);
		gl.glGenTextures(2, textures);
		
		this.colorBuffer = textures.get();
		this.depthBuffer = textures.get();
		
		
		// TODO use texture allocator
		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, colorBuffer);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 1, 1, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_MIRRORED_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_MIRRORED_REPEAT);
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		
		
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, depthBuffer);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT, 1, 1, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
		
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
		

		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, this.colorBuffer, 0);
		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, this.depthBuffer, 0);
		
		if(gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER) != GL4.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Failed to initialize the FX framebuffer");
		}
	
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
	}

	public void bindFBO(GL4 gl) {
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo);
	}
	
	public void resizeBuffers(GL4 gl, int width, int height) {
		gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, fbo);

		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, colorBuffer);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, width, height, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, null);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0); // Unbind

		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, depthBuffer);
		gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, 0); // Unbind
	}
	
	protected void setupData(FBOFrameContext context, StaticAllocator allocator) {
		setupFloatBuffer(GL4.GL_ARRAY_BUFFER, allocator.popVBO(this), buffer, GL4.GL_STATIC_DRAW);
		setupFloatAttribute(context.getPositionAttribute(), 2, 0, 0);
	}

	protected void update(GL4 gl, FBOFrameContext context, FBOFrameSettings settings) {
		Vector2f lightPosition = settings.getLightPosition();
		Vector2f pixelSize = settings.getPixelSize();

		gl.glActiveTexture(GL4.GL_TEXTURE2);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, colorBuffer);
		gl.glUniform1i(context.getColorTextureUniform(), 2);
		
		gl.glActiveTexture(GL4.GL_TEXTURE3);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, depthBuffer);
		gl.glUniform1i(context.getDepthTextureUniform(), 3);
				
		gl.glUniformMatrix4fv(context.getCurrentToPreviousViewportMatrixUniform(), 1, false, settings.getC2PVM());
		gl.glUniform2f(context.getLightPositionUniform(), lightPosition.x, lightPosition.y);
		gl.glUniform1f(context.getExposureUniform(), settings.getExposure());
		gl.glUniform2f(context.getPixelSizeUniform(), pixelSize.x, pixelSize.y);
	}

	protected void renderObject(GL4 gl) {
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}

	protected int getBufferCount() {
		return 1;
	}
}