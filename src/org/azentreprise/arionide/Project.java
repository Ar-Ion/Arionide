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
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'Arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.arionide;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.arionide.coders.Coder;
import org.azentreprise.arionide.coders.Decoder;
import org.azentreprise.arionide.coders.Encoder;
import org.azentreprise.arionide.debugging.Debug;
import org.azentreprise.arionide.debugging.IAm;

public class Project implements IProject {

	private final File path;
	private final Map<String, byte[]> properties = new HashMap<>();
	
	protected Project(File path) {
		this.path = path;
		this.load();
	}
	
	@IAm("loading a project")
	private void load() {
		try {
			byte[] data = Files.readAllBytes(this.path.toPath());
			
			int startIndex = 0;
			int endIndex = 0;
			
			while((endIndex = Arrays.binarySearch(data, startIndex, data.length, Coder.separator)) > -1) {
				byte[] keyBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, keyBuffer, 0, keyBuffer.length);
				
				startIndex = Arrays.binarySearch(data, endIndex + 1, data.length, Coder.sectionStart);
				endIndex = Arrays.binarySearch(data, startIndex, data.length, Coder.sectionEnd);
				byte[] valueBuffer = new byte[endIndex - startIndex];
				System.arraycopy(data, startIndex, valueBuffer, 0, valueBuffer.length);
				
				this.properties.put(new String(keyBuffer, Charset.forName("utf8")).replaceAll(Coder.whitespaceRegex, ""), valueBuffer);
				
				startIndex = endIndex;
			}
			
		} catch (Exception exception) {
			Debug.exception(exception);
		}
	}
	
	public String getName() {
		return null;
	}

	public File getPath() {
		return this.path;
	}

	public <T> T getProperty(String key, Decoder<T> decoder) {
		return decoder.decode(this.properties.get(key));
	}

	public <T> void setProperty(String key, T value, Encoder<T> encoder) {
		this.properties.put(key, encoder.encode(value));
		this.save();
	}

	public void save() {
		
	}

	public boolean checkVersionCompatibility() {
		return true;
	}
}