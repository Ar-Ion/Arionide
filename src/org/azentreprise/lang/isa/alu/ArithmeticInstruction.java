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
package org.azentreprise.lang.isa.alu;

import java.math.BigInteger;

import org.azentreprise.lang.Data;

public abstract class ArithmeticInstruction extends ALUInstruction {
	
	protected ArithmeticInstruction(String in0, String in1, String out) {
		this.loadOperand(in0);
		this.loadOperand(in1);
		this.loadOperand(out);
	}
	
	public void compute() {
		Data output = this.fetchOperandCount() != 2 ? this.getOutput(2) : this.getOutput(1);
		output.setData(this.compute(this.getInput(0), this.getInput(1)));
	}
	
	public abstract BigInteger compute(BigInteger op0, BigInteger op1);
}
