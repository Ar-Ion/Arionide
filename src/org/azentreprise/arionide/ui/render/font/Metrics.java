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
package org.azentreprise.arionide.ui.render.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Metrics {
	
	private static final CharMeta dummyMeta = new CharMeta(-1, 0, 0, 0, 0, 0, 0, 0, 0);
	
	private final Map<Character, CharMeta> meta = new HashMap<>();
	private final int base;
	private final int lineHeight;
	private final int textureSize;
	
	public Metrics(InputStream metaStream) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Document doc = builder.parse(new InputSource(metaStream));
		
		Element root = doc.getDocumentElement();
		Element common = (Element) root.getElementsByTagName("common").item(0);
		this.base = Integer.parseInt(common.getAttribute("base"));
		this.lineHeight = Integer.parseInt(common.getAttribute("lineHeight"));
		this.textureSize = Integer.parseInt(common.getAttribute("scaleW"));
		
		assert this.textureSize == Integer.parseInt(common.getAttribute("scaleH"));
		
		Element chars = (Element) root.getElementsByTagName("chars").item(0);
		NodeList array = chars.getElementsByTagName("char");
		int count = array.getLength();
		
		for(int i = 0; i < count; i++) {
			Element element = (Element) array.item(i);
			
			int id = Integer.parseInt(element.getAttribute("id"));
			int x = Integer.parseInt(element.getAttribute("x"));
			int y = Integer.parseInt(element.getAttribute("y"));
			int width = Integer.parseInt(element.getAttribute("width"));
			int height = Integer.parseInt(element.getAttribute("height"));
			int xOffset = Integer.parseInt(element.getAttribute("xoffset"));
			int yOffset = Integer.parseInt(element.getAttribute("yoffset"));
			int advance = Integer.parseInt(element.getAttribute("xadvance"));
			
			this.meta.put((char) id, new CharMeta(id, this.base, x, y, width, height, xOffset, yOffset, advance));
		}
	}
	
	public int getBase() {
		return this.base;
	}
	
	public int getLineHeight() {
		return this.lineHeight;
	}
	
	public int getTextureSize() {
		return this.textureSize;
	}
	
	public boolean isCharValid(char ch) {
		return this.meta.containsKey(ch);
	}
	
	public CharMeta fetchMeta(int ch) {
		CharMeta meta = this.meta.get((char) (ch & 0xFFFF));
		return meta != null ? meta : dummyMeta;
	}
	
	public CharMeta fetchMeta(char ch) {
		CharMeta meta = this.meta.get(ch);
		return meta != null ? meta : dummyMeta;
	}
}