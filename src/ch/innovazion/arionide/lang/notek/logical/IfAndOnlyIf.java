package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class IfAndOnlyIf extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("If and only if")
				.withColor(0.7f)
				.withComment("A is true if and only if B is true")
				.beginSignature("Truth")
					.withParameter(new Parameter("A").asConstant(new Reference()))
					.withParameter(new Parameter("B").asConstant(new Reference()))
				.endSignature()
				.build();
	}
}