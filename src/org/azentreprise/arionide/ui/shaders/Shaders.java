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
package org.azentreprise.arionide.ui.shaders;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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
		
		if(input == null) {
			throw new FileNotFoundException("Couldn't retrieve shader " + name);
		}
		
		while((count = input.read(buffer)) != -1) {
			baos.write(buffer, 0, count);
		}
		
		String code = new String(baos.toByteArray(), Charset.forName("utf8"));

		IntBuffer shaderID = IntBuffer.allocate(1);
		
		System.out.println("Compiling shader " + name + "...");
		ShaderUtil.createAndCompileShader(gl, shaderID, type, new String[][] {{ code }}, System.err);
		
		return shaderID.get(0);
	}
}
