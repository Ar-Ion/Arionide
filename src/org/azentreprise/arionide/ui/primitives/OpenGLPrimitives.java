/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.primitives;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;

import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.curve.opengl.TextRegionUtil;
import com.jogamp.graph.geom.SVertex;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class OpenGLPrimitives implements IPrimitives {
	
	private VAOManager manager;
	
	private int uiShader;
	
	private int rgb;
	private int alpha;
	private int pixelSize;
	
	private double pixelWidth;
	private double pixelHeight;
	
	public void init(GL4 gl) {
		try {
			int vert = this.loadShader(gl, "gui.vert", GL4.GL_VERTEX_SHADER);
			int frag = this.loadShader(gl, "gui.frag", GL4.GL_FRAGMENT_SHADER);
			int geom = this.loadShader(gl, "gui.geom", GL4.GL_GEOMETRY_SHADER);
			
			this.uiShader = gl.glCreateProgram();
			
			gl.glAttachShader(this.uiShader, vert);
			gl.glAttachShader(this.uiShader, frag);
			gl.glAttachShader(this.uiShader, geom);
			
			gl.glBindFragDataLocation(this.uiShader, 0, "color");
			
			gl.glLinkProgram(this.uiShader);
			
			this.rgb = gl.glGetUniformLocation(this.uiShader, "rgb");
			this.alpha = gl.glGetUniformLocation(this.uiShader, "alpha");
			this.pixelSize = gl.glGetUniformLocation(this.uiShader, "pixelSize");
			
			IntBuffer buffer = IntBuffer.allocate(1);
			gl.glGenTextures(1, buffer);
			int texture = buffer.get(0);
			
			gl.glActiveTexture(GL4.GL_TEXTURE0);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
			
			TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL4), this.getClass().getResourceAsStream("texture.png"), false, TextureIO.PNG);
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 32, 32, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data.getBuffer());
			
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_EDGE);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
			
			gl.glUniform1i(gl.glGetUniformLocation(this.uiShader, "sampler"), 0);
			
			this.manager = new VAOManager(gl, this.uiShader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void viewportChanged(int width, int height) {
		this.pixelWidth = 2.0d / width;
		this.pixelHeight = 2.0d / height;
	}
	
	public void beginUI(GL4 gl) {
		gl.glUseProgram(this.uiShader);
		
		gl.glEnable(GL4.GL_BLEND);
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glUniform2d(this.pixelSize, this.pixelWidth, this.pixelHeight);
	}
	
	public void endUI(GL4 gl) {
		gl.glDisable(GL4.GL_BLEND);
		gl.glUseProgram(0);
	}
	
	/* WARNING: Using the OpenGL implementation, the Rectangle2D bounds is actually a data structure representing the data (x1, y1, x2, y2). */
	
	public void drawRect(AppDrawingContext context, Rectangle2D bounds) {
		// Disable the geometry shader in order to use regular rectangles
		
		GL4 gl = this.getGL(context);
		GLCoordinates coords = new GLCoordinates(bounds);
		
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putBoundingPoints().getDataBuffer(), (nil, id) -> {
			gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, 4);
	}

	@Override
	public void fillRect(AppDrawingContext context, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRoundRect(AppDrawingContext context, Rectangle2D bounds) {		
		GL4 gl = this.getGL(context);
		GLCoordinates coords = new GLCoordinates(bounds);
		
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putBoundingPoints().getDataBuffer(), (nil, id) -> {
			gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		gl.glDrawArrays(GL4.GL_LINES_ADJACENCY, 0, 4);
	}

	@Override
	public void fillRoundRect(AppDrawingContext context, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLine(AppDrawingContext context, double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		
	}

	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds) {
		return this.drawText(context, text, bounds, 0);
	}

	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds, int yCorrection) {
		GL4 gl = this.getGL(context);
		
		TextRenderer textRenderer = new TextRenderer(context.getFontAdapter().adapt(text, bounds.getWidth(), bounds.getHeight()));
		Rectangle2D textBounds = textRenderer.getBounds(text);
		
		
		
		double x = bounds.getX() + (bounds.getWidth() - textBounds.getWidth()) / 2;
		double y = bounds.getY() + (bounds.getHeight() - textBounds.getHeight() + 1 + yCorrection) / 2;
		
		Dimension renderingArea = context.getSize();
		
		textRenderer.beginRendering(renderingArea.width, renderingArea.height);
		textRenderer.setColor(Color.YELLOW);
		textRenderer.setSmoothing(true);

		textRenderer.draw(text, (int) x, (int) y);
		textRenderer.endRendering();
		
		return new Point2D.Double(x, y);
	}
	
	public void setColor(GL4 gl, float r, float g, float b) {
		gl.glUniform3f(this.rgb, r, g, b);
	}
	
	public void setAlpha(GL4 gl, float alpha) {
		gl.glUniform1f(this.alpha, alpha);
	}
	
	private GL4 getGL(AppDrawingContext context) {
		assert context instanceof OpenGLDrawingContext;
		return ((OpenGLDrawingContext) context).getRenderer();
	}
	
	private int loadShader(GL4 gl, String name, int type) throws IOException {
		InputStream input = this.getClass().getResourceAsStream(name);
		
		byte[] buffer = new byte[128];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int count = 0;
		
		while((count = input.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		
		String code = new String(baos.toByteArray(), Charset.forName("utf8"));

		IntBuffer shaderID = IntBuffer.allocate(1);
		ShaderUtil.createAndCompileShader(gl, shaderID, type, new String[][] {{ code }}, System.err);
		
		return shaderID.get(0);
	}
}