package ch.innovazion.arionide.menu.code;

import ch.innovazion.arionide.menu.Menu;

public class CoreCodeBrowser extends CodeBrowser {
	
	private final CodeEditor editor;
	
	public CoreCodeBrowser(Menu parent) {
		super(parent);
		this.editor = new CodeEditor(this);
	}
	
	protected void onClick(int index) {	
		super.onClick(index);
		editor.show();
	}
}
