package org.azentreprise.arionide;

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