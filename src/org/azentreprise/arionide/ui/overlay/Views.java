package org.azentreprise.arionide.ui.overlay;

import java.util.ArrayList;
import java.util.List;

import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.views.MainView;
import org.azentreprise.arionide.ui.overlay.views.NewProjectView;

public class Views {
	
	public static View main;
	public static View newProject;
	
	public static List<View> all = new ArrayList<>();
	
	public static void init(AppManager appManager, LayoutManager layoutManager) {
		assert Views.all.isEmpty();
		
		Views.all.add(Views.main = new MainView(appManager, layoutManager));
		Views.all.add(Views.newProject = new NewProjectView(appManager, layoutManager));
	}
}