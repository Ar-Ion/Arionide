package ch.innovazion.arionide.menu;

import java.util.List;

import ch.innovazion.arionide.lang.SpecificationElement;
import ch.innovazion.arionide.project.StructureMeta;
import ch.innovazion.arionide.ui.ApplicationTints;

public class DisplayUtils {
	public static MenuDescription fullDescription(StructureMeta meta) {
		MenuDescription description = new MenuDescription(ApplicationTints.MENU_INFO_INACTIVE_COLOR, 1.0f);
		List<SpecificationElement> elements = meta.getSpecification().getElements();

		String name = meta.getName();
		
		if(!meta.getComment().equals("?")) {
			name += " (" + meta.getComment() + ")";
		}
		
		description.add(name);
		
		for(SpecificationElement element : elements) {
			description.add(element.toString());
		}

		description.setHighlight(0);
		description.setColor(0, ApplicationTints.MENU_INFO_DEFAULT_COLOR);
		
		return description;
	}
}
