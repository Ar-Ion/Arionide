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

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class OpenGLPrimitives implements IPrimitives {
	
	private final GLTextRenderer textRenderer = new GLTextRenderer();
	
	private VAOManager manager;
	
	private int currentShader;
	
	private int basicShader;
	private int uiShader;
	private int textShader;
	
	private int rgb1;
	private int alpha1;
	private int rgb2;
	private int alpha2;
	private int pixelSize;
	private int lightCenter;
	private int lightRadius;
	private int lightStrength;
	
	private double pixelWidth;
	private double pixelHeight;
	
	private float r;
	private float g;
	private float b;
	private float a;
	
	public void init(GL4 gl) {
		try {
			int basicVert = loadShader(gl, "basic.vert", GL4.GL_VERTEX_SHADER);
			int basicFrag = loadShader(gl, "basic.frag", GL4.GL_FRAGMENT_SHADER);
			
			int guiFrag = loadShader(gl, "gui.frag", GL4.GL_FRAGMENT_SHADER);
			int guiGeom = loadShader(gl, "gui.geom", GL4.GL_GEOMETRY_SHADER);
			
			int textVert = loadShader(gl, "text.vert", GL4.GL_VERTEX_SHADER);
			int textFrag = loadShader(gl, "text.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.basicShader = gl.glCreateProgram();
			
			gl.glAttachShader(this.basicShader, basicVert);
			gl.glAttachShader(this.basicShader, basicFrag);
			
			gl.glBindFragDataLocation(this.basicShader, 0, "color");

			gl.glLinkProgram(this.basicShader);
						
			this.rgb1 = gl.glGetUniformLocation(this.basicShader, "rgb");
			this.alpha1 = gl.glGetUniformLocation(this.basicShader, "alpha");
						
			this.uiShader = gl.glCreateProgram();
									
			gl.glAttachShader(this.uiShader, basicVert);
			gl.glAttachShader(this.uiShader, guiGeom);
			gl.glAttachShader(this.uiShader, guiFrag);
			
			gl.glBindFragDataLocation(this.uiShader, 0, "color");
			
			gl.glLinkProgram(this.uiShader);

			this.rgb2 = gl.glGetUniformLocation(this.uiShader, "rgb");
			this.alpha2 = gl.glGetUniformLocation(this.uiShader, "alpha");
			this.pixelSize = gl.glGetUniformLocation(this.uiShader, "pixelSize");
			this.lightCenter = gl.glGetUniformLocation(this.uiShader, "lightCenter");
			this.lightRadius = gl.glGetUniformLocation(this.uiShader, "lightRadius");
			this.lightStrength = gl.glGetUniformLocation(this.uiShader, "lightStrength");

			IntBuffer buffer = IntBuffer.allocate(2);
			gl.glGenTextures(2, buffer);
			int texture = buffer.get(0);
			
			gl.glActiveTexture(GL4.GL_TEXTURE0);
			gl.glBindTexture(GL4.GL_TEXTURE_2D, texture);
			TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL4), this.getClass().getResourceAsStream("texture.png"), false, TextureIO.PNG);
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 32, 32, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data.getBuffer());
			
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_NEAREST);
			gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);
						
			this.manager = new VAOManager(gl, this.uiShader);

			this.textShader = gl.glCreateProgram();
			
			gl.glAttachShader(this.textShader, textVert);
			gl.glAttachShader(this.textShader, textFrag);
			
			gl.glBindFragDataLocation(this.textShader, 0, "color");
			
			gl.glLinkProgram(this.textShader);
			
			this.textRenderer.initVAO(gl, this.textShader, buffer.get(1));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void viewportChanged(int width, int height) {
		this.pixelWidth = 2.0d / width;
		this.pixelHeight = 2.0d / height;
		
		this.textRenderer.stateChanged(new Dimension(width, height));
	}
	
	public void beginUI(GL4 gl) {		
		gl.glEnable(GL4.GL_BLEND);
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		
		this.use(gl, this.uiShader);
		
		this.disableLight(gl);
	}
	
	public void endUI(GL4 gl) {
		this.use(gl, this.textShader);
		
		this.textRenderer.uploadToGPU(gl);
		
		gl.glDisable(GL4.GL_BLEND);
	}
		
	public void drawRect(AppDrawingContext context, Rectangle2D bounds) {		
		GL4 gl = this.getGL(context);
		GLCoordinates coords = new GLCoordinates(bounds);
				
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putBoundingPoints().getDataBuffer(), (nil, id) -> {
			gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(gl, this.basicShader);

		gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, 4);
	}

	public void fillRect(AppDrawingContext context, Rectangle2D bounds) {	
		GL4 gl = this.getGL(context);
		GLCoordinates coords = new GLCoordinates(bounds);
		
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putSouth().putNorth().getDataBuffer(), (nil, id) -> {
			gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(gl, this.basicShader);
		
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}

	public void drawRoundRect(AppDrawingContext context, Rectangle2D bounds) {	
		GL4 gl = this.getGL(context);
		GLCoordinates coords = new GLCoordinates(bounds);
		
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putBoundingPoints().getDataBuffer(), (nil, id) -> {
			gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(gl, this.uiShader);
		
		gl.glDrawArrays(GL4.GL_LINES_ADJACENCY, 0, 4);
	}

	public void fillRoundRect(AppDrawingContext context, Rectangle2D bounds) {
		this.fillRect(context, bounds);
	}

	// Not checked
	public void drawLine(AppDrawingContext context, double x1, double y1, double x2, double y2) {
		GL4 gl = this.getGL(context);
		GLCoordinates coords = new GLCoordinates(new Rectangle2D.Double(x1, y1, x1 + x2, y1 + y2));
				
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(4).putNW().putSE().getDataBuffer(), (nil, id) -> {
			gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(gl, this.basicShader);

		gl.glDrawArrays(GL4.GL_LINE, 0, 2);
	}

	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds) {
		return this.drawText(context, text, bounds, 0);
	}

	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds, int yCorrection) {	
		return this.textRenderer.drawString(bounds, text, context.getFontAdapter(), yCorrection);
	}
	
	public void setColor(GL4 gl, float r, float g, float b) {
		if(this.r != r || this.g != g || this.b != b) {
			this.textRenderer.setColor(r, g, b);
			
			gl.glUniform3f(this.currentShader != this.uiShader ? this.rgb1 : this.rgb2, r, g, b);
		
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
	
	public void setAlpha(GL4 gl, float alpha) {
		if(this.a != alpha) {
			this.textRenderer.setAlpha(alpha);
			
			gl.glUniform1f(this.currentShader != this.uiShader ? this.alpha1 : this.alpha2, a);
			
			this.a = alpha;
		}
	}
	
	public float getRed() {
		return this.r;
	}
	
	public float getGreen() {
		return this.g;
	}
	
	public float getBlue() {
		return this.b;
	}
	
	public float getAlpha() {
		return this.a;
	}
	
	public GLTextRenderer getTextRenderer() {
		return this.textRenderer;
	}
	
	public void enableLight(AppDrawingContext context, Point2D center, double radius, double strength) {
		GL4 gl = this.getGL(context);
		
		this.use(gl, this.uiShader);

		this.enableLight(gl, center, radius, strength);
	}
	
	public void disableLight(AppDrawingContext context) {
		GL4 gl = this.getGL(context);
		
		this.use(gl, this.uiShader);

		this.disableLight(gl);
	}
	
	private void enableLight(GL4 gl, Point2D center, double radius, double strength) { // Ensure that the UI Shader is active before calling this method
		gl.glUniform2d(this.lightCenter, center.getX() - 1.0d, 1.0d - center.getY());
		gl.glUniform1d(this.lightRadius, radius);
		gl.glUniform1d(this.lightStrength, strength);
	}
	
	private void disableLight(GL4 gl) { // Ensure that the UI Shader is active before calling this method
		gl.glUniform1d(this.lightRadius, -1.0d);
	}
	
	public void use(GL4 gl, int shader) {
		gl.glUseProgram(shader);
		
		if(shader != this.textShader) {
			gl.glUniform3f(shader != this.uiShader ? this.rgb1 : this.rgb2, this.r, this.g, this.b);
			gl.glUniform1f(shader != this.uiShader ? this.alpha1 : this.alpha2, this.a);
		}

		if(shader == this.uiShader) {
			gl.glUniform2d(this.pixelSize, this.pixelWidth, this.pixelHeight);
		}
		
		this.currentShader = shader;
	}
	
	private GL4 getGL(AppDrawingContext context) {
		assert context instanceof OpenGLDrawingContext;
		return ((OpenGLDrawingContext) context).getRenderer();
	}
	
	public static int loadShader(GL4 gl, String name, int type) throws IOException {
		InputStream input = OpenGLPrimitives.class.getResourceAsStream(name);
		
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