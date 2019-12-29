package ch.innovazion.arionide.ui.overlay.views;

import java.util.function.Consumer;

import ch.innovazion.arionide.ui.AppManager;
import ch.innovazion.arionide.ui.layout.LayoutManager;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.components.Input;
import ch.innovazion.arionide.ui.overlay.components.Label;

public class InputPromptView extends PromptView {
	
	private final Label label;
	private final Input input;
	
	private Consumer<String> responder = (nil) -> {};
	
	public InputPromptView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager, 2);
		
		setButtons("OK", "Cancel");
		react("Cancel", View::discard);
		react("Ok", this::onInputValidated);
		
		this.add(this.label = new Label(this, ""), 0.1f, 0.1f, 0.9f, 0.25f);
		this.add(this.input = new Input(this, ""), 0.1f, 0.3f, 0.9f, 0.65f);
	}
	
	public void viewWillAppear() {				
		super.viewWillAppear();
		getAppManager().getFocusManager().request(input);
	}
	
	private void onInputValidated(View nil) {
		String text = input.toString();
		
		if(text != null && !text.isEmpty()) {
			responder.accept(text);
		}
	}

	public InputPromptView setText(String text) {
		label.setLabel(text);
		return this;
	}
	
	public InputPromptView setResponder(Consumer<String> responder) {
		this.responder = responder;
		return this;
	}
}
