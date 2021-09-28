package ch.innovazion.arionide.lang.notek.logical;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Text;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Assume extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Assume")
				.withColor(0.1f)
				.withComment("Creates an assumption")
				.beginSignature("Single")
					.withParameter(new Parameter("Assumption").setTextOnly(true))
				.endSignature()
				.beginSignature("Double")
					.withParameter(new Parameter("Assumption 1").asConstant(new Text()))
					.withParameter(new Parameter("Assumption 2").asConstant(new Text()))
				.endSignature()
				.beginSignature("Triple")
					.withParameter(new Parameter("Assumption 1").asConstant(new Text()))
					.withParameter(new Parameter("Assumption 2").asConstant(new Text()))
					.withParameter(new Parameter("Assumption 3").asConstant(new Text()))
				.endSignature()
				.beginSignature("Quadruple")
					.withParameter(new Parameter("Assumption 1").asConstant(new Text()))
					.withParameter(new Parameter("Assumption 2").asConstant(new Text()))
					.withParameter(new Parameter("Assumption 3").asConstant(new Text()))
					.withParameter(new Parameter("Assumption 4").asConstant(new Text()))
				.endSignature()
				.build();
	}
}