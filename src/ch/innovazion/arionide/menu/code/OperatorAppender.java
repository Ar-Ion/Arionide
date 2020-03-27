package ch.innovazion.arionide.menu.code;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.menu.MenuManager;

public class OperatorAppender extends InstructionAppender {

	public OperatorAppender(MenuManager manager) {
		super(manager);
	}

	protected List<Instruction> getInstructions() {
		Language lang = LanguageManager.get(target.getLanguage());

		if(lang != null) {
			return lang.getOperators();
		} else {
			return Arrays.asList();
		}
	}

	public void onAction(String action) {
		appendInstruction(id);
	}
}