/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.ui.render.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import ch.innovazion.arionide.resources.Resources;

public class FontResources {
	
	private final Metrics metrics;
	private final TextureData fontData;
	
	public FontResources(Resources resources) throws GLException, IOException, ParserConfigurationException, SAXException {
		File meta = resources.getResource("font-meta");
		File bitmap = resources.getResource("font-bitmap");
		
		InputStream metaStream = new FileInputStream(meta);
		InputStream bitmapStream = new FileInputStream(bitmap);
		
		this.metrics = new Metrics(metaStream);
		this.fontData = TextureIO.newTextureData(GLProfile.get(GLProfile.GL4), bitmapStream, false, TextureIO.PNG);
	}
	
	public Metrics getMetrics() {
		return this.metrics;
	}
	
	public TextureData getFontData() {
		return this.fontData;
	}
}