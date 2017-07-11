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

import java.util.concurrent.ThreadLocalRandom;

import org.azentreprise.configuration.Parseable;

public class Style extends Parseable {
	private final String name;
	private final String parent;
	private final ColorRange colorRange;
	private final WaveControl waveControl;
	private final MotionControl motionControl;
	private final double alphaConstant;
	
	public Style(String serialized) {
		super(serialized);
		
		String[] data = serialized.split(":");
		
		String[] name = this.parseField(data[1]);
		String[] parent = this.parseField(data[2]);
		String[] red = this.parseField(data[3]);
		String[] green = this.parseField(data[4]);
		String[] blue = this.parseField(data[5]);
		String[] waveControl = this.parseField(data[6]);
		String[] linearMotionMatrix = this.parseField(data[7]);
		String[] rotationMatrix = this.parseField(data[8]);
		String[] alphaConstant = this.parseField(data[9]);

		this.name = name[0];
		this.parent = parent[0];
		this.colorRange = new ColorRange(Integer.parseInt(red[0]), Integer.parseInt(red[1]), 
										 Integer.parseInt(green[0]), Integer.parseInt(green[1]),
										 Integer.parseInt(blue[0]), Integer.parseInt(blue[1]));
		this.waveControl = new WaveControl(Integer.parseInt(waveControl[0]), Integer.parseInt(waveControl[1]));
		
		double[] doubleLMM = new double[linearMotionMatrix.length];
		double[] doubleRM = new double[rotationMatrix.length];

		for(int i = 0; i < linearMotionMatrix.length; i++) {
			if(!linearMotionMatrix[i].isEmpty()) {
				doubleLMM[i] = Double.parseDouble(linearMotionMatrix[i]);
			}
		}
		
		for(int i = 0; i < rotationMatrix.length; i++) {
			if(!rotationMatrix[i].isEmpty()) {
				doubleRM[i] = Double.parseDouble(rotationMatrix[i]);
			}
		}
		
		this.motionControl = new MotionControl(doubleLMM, doubleRM);
		this.alphaConstant = Double.parseDouble(alphaConstant[0]);
	}
	
	public Style(String name, String parent, ColorRange colorRange, WaveControl waveControl, MotionControl motionControl, double alphaConstant) {
		super(null);
		
		this.name = name;
		this.parent = parent;
		this.colorRange = colorRange;
		this.waveControl = waveControl;
		this.motionControl = motionControl;
		this.alphaConstant = alphaConstant;
	}
	
	private String[] parseField(String field) {
		return field.substring(field.indexOf('<') + 1, field.indexOf('>')).split(" ");
	}
	
	protected String serialize() {
		String serialized = new String();
		serialized += this.serializeField("Name", this.name) + " ";
		serialized += this.serializeField("Parent", this.parent) + " ";
		serialized += this.serializeField("Red range", this.colorRange.r, this.colorRange.R) + " ";
		serialized += this.serializeField("Green range", this.colorRange.g, this.colorRange.G) + " ";
		serialized += this.serializeField("Blue range", this.colorRange.b, this.colorRange.B) + " ";
		serialized += this.serializeField("Wave control", this.waveControl) + " ";
		serialized += this.serializeField("Linear motion matrix", this.motionControl.getLinearString()) + " ";
		serialized += this.serializeField("Rotation matrix", this.motionControl.getRotationString()) + " ";
		serialized += this.serializeField("Alpha constant", this.alphaConstant);
		return serialized;
	}
	
	private String serializeField(String descriptor, Object... data) {
		String serializedData = new String();
		for(Object obj : data) serializedData += obj + " ";
		return descriptor + ":<" + serializedData.substring(0, serializedData.length() - 1) + ">";
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getParent() {
		return this.parent;
	}
	
	public ColorRange getColorRange() {
		return this.colorRange;
	}
	
	public WaveControl getWaveControl() {
		return this.waveControl;
	}
	
	public MotionControl getMotionControl() {
		return this.motionControl;
	}
	
	public double getAlphaConstant() {
		return this.alphaConstant;
	}
	
	public static class ColorRange {		
		public final int r, R, g, G, b, B;
		private final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		public ColorRange(int r, int R, int g, int G, int b, int B) {
			this.checkRange(r, R);
			this.checkRange(g, G);
			this.checkRange(b, B);
			
			this.r = r;
			this.R = R;
			this.g = g;
			this.G = G;
			this.b = b;
			this.B = B;
		}
		
		private void checkRange(int c, int C) {
			if(c < 0 || c > C || C > 255) {
				throw new RuntimeException("Invalid color range");
			}
		}
		
		public int getRandomColor() {
			int red = this.random.nextInt(this.r, this.R + 1);
			int green = this.random.nextInt(this.g, this.G + 1);
			int blue = this.random.nextInt(this.b, this.B + 1);
			
			return (red << 16) + (green << 8) + blue;
		}
		
		public int getAverageColor() {
			int red = (this.r + this.R) / 2;
			int green = (this.g + this.G) / 2;
			int blue = (this.b + this.B) / 2;
			
			return (red << 16) + (green << 8) + blue;
		}
	}
	
	public static class WaveControl {		
		private final int timeBetweenWaves, particlesPerWave;
		
		public WaveControl(int timeBetweenWaves, int particlesPerWave) {
			this.timeBetweenWaves = timeBetweenWaves;
			this.particlesPerWave = particlesPerWave;
		}
		
		public int getTimeBetweenWaves() {
			return this.timeBetweenWaves;
		}

		public int getParticlesPerWave() {
			return this.particlesPerWave;
		}
		
		public String toString() {
			return this.timeBetweenWaves + " " + this.particlesPerWave;
		}
	}
	
	public static class MotionControl {		
		private final double[] linear;
		private final double[] rotation;
		
		public MotionControl(double[] linear, double[] rotation) {
			this.linear = linear;
			this.rotation = rotation;
		}
		
		public double[] getLinear() {
			return this.linear;
		}
		
		public double[] getRotation() {
			return this.rotation;
		}
		
		public String getLinearString() {
			StringBuilder sb = new StringBuilder();
			
			for(double value : this.linear) {
				sb.append(value);
				sb.append(" ");
			}
			
			return sb.substring(0, sb.length() - 1);
		}
		
		public String getRotationString() {
			StringBuilder sb = new StringBuilder();
			
			for(double value : this.rotation) {
				sb.append(value);
				sb.append(" ");
			}
			
			return sb.substring(0, sb.length() - 1);
		}
		
		public String toString() {
			return this.getLinearString() + " - " + this.getRotationString();
		}
	}
}