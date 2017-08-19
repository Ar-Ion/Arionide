package org.azentreprise.arionide.ui.primitives;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.FontAdapter;

import com.jogamp.opengl.GL4;

// I agree: using AWT for text rendering and GL for what's left is bullshit.
public class GLTextRenderer {
	
	private static final int BUFFER_SIZE = 1024;
	
	private BufferedImage buffer;
	private IntBuffer textureBuffer;
	private Graphics2D g2d;
	private Dimension viewport;
	
	private int rgb;
	private int alpha;
	private Paint paint;
	
	private boolean preDirty = false;
	private boolean dirty = false;
	
	private int samplerUniform;
	private int vaoID;
	
	private List<Long> hashes = new ArrayList<>();
	private int iterationIndex = 0;
	
	protected GLTextRenderer() {
		this.buffer = new BufferedImage(BUFFER_SIZE, BUFFER_SIZE, BufferedImage.TYPE_INT_ARGB);
		int[] data = ((DataBufferInt) this.buffer.getRaster().getDataBuffer()).getData();
		this.g2d = this.buffer.createGraphics();
		this.g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
		this.textureBuffer = IntBuffer.wrap(data);
	}
	
	protected void stateChanged(Dimension viewport) {
		this.viewport = viewport;
		
		AffineTransform matrix = this.g2d.getTransform();
		matrix.setToIdentity();
		matrix.scale(BUFFER_SIZE / viewport.getWidth(), BUFFER_SIZE / viewport.getHeight());
		this.g2d.setTransform(matrix);
	}
	
	protected void initVAO(GL4 gl, int shader, int textureID) {
		gl.glActiveTexture(GL4.GL_TEXTURE1);
		gl.glBindTexture(GL4.GL_TEXTURE_2D, textureID);
				
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, GL4.GL_REPEAT);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);
		
		this.samplerUniform = gl.glGetUniformLocation(shader, "textSampler");
				
		GLCoordinates coords = new GLCoordinates(new Rectangle2D.Double(0.0d, 0.0d, 2.0d, 2.0d));
		DoubleBuffer data = coords.allocDataBuffer(8).putNorth().putSouth().getDataBuffer();
				
		IntBuffer vao = IntBuffer.allocate(1);
		IntBuffer vbo = IntBuffer.allocate(1);
				
		gl.glGenVertexArrays(1, vao);
		
		this.vaoID = vao.get(0);
		
		gl.glBindVertexArray(this.vaoID);
		
		gl.glGenBuffers(1, vbo);
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.get(0));
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, data.capacity() * Double.BYTES, data, GL4.GL_STATIC_DRAW);
		
		int position = gl.glGetAttribLocation(shader, "position");
		gl.glEnableVertexAttribArray(position);
		gl.glVertexAttribPointer(position, 2, GL4.GL_DOUBLE, false, 0, 0);
	}
	
	public Dimension getViewport() {
		return this.viewport;
	}
	
	public Color getCurrentColor() {
		return this.g2d.getColor();
	}
	
	public Paint setPaint(Paint paint) {
		Paint buffer = this.paint;
		this.paint = paint;
		return buffer;
	}
	
	public void setColor(float r, float g, float b) {
		this.rgb = Utils.packRGB((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f));
	}
	
	public void setAlpha(float alpha) {
		this.alpha = (int) (alpha * 255.0f);
	}
	
	protected Point2D drawString(Rectangle2D bounds, String string, FontAdapter adapter, int yCorrection) {
		Rectangle outerBounds = new GLCoordinates(bounds).getAWTBoundings(this.viewport);

		Font font = adapter.adapt(string, outerBounds.getWidth(), outerBounds.getHeight());

		if(this.g2d.getFont() != font) {
			this.g2d.setFont(font);
		}
				
		FontMetrics metrics = this.g2d.getFontMetrics();
		
		int x = outerBounds.x + (outerBounds.width - metrics.stringWidth(string)) / 2;
		int y = outerBounds.y + (outerBounds.height - metrics.getMaxDescent() + metrics.getMaxAscent() + 1 + yCorrection) / 2;
		
		int color = Utils.packARGB(this.rgb, this.alpha);
		long packed = 17 * string.hashCode() + 31 * (((x & 0xFFFFL) << 48) | ((y & 0xFFFFL) << 32) | color) + 47 * (this.paint != null ? this.paint.hashCode() : 0);
		
		if(this.iterationIndex == this.hashes.size()) {
			this.preDirty = true;
			this.hashes.add(packed); // Auto cast
		} 
		
		if(this.hashes.get(this.iterationIndex++) != packed) {
			this.preDirty = true;
			this.hashes.set(this.iterationIndex - 1, packed);
		}
		
		if(this.dirty) {
			this.g2d.setColor(new Color(color, true));
			this.g2d.setPaint(this.paint);
			this.g2d.drawString(string, x, y);
		}
		
		double glX = 2.0d * x / this.viewport.getWidth() - 1.0d;
		double glY = 2.0d * y / this.viewport.getHeight() - 1.0d;

		return new Point2D.Double(glX, glY);
	}
	
	protected void uploadToGPU(GL4 gl) {
		if(this.dirty) {
			gl.glActiveTexture(GL4.GL_TEXTURE1);
			gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGBA, BUFFER_SIZE, BUFFER_SIZE, 0, GL4.GL_BGRA, GL4.GL_UNSIGNED_INT_8_8_8_8_REV, this.textureBuffer);
			this.dirty = false;
		}
		
		if(this.preDirty) {
			Arrays.fill(this.textureBuffer.array(), 0);

			this.dirty = true;
			this.preDirty = false;
		}

		gl.glUniform1i(this.samplerUniform, 1);
		gl.glBindVertexArray(this.vaoID);
		gl.glDrawArrays(GL4.GL_TRIANGLE_STRIP, 0, 4);
		
		this.iterationIndex = 0;
	}
}