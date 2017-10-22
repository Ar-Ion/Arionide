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
package org.azentreprise.arionide;

import java.util.function.Function;

public class Utils {
	public static double fakeComplexPower(double x, double power) {
		return Math.signum(x) * Math.pow(Math.abs(x), power);
	}
	
	
	public static int fakeDivision(int dividend, int divisor, int infinity) {
		if(divisor != 0) {
			return dividend / divisor;
		} else {
			if(dividend == 0) {
				return 1;
			} else {
				return infinity;
			}
		}
	}
	
	public static int search(byte[] data, int start, int length, byte element) {
		while(start < length) {
			if(data[start] == element) {
				return start;
			}
			
			start++;
		}
		
		return -1;
	}
	
	public static int getAlpha(int argb) {
		return (argb >>> 24) & 0xFF;
	}
	
	public static int getRed(int argb) {
		return (argb >>> 16) & 0xFF;
	}
	
	public static int getGreen(int argb) {
		return (argb >>> 8) & 0xFF;
	}
	
	public static int getBlue(int argb) {
		return argb & 0xFF;
	}
	
	public static int packRGB(int red, int green, int blue) {
		checkColorRange("Red", red);
		checkColorRange("Green", green);
		checkColorRange("Blue", blue);
		
		return (red << 16) | (green << 8) | blue;
	}
	
	public static int packARGB(int red, int green, int blue, int alpha) {
		return packARGB(packRGB(red, green, blue), alpha);
	}
	
	public static int packARGB(int rgb, int alpha) {
		checkColorRange("Alpha", alpha);
		
		return (alpha << 24) | rgb;
	}
	
	public static void checkColorRange(String colorName, int color) {		
		if(checkUnsignedByteOverflow(color)) {
			throw new IllegalArgumentException(colorName + " out of range: " + color);
		}
	}
	
	public static boolean checkUnsignedByteOverflow(int value) {
		return value < 0 || value > 255;
	}
}