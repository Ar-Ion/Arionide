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
package ch.innovazion.arionide.lang.avr;

import ch.innovazion.arionide.lang.Environment;
import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.avr.arithmetic.Add;
import ch.innovazion.arionide.lang.avr.arithmetic.AddWithCarry;
import ch.innovazion.arionide.lang.avr.arithmetic.Decrement;
import ch.innovazion.arionide.lang.avr.arithmetic.Increment;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalComplement;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalConjunction;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalConjunctionImmediate;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalDisjunction;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalDisjunctionImmediate;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalExclusiveDisjunction;
import ch.innovazion.arionide.lang.avr.arithmetic.LogicalNegation;
import ch.innovazion.arionide.lang.avr.arithmetic.RegisterBitClear;
import ch.innovazion.arionide.lang.avr.arithmetic.RegisterBitSet;
import ch.innovazion.arionide.lang.avr.arithmetic.RegisterClear;
import ch.innovazion.arionide.lang.avr.arithmetic.RegisterSet;
import ch.innovazion.arionide.lang.avr.arithmetic.Subtract;
import ch.innovazion.arionide.lang.avr.arithmetic.SubtractImmediate;
import ch.innovazion.arionide.lang.avr.arithmetic.SubtractImmediateWithCarry;
import ch.innovazion.arionide.lang.avr.arithmetic.SubtractWithCarry;
import ch.innovazion.arionide.lang.avr.arithmetic.Test;
import ch.innovazion.arionide.lang.avr.bitwise.ArithmeticShiftRight;
import ch.innovazion.arionide.lang.avr.bitwise.CarryClear;
import ch.innovazion.arionide.lang.avr.bitwise.CarrySet;
import ch.innovazion.arionide.lang.avr.bitwise.HalfCarryClear;
import ch.innovazion.arionide.lang.avr.bitwise.HalfCarrySet;
import ch.innovazion.arionide.lang.avr.bitwise.IOBitClear;
import ch.innovazion.arionide.lang.avr.bitwise.IOBitSet;
import ch.innovazion.arionide.lang.avr.bitwise.InterruptClear;
import ch.innovazion.arionide.lang.avr.bitwise.InterruptSet;
import ch.innovazion.arionide.lang.avr.bitwise.LogicalShiftLeft;
import ch.innovazion.arionide.lang.avr.bitwise.LogicalShiftRight;
import ch.innovazion.arionide.lang.avr.bitwise.NegativeClear;
import ch.innovazion.arionide.lang.avr.bitwise.NegativeSet;
import ch.innovazion.arionide.lang.avr.bitwise.NoOperation;
import ch.innovazion.arionide.lang.avr.bitwise.OverflowClear;
import ch.innovazion.arionide.lang.avr.bitwise.OverflowSet;
import ch.innovazion.arionide.lang.avr.bitwise.RotateLeft;
import ch.innovazion.arionide.lang.avr.bitwise.RotateRight;
import ch.innovazion.arionide.lang.avr.bitwise.SignClear;
import ch.innovazion.arionide.lang.avr.bitwise.SignSet;
import ch.innovazion.arionide.lang.avr.bitwise.Swap;
import ch.innovazion.arionide.lang.avr.bitwise.TClear;
import ch.innovazion.arionide.lang.avr.bitwise.TLoadBit;
import ch.innovazion.arionide.lang.avr.bitwise.TSet;
import ch.innovazion.arionide.lang.avr.bitwise.TStoreBit;
import ch.innovazion.arionide.lang.avr.bitwise.ZeroClear;
import ch.innovazion.arionide.lang.avr.bitwise.ZeroSet;
import ch.innovazion.arionide.lang.avr.branch.BranchIfCarryClear;
import ch.innovazion.arionide.lang.avr.branch.BranchIfCarrySet;
import ch.innovazion.arionide.lang.avr.branch.BranchIfEqual;
import ch.innovazion.arionide.lang.avr.branch.BranchIfGreaterOrEqual;
import ch.innovazion.arionide.lang.avr.branch.BranchIfHalfCarryClear;
import ch.innovazion.arionide.lang.avr.branch.BranchIfHalfCarrySet;
import ch.innovazion.arionide.lang.avr.branch.BranchIfInterruptsDisabled;
import ch.innovazion.arionide.lang.avr.branch.BranchIfInterruptsEnabled;
import ch.innovazion.arionide.lang.avr.branch.BranchIfLess;
import ch.innovazion.arionide.lang.avr.branch.BranchIfLower;
import ch.innovazion.arionide.lang.avr.branch.BranchIfMinus;
import ch.innovazion.arionide.lang.avr.branch.BranchIfNotEqual;
import ch.innovazion.arionide.lang.avr.branch.BranchIfOverflowClear;
import ch.innovazion.arionide.lang.avr.branch.BranchIfOverflowSet;
import ch.innovazion.arionide.lang.avr.branch.BranchIfPlus;
import ch.innovazion.arionide.lang.avr.branch.BranchIfSREGBitClear;
import ch.innovazion.arionide.lang.avr.branch.BranchIfSREGBitSet;
import ch.innovazion.arionide.lang.avr.branch.BranchIfSameOrHigher;
import ch.innovazion.arionide.lang.avr.branch.BranchIfTClear;
import ch.innovazion.arionide.lang.avr.branch.BranchIfTSet;
import ch.innovazion.arionide.lang.avr.branch.Compare;
import ch.innovazion.arionide.lang.avr.branch.CompareImmediate;
import ch.innovazion.arionide.lang.avr.branch.CompareSkipIfEqual;
import ch.innovazion.arionide.lang.avr.branch.CompareWithCarry;
import ch.innovazion.arionide.lang.avr.branch.RelativeCall;
import ch.innovazion.arionide.lang.avr.branch.RelativeJump;
import ch.innovazion.arionide.lang.avr.branch.Return;
import ch.innovazion.arionide.lang.avr.branch.ReturnFromInterrupt;
import ch.innovazion.arionide.lang.avr.branch.SkipIfIOBitClear;
import ch.innovazion.arionide.lang.avr.branch.SkipIfIOBitSet;
import ch.innovazion.arionide.lang.avr.branch.SkipIfRegisterBitClear;
import ch.innovazion.arionide.lang.avr.branch.SkipIfRegisterBitSet;
import ch.innovazion.arionide.lang.avr.programs.AVRSkeletonBuilder;
import ch.innovazion.arionide.lang.avr.special.Block;
import ch.innovazion.arionide.lang.avr.special.Break;
import ch.innovazion.arionide.lang.avr.special.Organize;
import ch.innovazion.arionide.lang.avr.special.Spacer;
import ch.innovazion.arionide.lang.avr.transfers.Input;
import ch.innovazion.arionide.lang.avr.transfers.Load;
import ch.innovazion.arionide.lang.avr.transfers.LoadImmediate;
import ch.innovazion.arionide.lang.avr.transfers.LoadProgramMemory;
import ch.innovazion.arionide.lang.avr.transfers.Move;
import ch.innovazion.arionide.lang.avr.transfers.Output;
import ch.innovazion.arionide.lang.avr.transfers.Pop;
import ch.innovazion.arionide.lang.avr.transfers.Push;
import ch.innovazion.arionide.lang.avr.transfers.Store;
import ch.innovazion.arionide.lang.programs.Debugger;
import ch.innovazion.arionide.lang.programs.Relocator;
import ch.innovazion.arionide.project.Storage;

