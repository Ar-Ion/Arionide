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
package org.azentreprise;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.azentreprise.configuration.SystemConfiguration;
import org.azentreprise.configuration.WorkspaceConfiguration;
import org.azentreprise.ui.UIMain;

public class Arionide {
	
	private static volatile File root;
	private static volatile SystemConfiguration configuration;
	private static volatile boolean running = true;
	private static final List<Tickable> tickables = new ArrayList<Tickable>();
	private static final List<Tickable> tickablesToAdd = new ArrayList<Tickable>();
	private static final List<Tickable> tickablesToRemove = new ArrayList<Tickable>();
	
	public static void main(String args[]) {
		try {
			Debug.taskBegin("starting up Arionide"); {
				Arionide.root = new File(System.getProperty("user.home") + File.separator + "Arionide Workspace");
				Arionide.root.mkdirs();
				
				Arionide.setup();
				
				Debug.taskBegin("loading UI"); {
					UIMain.init();
				} Debug.taskEnd();
			} Debug.taskEnd();
			
			Arionide.startTicking();
			
			Debug.taskBegin("shutting down Arionide"); {
				Projects.getWorkspaceConfiguration().save();
				Arionide.configuration.save();
				if(Projects.getCurrentProject() != null) {
					Projects.getCurrentProject().save();
				}
			} Debug.taskEnd();
		} catch(Exception exception) {
			exception.printStackTrace();
			Debug.exception(exception);
			System.exit(1);
		}
		
		Debug.info("Arionide process terminated");
		System.exit(0);
	}
	
	public static void setup() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Debug.taskBegin("loading configuration"); {
			Arionide.configuration = new SystemConfiguration();
		} Debug.taskEnd();
		
		Debug.taskBegin("setting up log"); {
			File log = new File(root, "Arionide.log");
			Debug.setDebugOutput(new FileOutputStream(log));
		} Debug.taskEnd();
		
		Debug.taskBegin("checking licence agreement"); {
			File license = new File(root, "LICENSE.txt");
			File background = new File(root, "bg.jpg");
			
			if(!license.exists()) {
				InputStream input = Arionide.class.getResourceAsStream("LICENSE.txt");
				
				OutputStream output = new FileOutputStream(license);
				byte[] buffer = new byte[64];
				int length = 0;
				while((length = input.read(buffer)) != -1) {
					output.write(buffer, 0, length);
				}
				output.close();
				
				JOptionPane.showMessageDialog(null, "This software is under GNU GPLv3 license.\nThe copy of the license has been installed in '" + license.getAbsolutePath() + "'.\nBy clicking on OK, you agree all the terms of this license.", "User license agreement", JOptionPane.INFORMATION_MESSAGE);
			}
			
			if(!background.exists()) {
				InputStream input = Arionide.class.getResourceAsStream("bg.jpg");
				
				OutputStream output = new FileOutputStream(background);
				byte[] buffer = new byte[64];
				int length = 0;
				while((length = input.read(buffer)) != -1) {
					output.write(buffer, 0, length);
				}
				output.close();
			}
		} Debug.taskEnd();
		
		Debug.taskBegin("setting up workspace"); {
			Arionide.configuration.workspaceLocation.mkdirs();
			WorkspaceConfiguration configuration = new WorkspaceConfiguration(new File(Arionide.configuration.workspaceLocation, "workspace.config"), "Workspace Configuration");
			Projects.openWorkspace(configuration);
		} Debug.taskEnd();
	}
	
	public static void startTicking() throws InterruptedException {
		Debug.taskBegin("ticking Arionide's execution loop");
			
		long ticksCount = 0L;
			
		while(Arionide.running) {
			long time = System.currentTimeMillis();
			
			Arionide.tickables.addAll(Arionide.tickablesToAdd);
			Arionide.tickablesToAdd.clear();
			Arionide.tickables.removeAll(Arionide.tickablesToRemove);
			Arionide.tickablesToRemove.clear();
			
			
			for(Tickable tickable : Arionide.tickables) {
				if(ticksCount % tickable.getTicksBetweenExecutions() == 0) {						
					tickable.runTick();
				}
			}
		
			ticksCount++;
			
			long sleepTime = 20L - (System.currentTimeMillis() - time);
			if(sleepTime > 0) {
				Thread.sleep(sleepTime);
			}
			
			Performances.tick();
		}
		
		Debug.taskEnd();
	}
	
	public static void shutdown() {
		Arionide.running = false;
	}
	
	public static void registerTickable(Tickable tickable) {
		Arionide.tickablesToAdd.add(tickable);
	}
	
	public static void unregisterTickable(Tickable tickable) {
		if(Arionide.tickables.contains(tickable)) {
			Arionide.tickablesToRemove.add(tickable);
		}
	}
	
	public static File getRoot() {
		return Arionide.root;
	}
	
	public static SystemConfiguration getSystemConfiguration() {
		return Arionide.configuration;
	}
	
	public static Font getSystemFont() {
		return Font.decode(Arionide.configuration.font);
	}
	
	public static String getClientIdentifier() {
		return "Arionide Alpha 0.1";
	}
	
	public static int getVersion() {
		return 97;
	}
	
	public static int getBackwardCompatibility() {
		return 25;
	}
}