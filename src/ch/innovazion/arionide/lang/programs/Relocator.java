/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE used to conceive applications and algorithms in a three-dimensional environment. 
 * It is the work of Arion Zimmermann for his final high-school project at Calvin College (Geneva, Switzerland).
 * Copyright (C) 2016-2020 Innovazion. All rights reserved.
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
package ch.innovazion.arionide.lang.programs;

import java.util.HashMap;
import java.util.Map;

import ch.innovazion.arionide.lang.ApplicationMemory;
import ch.innovazion.arionide.lang.Program;
import ch.innovazion.arionide.lang.Skeleton;
import ch.innovazion.arionide.lang.symbols.Callable;
import ch.innovazion.arionide.lang.symbols.Information;
import ch.innovazion.arionide.project.Storage;

public class Relocator extends Program {

	public Relocator(Storage storage) {
		super(storage);
	}
	
	public void run(int rootStructure, ProgramIO io) {
		Skeleton skeleton = io.in(Skeleton.class);
		
		if(skeleton != null) {
			Map<Long, Callable> text = new HashMap<>();
			Map<Long, Information> data = new HashMap<>();
			
			for(Callable callable : skeleton.getText()) {
				text.put(skeleton.getTextAddress(callable), callable);
			}
			
			for(Information info : skeleton.getRodata()) {
				data.put(skeleton.getRodataAddress(info), info);
			}
			
			for(Information info : skeleton.getBSS()) {
				data.put(skeleton.getBSSAddress(info), info);
			}
			
			for(Information info : skeleton.getData()) {
				data.put(skeleton.getDataAddress(info), info);
			}
			
			io.out(new ApplicationMemory(text, data));
		} else {
			io.fatal("Cannot run relocator without having built the application skeleton");
		}		
	}

	public String getName() {
		return "Built-in relocator";
	}
}
