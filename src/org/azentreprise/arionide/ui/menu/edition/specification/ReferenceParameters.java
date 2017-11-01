package org.azentreprise.arionide.ui.menu.edition.specification;

import java.util.List;

import javax.swing.JOptionPane;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.lang.Data;
import org.azentreprise.arionide.lang.SpecificationElement;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.menu.Menu;

public class ReferenceParameters extends Menu {
	
	private final Menu parent;
	private final List<Data> parameters;
	
	protected ReferenceParameters(AppManager manager, Menu parent, List<Data> parameters) {
		super(manager, "Back", "Add");
		
		this.parent = parent;
		this.parameters = parameters;
		
		for(Data data : parameters) {
			this.getElements().add(data.getName());
		}
		
		this.setMenuCursor(1);
	}
	
	public void onClick(int id) {
		if(id == 0) {
			this.parent.show();
		} else if(id == 1) {
			new Thread(() -> {
				String name = JOptionPane.showInputDialog(null, "Enter the name for the new parameter", "New parameter", JOptionPane.PLAIN_MESSAGE);
				
				if(name != null) {
					Data data = new Data(name, null, -1);
					this.parameters.add(data);
					this.getAppManager().getEventDispatcher().fire(new MessageEvent("Parameter successfully added", MessageType.SUCCESS));
				}
			}).start();
		} else {
			id -= 2;
			
			
		}
	}
}