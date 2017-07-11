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
package org.azentreprise.ui.views.project.editors;

import java.awt.Font;
import java.awt.Frame;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.azentreprise.Debug;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.ui.Style;
import org.azentreprise.ui.Style.ColorRange;
import org.azentreprise.ui.Style.MotionControl;
import org.azentreprise.ui.Style.WaveControl;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.components.UIText;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.UIProjectView;

public class UIStyleEditorProjectView extends UIEditorProjectView {
	
	private static Style style;
	
	public UIStyleEditorProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent);
	}
	
	public void initComponents() {
		this.add(new UILabel(this, 0.71f, 0.05f, 0.95f, 0.15f, "Style Editor - " + style.getName()).alterFont(Font.BOLD));
		
		this.add(new UILabel(this, 0.71f, 0.15f, 0.82f, 0.19f, "Parent:"));
		this.add(new UIText(this, 0.83f, 0.15f, 0.94f, 0.19f, "Parent name").setText(style.getParent()));
		
		this.add(new UILabel(this, 0.71f, 0.20f, 0.82f, 0.24f, "Red range:"));
		this.add(new UIText(this, 0.83f, 0.20f, 0.94f, 0.24f, "Lowest - Highest").setText(style.getColorRange().r + " - " + style.getColorRange().R));
	
		this.add(new UILabel(this, 0.71f, 0.25f, 0.82f, 0.29f, "Green range:"));
		this.add(new UIText(this, 0.83f, 0.25f, 0.94f, 0.29f, "Lowest - Highest").setText(style.getColorRange().g + " - " + style.getColorRange().G));
		
		this.add(new UILabel(this, 0.71f, 0.30f, 0.82f, 0.34f, "Blue range:"));
		this.add(new UIText(this, 0.83f, 0.30f, 0.94f, 0.34f, "Lowest - Highest").setText(style.getColorRange().b + " - " + style.getColorRange().B));
		
		this.add(new UILabel(this, 0.71f, 0.35f, 0.82f, 0.39f, "Wave control:"));
		this.add(new UIText(this, 0.83f, 0.35f, 0.94f, 0.39f, "Frequency - Amplitude").setText(style.getWaveControl().getTimeBetweenWaves() + " - " + style.getWaveControl().getParticlesPerWave()));
		
		this.add(new UILabel(this, 0.71f, 0.40f, 0.82f, 0.44f, "Linear motion matrix:"));
		this.add(new UIText(this, 0.83f, 0.40f, 0.94f, 0.44f, "Elements (Doubles)").setText(style.getMotionControl().getLinearString()));
		
		this.add(new UILabel(this, 0.71f, 0.45f, 0.82f, 0.49f, "Rotation matrix:"));
		this.add(new UIText(this, 0.83f, 0.45f, 0.94f, 0.49f, "Elements (Doubles)").setText(style.getMotionControl().getRotationString()));
		
		this.add(new UILabel(this, 0.71f, 0.50f, 0.82f, 0.54f, "Alpha constant:"));
		this.add(new UIText(this, 0.83f, 0.50f, 0.94f, 0.54f, "Double value").setText(Double.toString(style.getAlphaConstant())));
	}
	
	public static void setTarget(Style style) {
		UIStyleEditorProjectView.style = style;
	}

	public boolean validate() {
		Debug.taskBegin("validating the user input");
		
		String[] data = UIText.fetchUserData(this);
		
		String parent = data[0];
		
		String[] redRange = data[1].replace(" ", "").split("-");
		String[] greenRange = data[2].replace(" ", "").split("-");
		String[] blueRange = data[3].replace(" ", "").split("-");

		String[] waveControlString = data[4].replace(" ", "").split("-");
		
		String[] linearMotionMatrix = data[5].split(" ");
		String[] rotationMatrix = data[6].split(" ");
		
		String alphaConstant = data[7];
		
		boolean resolved = false;
		
		for(Style style : Projects.getCurrentProject().styles) {
			if(style.getName().equalsIgnoreCase(parent)) {
				resolved = true;
			}
		}
		
		if(!resolved && !parent.equalsIgnoreCase("None")) {
			JOptionPane.showConfirmDialog(null, "Unable to resolve the parent name", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(redRange.length < 2) {
			JOptionPane.showConfirmDialog(null, "Syntax error for the red range", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		} else if(greenRange.length < 2) {
			JOptionPane.showConfirmDialog(null, "Syntax error for the green range", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		} else if(blueRange.length < 2) {
			JOptionPane.showConfirmDialog(null, "Syntax error for the blue range", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		} else if(waveControlString.length < 2) {
			JOptionPane.showConfirmDialog(null, "Syntax error for the wave control", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
		}
		
		int redMin = Integer.parseInt(redRange[0]);
		int redMax = Integer.parseInt(redRange[1]);
		
		int greenMin = Integer.parseInt(greenRange[0]);
		int greenMax = Integer.parseInt(greenRange[1]);
			
		int blueMin = Integer.parseInt(blueRange[0]);
		int blueMax = Integer.parseInt(blueRange[1]);
			
		int frequency = Integer.parseInt(waveControlString[0]);
		int amplitude = Integer.parseInt(waveControlString[1]);
			
		double[] doublesLMM = new double[linearMotionMatrix.length];
			
		for(int i = 0; i < doublesLMM.length; i++) {
			doublesLMM[i] = Double.parseDouble(linearMotionMatrix[i]);
		}
			
		double[] doublesRM = new double[rotationMatrix.length];
			
		for(int i = 0; i < doublesRM.length; i++) {
			doublesRM[i] = Double.parseDouble(rotationMatrix[i]);
		}
			
		double doubleAC = Double.parseDouble(alphaConstant);
			
		ColorRange colorRange = new ColorRange(redMin, redMax, greenMin, greenMax, blueMin, blueMax);
			
		WaveControl waveControl = new WaveControl(frequency, amplitude);

		MotionControl motionControl = new MotionControl(doublesLMM, doublesRM);
			
		Style newStyle = new Style(style.getName(), parent, colorRange, waveControl, motionControl, doubleAC);
		
		Project project = Projects.getCurrentProject();
		
		project.styles.set(project.styles.indexOf(style), newStyle);
		
		try {
			project.save();
		} catch (IllegalArgumentException | IllegalAccessException | IOException exception) {
			Debug.exception(exception);
		}
		
		UIProjectView.simpleRenderer.reset(project);
		UIProjectView.complexRenderer.reset(project);
		
		Debug.taskEnd();
		
		return true;
	}
}
