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
package org.azentreprise.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azentreprise.lang.isa.Branch;
import org.azentreprise.lang.isa.Call;
import org.azentreprise.lang.isa.FuncParamType;
import org.azentreprise.lang.isa.Loop;
import org.azentreprise.lang.isa.NFSRead;
import org.azentreprise.lang.isa.NFSWrite;
import org.azentreprise.lang.isa.Nop;
import org.azentreprise.lang.isa.Set;
import org.azentreprise.lang.isa.alu.Add;
import org.azentreprise.lang.isa.alu.And;
import org.azentreprise.lang.isa.alu.Div;
import org.azentreprise.lang.isa.alu.Mul;
import org.azentreprise.lang.isa.alu.Neg;
import org.azentreprise.lang.isa.alu.Or;
import org.azentreprise.lang.isa.alu.Sub;

public class ISAMapping {

	private static final Map<String, InstructionInfo> mapping = new HashMap<>();
	
	static {
		// Common
		mapping.put("Set", new InstructionInfo(Set.class, FuncParamType.VARNAME, FuncParamType.INPUT));
		mapping.put("Branch", new InstructionInfo(Branch.class, FuncParamType.VARNAME, FuncParamType.FUNCNAME, FuncParamType.FUNCNAME));
		mapping.put("Loop", new InstructionInfo(Loop.class, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("Call", new InstructionInfo(Call.class, FuncParamType.FUNCNAME));
		mapping.put("Nop", new InstructionInfo(Nop.class));
		// Native File System
		mapping.put("NFSRead", new InstructionInfo(NFSRead.class, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("NFSWrite", new InstructionInfo(NFSWrite.class, FuncParamType.VARNAME, FuncParamType.VARNAME));
		// Arithmetic
		mapping.put("Add", new InstructionInfo(Add.class, FuncParamType.VARNAME, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("Sub", new InstructionInfo(Sub.class, FuncParamType.VARNAME, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("Mul", new InstructionInfo(Mul.class, FuncParamType.VARNAME, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("Div", new InstructionInfo(Div.class, FuncParamType.VARNAME, FuncParamType.VARNAME, FuncParamType.VARNAME));
		// Logic
		mapping.put("And", new InstructionInfo(And.class, FuncParamType.VARNAME, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("Or", new InstructionInfo(Or.class, FuncParamType.VARNAME, FuncParamType.VARNAME, FuncParamType.VARNAME));
		mapping.put("Neg", new InstructionInfo(Neg.class, FuncParamType.VARNAME));
	}

	protected static java.util.Set<String> getNames() {
		return mapping.keySet();
	}
	
	public static InstructionInfo getNative(String name) {
		return mapping.get(name);
	}
	
	public static void loadISADesignObjects(String pattern, List<String> list) {
		list.add(pattern.replace("name", "Set:Name:Data").replace("x", "0").replace("y", "2"));
		list.add(pattern.replace("name", "Branch:Condition:Yes:No").replace("x", "2").replace("y", "0"));
		list.add(pattern.replace("name", "Loop:Condition:Increment").replace("x", "-2").replace("y", "0"));
		list.add(pattern.replace("name", "Call:Function").replace("x", "1").replace("y", "-2"));
		list.add(pattern.replace("name", "Nop").replace("x", "-1").replace("y", "-2"));
		
		list.add(pattern.replace("name", "NFSRead:File:Storage").replace("x", "2").replace("y", "-3"));
		list.add(pattern.replace("name", "NFSWrite:File:Storage").replace("x", "-2").replace("y", "-3"));
		
		list.add(pattern.replace("name", "Sub:FOP:SOP:Output").replace("x", "-3").replace("y", "0"));
		list.add(pattern.replace("name", "Add:FOP:SOP:Output").replace("x", "-2").replace("y", "3"));
		list.add(pattern.replace("name", "Mul:FOP:SOP:Output").replace("x", "2").replace("y", "3"));
		list.add(pattern.replace("name", "Div:FOP:SOP:Output").replace("x", "3").replace("y", "0"));

		list.add(pattern.replace("name", "And:FOP:SOP:Output").replace("x", "3").replace("y", "4"));
		list.add(pattern.replace("name", "Neg:Variable").replace("x", "0").replace("y", "5"));
		list.add(pattern.replace("name", "Or:FOP:SOP:Output").replace("x", "-3").replace("y", "4"));
	}
	
	public static void loadISADesignLinks(String pattern, List<String> list) {
		list.add(pattern.replace("x", "Set:Name:Data").replace("y", "Branch:Condition:Yes:No"));
		list.add(pattern.replace("x", "Branch:Condition:Yes:No").replace("y", "Call:Function"));
		list.add(pattern.replace("x", "Call:Function").replace("y", "Nop"));
		list.add(pattern.replace("x", "Nop").replace("y", "Loop:Condition:Increment"));
		list.add(pattern.replace("x", "Loop:Condition:Increment").replace("y", "Set:Name:Data"));
		
		list.add(pattern.replace("x", "NFSRead:File:Storage").replace("y", "NFSWrite:File:Storage"));

		list.add(pattern.replace("x", "Sub:FOP:SOP:Output").replace("y", "Add:FOP:SOP:Output"));
		list.add(pattern.replace("x", "Add:FOP:SOP:Output").replace("y", "Mul:FOP:SOP:Output"));
		list.add(pattern.replace("x", "Mul:FOP:SOP:Output").replace("y", "Div:FOP:SOP:Output"));

		list.add(pattern.replace("x", "And:FOP:SOP:Output").replace("y", "Neg:Variable"));
		list.add(pattern.replace("x", "Neg:Variable").replace("y", "Or:FOP:SOP:Output"));
	}
}
