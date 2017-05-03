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
package org.azentreprise.lang;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.configuration.Definitions;
import org.azentreprise.lang.Executable.CodeDescriptor;
import org.azentreprise.lang.Executable.FunctionDescriptor;

public class Linker {
	
	private static volatile boolean DEBUG_OVERWRITES = false;
	
	private final Definitions configuration;
	private final File targetExecutableFile;
	
	private DataOutputStream stream;
	
	public Linker(String targetExecutableName, Definitions configuration) {
		File buildDir = new File(Arionide.getSystemConfiguration().workspaceLocation, "build");
		buildDir.mkdirs();
		
		this.targetExecutableFile = new File(Arionide.getSystemConfiguration().workspaceLocation, targetExecutableName + ".langarion");
		this.configuration = configuration;
	}
	
	public void link() {
		Debug.taskBegin("compiling"); try {
			if(this.configuration.objects.size() > 0) {
				this.prepareStream();
				
				Map<Integer, FunctionDescriptor> functionDescriptors = new HashMap<>();
				Map<Integer, CodeDescriptor> codeDescriptors = new HashMap<>();

				long definitionsHashcode = 0L;
			
				for(String name : this.configuration.objects) {
					Executable executable = new Executable(this.buildInternalStream(name));
					
					if(definitionsHashcode != 0 && definitionsHashcode != executable.getDefinitionsHashcode()) {
						throw new LangarionError(name + " doesn't match the current definitions specification");
					}
					
					definitionsHashcode = executable.getDefinitionsHashcode();
					
					FunctionDescriptor[] fds = executable.getFunctionDescriptors();
					CodeDescriptor[] cds = executable.getCodeDescriptors();
					
					for(FunctionDescriptor fd : fds) {
						if(functionDescriptors.put(fd.getUID(), fd) != null && Linker.DEBUG_OVERWRITES) {
							System.out.println(name + " has overwritten function descriptor " + fd.getUID());
						}
					}
					
					for(CodeDescriptor cd : cds) {
						if(codeDescriptors.put(cd.getUID(), cd) != null && Linker.DEBUG_OVERWRITES) {
							 System.out.println(name + " has overwritten code descriptor " + cd.getUID());
						}
					}
				}
				
				this.stream.writeInt(0xC0FFEE);
				this.stream.writeLong(definitionsHashcode);
				this.stream.writeInt(functionDescriptors.size());
				
				for(FunctionDescriptor descriptor : functionDescriptors.values()) {
					this.stream.writeInt(descriptor.getUID());
					this.stream.writeInt(descriptor.getSuperiorUID());
					this.stream.writeInt(descriptor.getProperties());
					this.stream.writeInt(descriptor.getParentsUID().length);
										
					for(int uid : descriptor.getParentsUID()) {
						this.stream.writeInt(uid);
					}
				}
				
				for(CodeDescriptor descriptor : codeDescriptors.values()) {
					this.stream.writeInt(descriptor.getUID());
					this.stream.writeInt(descriptor.getConstantPool().length);
					
					for(String constant : descriptor.getConstantPool()) {
						assert constant.length() < 256;
						
						this.stream.write(constant.length());
						this.stream.write(constant.getBytes(Charset.forName("utf8")));
					}

					
					this.stream.writeInt(descriptor.getCode().length);
					this.stream.write(descriptor.getCode());
				}
			} else {
				throw new LangarionError("Nothing to link");
			}
		} catch(Exception exception) {
			Debug.exception(exception);
		} Debug.taskEnd();
	}
	
	private InputStream buildInternalStream(String executable) throws FileNotFoundException {
		return new FileInputStream(new File(this.targetExecutableFile.getParent(), executable + ".pre"));
	}
	
	private void prepareStream() throws FileNotFoundException {
		this.stream = new DataOutputStream(new FileOutputStream(this.targetExecutableFile));
	}
}
