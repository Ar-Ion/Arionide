package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Implies extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Implies")
				.withColor(0.5f)
				.withComment("Creates an implication")
				.beginSignature("default")
					.withParameter(new Parameter("Implication").setTextOnly(true))
				.endSignature()
				.build();
	}
}