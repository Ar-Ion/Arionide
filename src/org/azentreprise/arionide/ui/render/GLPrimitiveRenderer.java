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
package org.azentreprise.arionide.ui.render;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.azentreprise.Debug;
import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLContext;
import org.azentreprise.arionide.ui.render.font.FontRenderer;
import org.azentreprise.arionide.ui.shaders.Shaders;
import org.azentreprise.arionide.ui.topology.Bounds;
import org.azentreprise.arionide.ui.topology.Point;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLException;

public class GLPrimitiveRenderer implements PrimitiveRenderer {
	
	private OpenGLContext context;
	private GL4 gl;
	
	private FontRenderer fontRenderer;
	
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
	
	public void init(AppDrawingContext context) {
		assert context instanceof OpenGLContext;
		this.context = (OpenGLContext) context;
		this.gl = this.context.getRenderer();
		this.fontRenderer = this.context.getFontRenderer();
		
		try {
			int basicVert = Shaders.loadShader(this.gl, "shape.vert", GL4.GL_VERTEX_SHADER);
			int basicFrag = Shaders.loadShader(this.gl, "shape.frag", GL4.GL_FRAGMENT_SHADER);
			
			int guiFrag = Shaders.loadShader(this.gl, "shape.frag", GL4.GL_FRAGMENT_SHADER);
			int guiGeom = Shaders.loadShader(this.gl, "gui.geom", GL4.GL_GEOMETRY_SHADER);
			
			this.basicShader = this.gl.glCreateProgram();
			
			this.gl.glAttachShader(this.basicShader, basicVert);
			this.gl.glAttachShader(this.basicShader, basicFrag);
			
			this.gl.glBindFragDataLocation(this.basicShader, 0, "color");

			this.gl.glLinkProgram(this.basicShader);
						
			this.rgb1 = this.gl.glGetUniformLocation(this.basicShader, "rgb");
			this.alpha1 = this.gl.glGetUniformLocation(this.basicShader, "alpha");
						
			this.uiShader = this.gl.glCreateProgram();
									
			this.gl.glAttachShader(this.uiShader, basicVert);
			this.gl.glAttachShader(this.uiShader, guiGeom);
			this.gl.glAttachShader(this.uiShader, guiFrag);
			
			this.gl.glBindFragDataLocation(this.uiShader, 0, "color");
			
			this.gl.glLinkProgram(this.uiShader);

			this.rgb2 = this.gl.glGetUniformLocation(this.uiShader, "rgb");
			this.alpha2 = this.gl.glGetUniformLocation(this.uiShader, "alpha");
			this.pixelSize = this.gl.glGetUniformLocation(this.uiShader, "pixelSize");
			this.lightCenter = this.gl.glGetUniformLocation(this.uiShader, "lightCenter");
			this.lightRadius = this.gl.glGetUniformLocation(this.uiShader, "lightRadius");
			this.lightStrength = this.gl.glGetUniformLocation(this.uiShader, "lightStrength");

			/*IntBuffer buffer = IntBuffer.allocate(1);
			this.gl.glGenTextures(1, buffer);
			
			int roundRect = buffer.get(0);
			
			this.gl.glActiveTexture(GL4.GL_TEXTURE0);
			this.gl.glBindTexture(GL4.GL_TEXTURE_2D, roundRect);
			
			InputStream stream = new FileInputStream(this.context.getResources().getResource("round-rect"));
			TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL4), stream, false, TextureIO.PNG);
			
			this.gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, 32, 32, 0, GL4.GL_RGBA, GL4.GL_UNSIGNED_BYTE, data.getBuffer());
			
			this.gl.glGenerateMipmap(GL4.GL_TEXTURE_2D);
			
			this.gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_CLAMP_TO_BORDER);
			this.gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_CLAMP_TO_BORDER);
			this.gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR_MIPMAP_NEAREST);
			this.gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_NEAREST);*/
						
			this.manager = new VAOManager(this.gl, this.uiShader);
		} catch (IOException | GLException exception) {
			Debug.exception(exception);
		}
	}
	
	public void viewportChanged(int width, int height) {
		this.pixelWidth = 2.0d / width;
		this.pixelHeight = 2.0d / height;
	}
	
