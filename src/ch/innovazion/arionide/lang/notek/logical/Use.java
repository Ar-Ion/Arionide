package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Use extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Use")
				.withColor(0.4f)
				.withComment("Defines a dependency on a concept or proof")
				.beginSignature("Definition")
					.withParameter(new Parameter("Definition").asConstant(new Reference()))
				.endSignature()
				.beginSignature("Theorem")
					.withParameter(new Parameter("Theorem").asConstant(new Reference()))
				.endSignature()
				.beginSignature("Lemma")
					.withParameter(new Parameter("Lemma").asConstant(new Reference()))
				.endSignature()
				.build();
	}
}