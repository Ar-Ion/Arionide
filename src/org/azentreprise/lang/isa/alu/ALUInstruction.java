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
package org.azentreprise.lang.isa.alu;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.azentreprise.lang.Code;
import org.azentreprise.lang.Data;
import org.azentreprise.lang.LangarionError;
import org.azentreprise.lang.isa.Instruction;

public abstract class ALUInstruction extends Instruction {
	
	private final List<String> operands = new ArrayList<>();
	private final List<Data> data = new ArrayList<>();
		
	protected void loadOperand(String operand) {
		this.operands.add(operand);
	}
	
	protected int fetchOperandCount() {
		return this.operands.size();
	}
	
	protected BigInteger getInput(int id) {
		return this.data.get(id).asBigInteger();
	}
	
	protected Data getOutput(int id) {
		return this.data.get(id);
	}
	
	public void execute(Code code) throws LangarionError {
		this.data.clear();
		
		for(String element : operands) {
			this.data.add(code.var(element, true));
		}
		
		this.compute();
	}
	
	public abstract void compute();
}
