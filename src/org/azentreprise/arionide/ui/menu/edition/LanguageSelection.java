package org.azentreprise.arionide.ui.menu.edition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.project.HierarchyElement;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;

public class LanguageSelection extends SpecificMenu {
	
	private final List<Integer> languageIDs = new ArrayList<>();

	private int current;
	
	protected LanguageSelection(AppManager manager) {
		super(manager, "<Error>");
	}
	
	public void setCurrent(WorldElement current) {
		super.setCurrent(current);
		
		Project project = this.getAppManager().getWorkspace().getCurrentProject();
		Map<Integer, StructureMeta> metaData = project.getStorage().getStructureMeta();
		
		if(project != null) {
			this.getElements().clear();
			this.languageIDs.clear();
			
			for(HierarchyElement element : project.getStorage().getHierarchy()) {
				if(element.getID() != current.getID()) {
					StructureMeta meta = metaData.get(element.getID());
					
					if(meta != null) {
						this.getElements().add(meta.getName());
						this.languageIDs.add(element.getID());
					}
				}
			}
			
			this.getElements().add("Cancel");
			
			this.current = current.getID();
		}
	}
	
	public void onClick(int element) {
		if(element < this.languageIDs.size()) {
			Project project = this.getAppManager().getWorkspace().getCurrentProject();
			
			if(project != null) {
				MessageEvent message = project.getDataManager().setLanguage(this.current, this.languageIDs.get(element));
				this.getAppManager().getEventDispatcher().fire(message);
				MainMenus.getStructureEditor().show();
			}
		} else {
			MainMenus.getStructureEditor().show();
		}
	}

	public String getDescription() {
		return "Language selection for " + super.getDescription();
	}
}