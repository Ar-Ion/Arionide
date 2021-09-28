package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Suppose extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Suppose")
				.withColor(0.3f)
				.withComment("Creates an supposition")
				.beginSignature("default")
					.withParameter(new Parameter("Supposition").setTextOnly(true))
				.endSignature()
				.build();
	}
}