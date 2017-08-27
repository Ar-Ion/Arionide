package org.azentreprise.arionide.ui.shaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.charset.Charset;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class Shaders {
	public static int loadShader(GL4 gl, String name, int type) throws IOException {
		InputStream input = Shaders.class.getResourceAsStream(name);
		
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
