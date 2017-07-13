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
package org.azentreprise;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.azentreprise.configuration.Configuration;

public class ProjectFormatConverter {
	public static void importProject(File file) {
		Debug.taskBegin("Importing a project");

		try {
			GZIPInputStream input = new GZIPInputStream(new FileInputStream(file));
			int prefix = 1;
			File output;
			
			do {
				String fileName = file.getName();
				fileName = fileName.substring(0, fileName.length() - 9);
				if(prefix > 1) {
					fileName += " (" + prefix + ")";
				}
				fileName += ".proj";
				output = new File(Arionide.getRoot(), "workspace" + File.separatorChar + fileName);
				prefix++;
			} while(output.exists());
		
			Files.copy(input, output.toPath());
			input.close();
			
			Projects.getWorkspaceConfiguration().projects.add(output.getName().replace(".proj", ""));
			Projects.getWorkspaceConfiguration().save();
		} catch (IOException | IllegalArgumentException | IllegalAccessException exception) {
			Debug.exception(exception);
		}
		
		Debug.taskEnd();
	}
	
	public static void exportProject(File file) {
		Debug.taskBegin("Exporting a project");
		
		if(!file.getName().endsWith(".Arionide")) {
			file = new File(file.getParentFile(), file.getName().concat(".Arionide"));
		}

		try {
			Field field = Configuration.class.getDeclaredField("writer");
			field.setAccessible(true);
			field.set(Projects.getCurrentProject(), new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)))));
			Projects.getCurrentProject().save(false);
		} catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			Debug.exception(exception);
		}
		
		Debug.taskEnd();
	}
}
