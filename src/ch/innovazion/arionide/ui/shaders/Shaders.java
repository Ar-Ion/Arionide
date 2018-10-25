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
package ch.innovazion.arionide.ui.shaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.ui.shaders.preprocessor.PreprocessorException;
import ch.innovazion.arionide.ui.shaders.preprocessor.ShaderPreprocessor;
import ch.innovazion.arionide.ui.shaders.preprocessor.ShaderSettings;

public class Shaders {
	public static int loadShader(GL4 gl, String name, ShaderSettings settings) throws IOException {
		InputStream input = Shaders.class.getResourceAsStream(name);
		ShaderPreprocessor preprocessor = new ShaderPreprocessor(settings);
		String code = Utils.read(input);

		IntBuffer shaderID = IntBuffer.allocate(1);
		
		System.out.println("Compiling shader " + name + "...");
		
		String[] lines = code.split(System.lineSeparator());
		code = new String();
		
		int lineID = 1;
		for(String line : lines) {
			if(line.startsWith("@")) {
				int openingBracket = line.indexOf('(');
				int closingBracket = line.lastIndexOf(')');
				
				if(openingBracket + closingBracket < -1) { // Void commands
					code += preprocessor.processCommand(line.substring(1), new String[0]);
				} else if(openingBracket > -1 && openingBracket < closingBracket) { // Non-void commands
					String command = line.substring(1, openingBracket);
					String[] args = line.substring(openingBracket, closingBracket).split(",");
				
					code += preprocessor.processCommand(command, args);
				} else {
					throw new PreprocessorException("Syntax error at line " + lineID);
				}
			} else {
				code += line;
			}
			
			lineID += 1;
			code += System.lineSeparator();
		}
		
		ShaderUtil.createAndCompileShader(gl, shaderID, settings.getType(), new String[][] {{ code }}, System.err);
		
		return shaderID.get(0);
	}
}