public class AVRLanguage extends Language {

	private static final long serialVersionUID = -962242275743950263L;

	private final Environment env = new AVREnvironment(this);
	private Instruction entryPoint = new AVREntryPoint();
		
	protected void registerPrograms(Storage storage) {
		registerProgram(new AVRSkeletonBuilder(storage));
		registerProgram(new Relocator(storage, 1 << 17)); // 128KB program memory
		registerProgram(new Debugger(storage, env));
	}
	
	protected void registerInstructions() {
		registerShadowInstruction(this.entryPoint = new AVREntryPoint());
		
		registerOperator(new Block());
		registerOperator(new Spacer());
		registerOperator(new Break());
		registerOperator(new Organize());

		registerInstruction(new Add());
		registerInstruction(new AddWithCarry());
		registerInstruction(new Subtract());
		registerInstruction(new SubtractImmediate());
		registerInstruction(new SubtractWithCarry());
		registerInstruction(new SubtractImmediateWithCarry());
		registerInstruction(new LogicalConjunction());
		registerInstruction(new LogicalConjunctionImmediate());
		registerInstruction(new LogicalDisjunction());
		registerInstruction(new LogicalDisjunctionImmediate());
		registerInstruction(new LogicalExclusiveDisjunction());
		registerInstruction(new LogicalComplement());
		registerInstruction(new LogicalNegation());
		registerInstruction(new RegisterBitSet());
		registerInstruction(new RegisterBitClear());
		registerInstruction(new Increment());
		registerInstruction(new Decrement());
		registerInstruction(new Test());
		registerInstruction(new RegisterClear());
		registerInstruction(new RegisterSet());
		
		registerInstruction(new RelativeJump());
		registerInstruction(new RelativeCall());
		registerInstruction(new Return());
		registerInstruction(new ReturnFromInterrupt());
		registerInstruction(new CompareSkipIfEqual());
		registerInstruction(new Compare());
		registerInstruction(new CompareWithCarry());
		registerInstruction(new CompareImmediate());
		registerInstruction(new SkipIfRegisterBitClear());
		registerInstruction(new SkipIfRegisterBitSet());
		registerInstruction(new SkipIfIOBitClear());
		registerInstruction(new SkipIfIOBitSet());
		registerInstruction(new BranchIfSREGBitSet());
		registerInstruction(new BranchIfSREGBitClear());
		registerInstruction(new BranchIfEqual());
		registerInstruction(new BranchIfNotEqual());
		registerInstruction(new BranchIfCarrySet());
		registerInstruction(new BranchIfCarryClear());
		registerInstruction(new BranchIfSameOrHigher());
		registerInstruction(new BranchIfLower());
		registerInstruction(new BranchIfMinus());
		registerInstruction(new BranchIfPlus());
		registerInstruction(new BranchIfGreaterOrEqual());
		registerInstruction(new BranchIfLess());
		registerInstruction(new BranchIfHalfCarrySet());
		registerInstruction(new BranchIfHalfCarryClear());
		registerInstruction(new BranchIfTSet());
		registerInstruction(new BranchIfTClear());
		registerInstruction(new BranchIfOverflowSet());
		registerInstruction(new BranchIfOverflowClear());
		registerInstruction(new BranchIfInterruptsEnabled());
		registerInstruction(new BranchIfInterruptsDisabled());
		
		registerInstruction(new Load());
		registerInstruction(new Store());
		registerInstruction(new Move());
		registerInstruction(new LoadImmediate());
		registerInstruction(new Input());
		registerInstruction(new Output());
		registerInstruction(new LoadProgramMemory());
		registerInstruction(new Push());
		registerInstruction(new Pop());
		
		registerInstruction(new IOBitSet());
		registerInstruction(new IOBitClear());
		registerInstruction(new LogicalShiftLeft());
		registerInstruction(new LogicalShiftRight());
		registerInstruction(new RotateLeft());
		registerInstruction(new RotateRight());
		registerInstruction(new ArithmeticShiftRight());
		registerInstruction(new Swap());
		registerInstruction(new TStoreBit());
		registerInstruction(new TLoadBit());
		registerInstruction(new CarrySet());
		registerInstruction(new CarryClear());
		registerInstruction(new NegativeSet());
		registerInstruction(new NegativeClear());
		registerInstruction(new ZeroSet());
		registerInstruction(new ZeroClear());
		registerInstruction(new InterruptSet());
		registerInstruction(new InterruptClear());
		registerInstruction(new SignSet());
		registerInstruction(new SignClear());
		registerInstruction(new OverflowSet());
		registerInstruction(new OverflowClear());
		registerInstruction(new TSet());
		registerInstruction(new TClear());
		registerInstruction(new HalfCarrySet());
		registerInstruction(new HalfCarryClear());
		registerInstruction(new NoOperation());
	}

	protected short getVersionMajor() {
		return 1;
	}

	protected short getVersionMinor() {
		return 0;
	}

	public String getVendorUID() {
		return "Atmel Corp. (Microchip Technology)";
	}

	public Instruction getEntryPoint() {
		return entryPoint;
	}

	public Environment getEnvironment() {
		return env;
	}
}
