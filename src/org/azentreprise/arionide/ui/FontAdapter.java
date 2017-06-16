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
package org.azentreprise.arionide.ui;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

public class FontAdapter {
	
	// please consider using powers of two for these variables
	private static final int MAX_FONT_SIZE = 64;
	private static final int STEP = 2;
	
	// faster than (int) (Math.log(MAX_FONT_SIZE / STEP) / Math.log(2));
	private static final int COMPLEXITY = 31 - Integer.numberOfLeadingZeros(MAX_FONT_SIZE / STEP);

	// approximation of the width ratio
	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	/* 
	 * The cache info holds information about width per char and height per char.
	 * By the way I know that height per char isn't relevant but it is for the symmetry of the code. (make it beautiful)
	 * The point is that these two ratios are strictly monotonically increasing.
	 * The distribution should be approximately linear.
	 */
	private final List<CacheInfo> cache = new ArrayList<>();
	
	private FontMetrics lastMetrics;
	
	public FontAdapter(Font font) {
		Canvas fakeCanvas = new Canvas(); 

		for(int i = 1; i < MAX_FONT_SIZE; i += STEP) {
			font = font.deriveFont((float) i);
			
			FontMetrics metrics = fakeCanvas.getFontMetrics(font);
			
			float width = metrics.stringWidth(CHARS);
			float height = metrics.getLeading() + metrics.getAscent() + metrics.getDescent();
					
			this.cache.add(new CacheInfo(font, metrics, width / CHARS.length(), height / CHARS.length()));
		}
	}
	
	// linear interpolation
	public Font adapt(String str, int width, int height, float relativeSize) {
		float iw = width * relativeSize / str.length();
		float ih = height * relativeSize / str.length();
		
		CacheInfo info = this.makeInterpolation(iw, ih);
		
		this.lastMetrics = info.getFontMetrics();
		
		return info.getFont();
	}
	
	// dichotomy algorithm
	private CacheInfo makeInterpolation(float iw, float ih) {
		
		int current = MAX_FONT_SIZE / STEP / 2;
		CacheInfo sample = null;
		
		for(int i = 0; i < COMPLEXITY; i++) {
			sample = this.cache.get(current);
			
			if(sample.getWidthRatio() >= iw || sample.getHeightRatio() >= ih) {
				current -= Integer.highestOneBit(i);
			} else {
				current += Integer.highestOneBit(i);
			}
		}
		
		return sample;
	}
	
	public FontMetrics getLastMetrics() {
		return this.lastMetrics;
	}
	
	private static final class CacheInfo {
		private final Font font;
		private final FontMetrics metrics;
		private final float widthRatio;
		private final float heightRatio;
		
		private CacheInfo(Font font, FontMetrics metrics, float widthRatio, float heightRatio) {
			this.font = font;
			this.metrics = metrics;
			this.widthRatio = widthRatio;
			this.heightRatio = heightRatio;
		}
		
		private Font getFont() {
			return this.font;
		}
		
		private FontMetrics getFontMetrics() {
			return this.metrics;
		}
		
		private float getWidthRatio() {
			return this.widthRatio;
		}
		
		private float getHeightRatio() {
			return this.heightRatio;
		}
	}
}
