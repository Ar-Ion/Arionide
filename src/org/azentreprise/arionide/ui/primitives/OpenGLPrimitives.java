/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.arionide.ui.primitives;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.ui.AppDrawingContext;
import org.azentreprise.arionide.ui.OpenGLDrawingContext;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class OpenGLPrimitives implements IPrimitives {
	
	/* These constants are used for round rectangle rendering. */
	private final int radius = 25;
	private final int radiusSq = this.radius * this.radius;
	private final int quality = 8;
	private final int alloc = this.quality * 8;
	private final int halfAlloc = this.quality * 4;
	private final double[] vertices = new double[this.alloc];
	
	/* Relative radius dimensions */
	private double relWidth = 0.0d;
	private double relHeight = 0.0d;

	private Map<Rectangle2D, CacheElement> cache = new HashMap<>();
	
	private int uiShader = -1;
	private int shaderRGB = -1;
	private int shaderAlpha = -1;
	
	public void init(GL4 gl) {
		try {
			int vert = this.loadShader(gl, "gui_shader.vert", GL4.GL_VERTEX_SHADER);
			int frag = this.loadShader(gl, "gui_shader.frag", GL4.GL_FRAGMENT_SHADER);
			
			this.uiShader = gl.glCreateProgram();
			
			gl.glAttachShader(this.uiShader, vert);
			gl.glAttachShader(this.uiShader, frag);
			
			gl.glBindFragDataLocation(this.uiShader, 0, "color");
			
			gl.glLinkProgram(this.uiShader);
			
			this.shaderRGB = gl.glGetUniformLocation(this.uiShader, "rgb");
			this.shaderAlpha = gl.glGetUniformLocation(this.uiShader, "alpha");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void viewportChanged(int width, int height) {
		for(int i = 0; i < this.quality; i++) {
			double x = (double) i * this.radius / (this.quality - 1);
			double y = Math.sqrt(this.radiusSq - x*x);
			
			x /= width;
			y /= height;
			
			int di = 2*i;
			int diinc = di + 1;
			
			this.vertices[di] = x;
			this.vertices[diinc] = y;
			
			this.vertices[this.halfAlloc - di - 2] = x;
			this.vertices[this.halfAlloc - di - 1] = -y;
			
			this.vertices[this.halfAlloc + di] = -x;
			this.vertices[this.halfAlloc + diinc] = -y;
			
			this.vertices[this.alloc - di - 2] = -x;
			this.vertices[this.alloc - di - 1] = y;
		}
		
		this.relWidth = (double) this.radius / width;
		this.relHeight = (double) this.radius / height;
	}
	
	public void beginUI(GL4 gl) {
		gl.glUseProgram(this.uiShader);
	}
	
	public void endUI(GL4 gl) {
		gl.glUseProgram(0);
	}
	
	/* WARNING: Using the OpenGL implementation, the Rectangle2D bounds is actually a data structure representing the data (x1, y1, x2, y2). */
	
	public void drawRect(AppDrawingContext context, Rectangle2D bounds) {
		GL4 gl = this.getGL(context);
		CacheElement element = this.getCacheElement(bounds);
		
		if(element.vbo.get(0) == 0) {	
			DoubleBuffer buffer = DoubleBuffer.allocate(8);

			buffer.put(0, bounds.getX() - 1.0d);
			buffer.put(1, -bounds.getY() + 1.0d);
			
			buffer.put(2, bounds.getX() - 1.0d);
			buffer.put(3, -bounds.getHeight() - bounds.getY() + 1.0d);
			
			buffer.put(4, bounds.getWidth() + bounds.getX() - 1.0d);
			buffer.put(5, -bounds.getHeight() - bounds.getY() + 1.0d);
			
			buffer.put(6, bounds.getWidth() + bounds.getX() - 1.0d);
			buffer.put(7, -bounds.getY() + 1.0d);
			
			gl.glGenVertexArrays(1, element.vao);
			gl.glBindVertexArray(element.vao.get(0));
			
			gl.glGenBuffers(1, element.vbo);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, element.vbo.get(0));
			gl.glBufferData(GL4.GL_ARRAY_BUFFER, buffer.capacity() * Double.BYTES, buffer, GL4.GL_STATIC_DRAW);
			
			int attribute = gl.glGetAttribLocation(this.uiShader, "position");
			gl.glEnableVertexAttribArray(attribute);
			gl.glVertexAttribPointer(attribute, 2, GL4.GL_DOUBLE, false, 0, 0);
		} else {
			gl.glBindVertexArray(element.vao.get(0));
		}

		gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, 4);
	}

	@Override
	public void fillRect(AppDrawingContext context, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawRoundRect(AppDrawingContext context, Rectangle2D bounds) {		
		GL4 gl = this.getGL(context);
		CacheElement element = this.getCacheElement(bounds);

		if(element.vbo.get(0) == 0) {	
			DoubleBuffer buffer = DoubleBuffer.allocate(this.vertices.length);
			
			double translateX = bounds.getX() + bounds.getWidth() - 1.0d;
			double translateY = 1.0d - bounds.getY() - this.relHeight;
			double width = bounds.getWidth() - 2 * this.relWidth;
			double height = bounds.getHeight() - 2 * this.relHeight;
			
			for(int i = 0; i < this.halfAlloc; i++) {
				if(i == this.quality) {
					translateY -= height;
				} else if(i == 2 * this.quality) {
					translateX -= width;
				} else if(i == 3 * this.quality) {
					translateY += height;
				}
				
				buffer.put(2*i, this.vertices[2*i] + translateX);
				buffer.put(2*i + 1, this.vertices[2*i + 1] + translateY);
			}
			
			gl.glGenVertexArrays(1, element.vao);
			gl.glBindVertexArray(element.vao.get(0));
			
			gl.glGenBuffers(1, element.vbo);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, element.vbo.get(0));
			gl.glBufferData(GL4.GL_ARRAY_BUFFER, buffer.capacity() * Double.BYTES, buffer, GL4.GL_STATIC_DRAW);
			
			int attribute = gl.glGetAttribLocation(this.uiShader, "position");
			gl.glEnableVertexAttribArray(attribute);
			gl.glVertexAttribPointer(attribute, 2, GL4.GL_DOUBLE, false, 0, 0);
		} else {
			gl.glBindVertexArray(element.vao.get(0));
		}		
		
		gl.glDrawArrays(GL4.GL_LINE_LOOP, 0, this.halfAlloc);
	}

	@Override
	public void fillRoundRect(AppDrawingContext context, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawLine(AppDrawingContext context, double x1, double y1, double x2, double y2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D drawText(AppDrawingContext context, String text, Rectangle2D bounds, int yCorrection) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setColor(GL4 gl, float r, float g, float b) {
		gl.glUniform3f(this.shaderRGB, r, g, b);
	}
	
	public void setAlpha(GL4 gl, float alpha) {
		gl.glUniform1f(this.shaderAlpha, alpha);
	}
	
	private GL4 getGL(AppDrawingContext context) {
		assert context instanceof OpenGLDrawingContext;
		return ((OpenGLDrawingContext) context).getRenderer();
	}
	
	private CacheElement getCacheElement(Rectangle2D bounds) {
		CacheElement element = this.cache.get(bounds);
		
		if(element != null) {
			element.lastUpdate = System.currentTimeMillis();
		} else {
			element = new CacheElement(IntBuffer.allocate(1), IntBuffer.allocate(1), System.currentTimeMillis());
			this.cache.put(bounds, element);
		}
		
		return element;
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
	
	/* Needs a GC System */
	private static class CacheElement  {
		
		private IntBuffer vbo;
		private IntBuffer vao;
		private long lastUpdate;
		
		private CacheElement(IntBuffer vbo, IntBuffer vao, long lastUpdate) {
			this.vbo = vbo;
			this.vao = vao;
			this.lastUpdate = lastUpdate;
		}
	}
}