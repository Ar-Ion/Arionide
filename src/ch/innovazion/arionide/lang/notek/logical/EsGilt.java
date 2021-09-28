package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Reference;
import ch.innovazion.arionide.lang.symbols.Text;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class EsGilt extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Es gilt")
				.withColor(0.6f)
				.withComment("Claims that the following holds true")
				.beginSignature("Truth")
					.withParameter(new Parameter("Claim").asConstant(new Text()))
					.withParameter(new Parameter("Proof").asConstant(new Reference()))
				.endSignature()
				.beginSignature("Postulate")
					.withParameter(new Parameter("Claim").setTextOnly(true))
				.endSignature()
				.build();
	}
}