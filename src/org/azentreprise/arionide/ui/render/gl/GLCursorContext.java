package org.azentreprise.arionide.ui.render.gl;

import java.io.IOException;
import java.math.BigInteger;

import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.ui.render.Identification;
import org.azentreprise.arionide.ui.shaders.Shaders;
import org.azentreprise.arionide.ui.shaders.preprocessor.DummySettings;

import com.jogamp.opengl.GL4;

public class GLCursorContext extends GLRenderingContext {
	
	public static final int SIZE_IDENTIFIER = 0;
	public static final int PREPARE_ACTION_IDENTIFIER = 0x1;

	private static final BigInteger[] scheme = Identification.makeScheme(1);
	
	private int shader;	
	private int position;

	protected GLCursorContext(GL4 gl) {
		super(gl);
	}
	
	public void load() {
		GL4 gl = this.getGL();
		
		try {
			int vert = Shaders.loadShader(gl, "cursor.vert", DummySettings.VERTEX);
			int frag = Shaders.loadShader(gl, "cursor.frag", DummySettings.FRAGMENT);
			
			this.shader = gl.glCreateProgram();
			
			gl.glAttachShader(this.shader, vert);
			gl.glAttachShader(this.shader, frag);
			
			gl.glBindFragDataLocation(this.shader, 0, "color");
			
			gl.glLinkProgram(this.shader);
			
			this.position = gl.glGetAttribLocation(this.shader, "position");
		} catch (IOException exception) {
			Debug.exception(exception);
		}
	}
	
	public void enter() {
		GL4 gl = this.getGL();
		
		gl.glUseProgram(this.shader);
		gl.glBlendFunc(GL4.GL_ONE_MINUS_DST_COLOR, GL4.GL_ZERO);
	}

	public void exit() {
		this.getGL().glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void onAspectRatioUpdate(float newRatio) {
		return;
	}

	public BigInteger[] getIdentificationScheme() {
		return scheme;
	}
	
	public int getShaderID() {
		return this.shader;
	}
	
	public int getPositionAttribute() {
		return this.position;
	}
}
