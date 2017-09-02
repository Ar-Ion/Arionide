package org.azentreprise.arionide.ui.menu;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;

public class ConfirmMenu extends SpecificMenu {
	
	private static final String yes = "Yes";
	private static final String no = "No";

	private final Menu parent;
	private final Confirmable confirm;
	private final String caption;
	
	protected ConfirmMenu(AppManager manager, Menu parent, Confirmable confirm, String caption) {
		super(manager, yes, no);
		this.parent = parent;
		this.confirm = confirm;
		this.caption = caption;
	}
	
	public void setCurrent(WorldElement current) {
		super.setCurrent(current);
		this.getManager().getEventDispatcher().fire(new MessageEvent(this.caption, MessageType.INFO));
	}
	
	public void onClick(String element) {
		this.show(this.parent);

		if(element.equals(yes)) {
			this.confirm.confirm();
		}
	}
}