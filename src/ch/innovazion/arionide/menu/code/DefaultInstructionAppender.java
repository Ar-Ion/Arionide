package ch.innovazion.arionide.menu.code;

import java.util.Arrays;
import java.util.List;

import ch.innovazion.arionide.lang.Instruction;
import ch.innovazion.arionide.lang.Language;
import ch.innovazion.arionide.lang.LanguageManager;
import ch.innovazion.arionide.menu.MenuManager;

public class DefaultInstructionAppender extends InstructionAppender {

	public DefaultInstructionAppender(MenuManager manager) {
		super(manager, "<Operator>");
	}

	protected List<Instruction> getInstructions() {
		Language lang = LanguageManager.get(target.getLanguage());

		if(lang != null) {
			return lang.getInstructions();
		} else {
			return Arrays.asList();
		}
	}

	public void onAction(String action) {
		if(id > 0) {
			appendInstruction(id - 1);
		} else {
			go("operator");
		}
	}
}
