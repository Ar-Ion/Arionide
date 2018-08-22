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

import org.azentreprise.arionide.ui.topology.Affine;
import org.azentreprise.arionide.ui.topology.Bounds;

import com.jogamp.opengl.GL4;

public interface FontRenderer {
	
	public static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!№;%:?*()_+-=.,/|\\\"'@#$^&{}[]°§<>≤≥";
	
	public static final float BBOX_FIT_X = 0.9f;
	public static final float BBOX_FIT_Y = 0.5f;
	public static final int MAX_CHARS = 256;
	
	public void windowRatioChanged(float newRatio);
	public TextCacheEntry getCacheEntry(String str);
	public Affine renderString(GL4 gl, String str, Bounds bounds);
	public TextTessellator getTessellator();
}