	public void beginUI() {
		this.gl.glEnable(GL4.GL_BLEND);
				
		this.drawCursor();
		
		this.gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		
		this.use(this.uiShader);
		
		this.disableLight(this.gl);
	}
	
	public void endUI() {
		this.use(this.textShader);
				
		this.gl.glDisable(GL4.GL_BLEND);
	}
		
	public void drawRect(Bounds bounds) {		
		GLCoordinates coords = new GLCoordinates(bounds);
				
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putBoundingPoints().getDataBuffer(), (nil, id) -> {
			this.gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, 4);
	}

	public void fillRect(Bounds bounds) {	
		GLCoordinates coords = new GLCoordinates(bounds);
		
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putSouth().putNorth().getDataBuffer(), (nil, id) -> {
			this.gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(this.basicShader);
		
		this.gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
	}

	public void drawRoundRect(Bounds bounds) {	
		//this.drawRect(bounds);
		/*GLCoordinates coords = new GLCoordinates(bounds);
		
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(8).putBoundingPoints().getDataBuffer(), (nil, id) -> {
			this.gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(this.uiShader);
		
		this.gl.glDrawArrays(GL4.GL_LINES_ADJACENCY, 0, 4);*/
	}

	public void fillRoundRect(Bounds bounds) {
		this.fillRect(bounds);
	}

	// Not checked
	public void drawLine(float x1, float y1, float x2, float y2) {
		GLCoordinates coords = new GLCoordinates(new Bounds(x1, y1, x1 + x2, y1 + y2));
				
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(4).putNW().putSE().getDataBuffer(), (nil, id) -> {
			this.gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(this.basicShader);

		this.gl.glDrawArrays(GL4.GL_LINE, 0, 2);
	}
	
	public void drawCursor() {		
		GLCoordinates coords = new GLCoordinates(new Bounds(1.0f, 1.0f, 1.0f, 1.0f));
				
		this.manager.loadVAO(coords.getUUID(), () -> coords.allocDataBuffer(2).putX1().getDataBuffer(), (nil, id) -> {
			this.gl.glVertexAttribPointer(id, 2, GL4.GL_DOUBLE, false, 0, 0);
		}, "position");

		this.use(this.basicShader);
		
		this.setColor(this.gl, 1.0f, 1.0f, 1.0f);
		
		this.gl.glBlendFunc(GL4.GL_ONE_MINUS_DST_COLOR, GL4.GL_ONE_MINUS_SRC_COLOR);

		this.gl.glPointSize(3.0f);
		this.gl.glDrawArrays(GL4.GL_POINTS, 0, 1);
		this.gl.glPointSize(1.0f);
	}


	public Point drawText(String text, Bounds bounds) {
		return this.fontRenderer.renderString(this.gl, text, bounds);
	}
	
	public void setColor(GL4 gl, float r, float g, float b) {
		if(this.r != r || this.g != g || this.b != b) {
			
			gl.glUniform3f(this.currentShader != this.uiShader ? this.rgb1 : this.rgb2, r, g, b);
		
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
	
	public void setAlpha(GL4 gl, float alpha) {
		if(this.a != alpha) {
			
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

	public void enableLight(Point2D center, double radius, double strength) {		
		this.use(this.uiShader);
		this.gl.glUniform2d(this.lightCenter, center.getX() - 1.0d, 1.0d - center.getY());
		this.gl.glUniform1d(this.lightRadius, radius);
		this.gl.glUniform1d(this.lightStrength, strength);
	}
	
	public void disableLight(AppDrawingContext context) {		
		this.use(this.uiShader);
		this.disableLight(this.gl);
	}
	
	private void disableLight(GL4 gl) { // Ensure that the UI Shader is active before calling this method
		gl.glUniform1d(this.lightRadius, -1.0d);
	}
	
	public void use(int shader) {
		this.gl.glUseProgram(shader);
		
		if(shader != this.textShader) {
			this.gl.glUniform3f(shader != this.uiShader ? this.rgb1 : this.rgb2, this.r, this.g, this.b);
			this.gl.glUniform1f(shader != this.uiShader ? this.alpha1 : this.alpha2, this.a);
		}

		if(shader == this.uiShader) {
			this.gl.glUniform2d(this.pixelSize, this.pixelWidth, this.pixelHeight);
		}
		
		this.currentShader = shader;
	}
	
	public GL4 getGL() {
		return this.gl;
	}
}