package ch.innovazion.arionide.menu.structure;

import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.menu.MenuManager;
import ch.innovazion.arionide.project.Structure;
import ch.innovazion.arionide.ui.overlay.View;
import ch.innovazion.arionide.ui.overlay.Views;
import ch.innovazion.automaton.Export;
import ch.innovazion.automaton.Inherit;

public class StructureEditor extends Menu {
	
	@Export
	@Inherit
	protected Structure target;

	public StructureEditor(MenuManager manager) {
		super(manager, "Specify", "Comment", "Rename", "Abstractify", "Language", "Tint", "Delete");
	}

	public void onAction(String action) {
		switch(action) {
		case "Rename":
			Views.input.setText("Please enter the new name of the new structure")
					   .setPlaceholder("Structure name")
					   .setResponder(this::rename)
					   .stackOnto(Views.code);
			
			break;
		case "Abstractify":
			Views.confirm.setPrimaryText("Are you sure you want to abstractify this structure?")
						 .setSecondaryText("All the code inside the structure will be deleted")
					     .setButtons("Yes", "Cancel")
					     .react("Yes", this::abstractify)
					     .react("Cancel", View::discard)
					     .stackOnto(Views.code);
			
			break;
		case "Delete":
			Views.confirm.setPrimaryText("Are you sure you want to delete this structure?")
						 .setSecondaryText("Everything inside the structure will be deleted")
					     .setButtons("Yes", "Cancel")
					     .react("Yes", this::delete)
					     .react("Cancel", View::discard)
					     .stackOnto(Views.code);
			
			break;
		default:
			go(action.toLowerCase());
		}
	}
	
	private void rename(String name) {
		project.getStructureManager().rename(target.getIdentifier(), name);
	}
	
	private void abstractify(View nil) {
		project.getStructureManager().abstractifyStructure(target.getIdentifier());
	}
	
	private void delete(View nil) {
		project.getStructureManager().deleteStructure(target.getIdentifier());
	}
}
