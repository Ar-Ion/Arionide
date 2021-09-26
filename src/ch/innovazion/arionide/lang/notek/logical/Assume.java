package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Assume extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Assume")
				.withColor(0.1f)
				.withComment("Creates an assumption")
				.beginSignature("Text")
				.withParameter(new Parameter("Text").setTextOnly(true))
				.endSignature()
				.build();
	}
}