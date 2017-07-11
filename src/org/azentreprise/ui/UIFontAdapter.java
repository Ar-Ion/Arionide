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
package org.azentreprise.ui;

import java.awt.Font;
import java.awt.FontMetrics;

public class UIFontAdapter {
	
	private static final int cacheStep = 2;
	private static final int widthCorrection = 4;
	
	private static Font[] cache = new Font[32];
	private static String hash = new String();
	
	private static boolean setup = false;
	private static FontMetrics metrics = null;
	private static Font font = null;
	private static float fontSize = 0.0f;
	private static int fontHeight = 0;
	
	public static boolean needSetup() {
		return !setup;
	}
	
	public static void setup(FontMetrics fm) {
		setup = true;
		metrics = fm;
		font = metrics.getFont();
		fontHeight = metrics.getHeight() + metrics.getAscent();
		fontSize = font.getSize2D();
		
		for(int i = 0; i < cache.length; i++) {
			cache[i] = font.deriveFont(i * cacheStep + 1.0f);
		}
	}
	
	public static Font adapt(String str, int width, int height, float relativeSize) {
		String localHash = "hash@" + width + height + relativeSize;
		
		if(localHash != hash) {
			float fontSizeX = (float) width * relativeSize / metrics.stringWidth(str) / widthCorrection;
			float fontSizeY = (float) height * relativeSize / fontHeight;
			int size = (int) (fontSize * Math.min(fontSizeX, fontSizeY) / cacheStep);
			
			if(size < 0) {
				size = 0;
			} else if(size >= cache.length) {
				size = cache.length - 1;
			}
			
			hash = localHash;
			
			return cache[size];
		} else {
			return null;
		}
	}
}
