package ch.innovazion.arionide.lang.notek.literal;

import ch.innovazion.arionide.lang.notek.NotekInstruction;
import ch.innovazion.arionide.lang.symbols.Parameter;
import ch.innovazion.arionide.lang.symbols.Text;
import ch.innovazion.arionide.project.StructureModel;
import ch.innovazion.arionide.project.StructureModelFactory;

public class Comment extends NotekInstruction {

	public StructureModel createStructureModel() {
		return StructureModelFactory
				.draft("Comment")
				.withColor(0.8f)
				.withComment("Creates a comment")
				.beginSignature("default")
					.withParameter(new Parameter("Comment").setTextOnly(true))
				.endSignature()
				.build();
	}
}