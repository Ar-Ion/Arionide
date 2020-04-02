package ch.innovazion.arionide.menu;

public class ErrorMenu extends Menu {

	protected ErrorMenu(MenuManager manager, String errorMessage) {
		super(manager, errorMessage);
		this.description = new MenuDescription("An internal error occured");
	}

	public void onAction(String action) {
		go("..");
	}
}
