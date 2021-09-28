package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Let extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Let")
				.withColor(0.2f)
				.withComment("Creates a requirement")
				.beginSignature("default")
					.withParameter(new Parameter("Requirement").setTextOnly(true))
				.endSignature()
				.build();
	}
}