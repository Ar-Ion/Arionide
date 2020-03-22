package ch.innovazion.arionide.menu;

public class RootMenu extends Menu {
	
	public static final String structureBrowser = "loadStructureBrowser";
	public static final String codeBrowser = "loadCodeBrowser";
	
	protected RootMenu(MenuManager manager) {
		super(manager, "Loading menu...");
	}

	public void onAction(String action) {
		switch(action) {
		case structureBrowser:
			go("structure");
			break;
		case codeBrowser:
			go("code");
			break;
		}
	}
}
