package org.azentreprise.arionide.ui.overlay.views;

import java.awt.Color;
import java.awt.Graphics2D;

import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.layout.LayoutManager;
import org.azentreprise.arionide.ui.overlay.View;
import org.azentreprise.arionide.ui.overlay.components.Button;
import org.azentreprise.arionide.ui.overlay.components.ClickListener;
import org.azentreprise.arionide.ui.overlay.components.Label;
import org.azentreprise.arionide.ui.overlay.components.Text;

public class NewProjectView extends View implements ClickListener {
	public NewProjectView(AppManager appManager, LayoutManager layoutManager) {
		super(appManager, layoutManager);
		
		this.setBackgroundColor(new Color(63, 63, 63, 127));
		this.setBorderColor(new Color(0xCAFE));
				
		this.add(new Label(this, "New project"), 0, 0.05f, 1.0f, 0.2f);
		
		this.add(new Text(this, "Project name"), 0.1f, 0.3f, 0.9f, 0.4f);
		this.add(new Text(this, "Owner"), 0.1f, 0.45f, 0.9f, 0.55f);
		this.add(new Text(this, "Description"), 0.1f, 0.60f, 0.9f, 0.70f);
		
		this.add(new Button(this, "Create").setHandler(this, "create"), 0.1f, 0.8f, 0.45f, 0.9f);
		this.add(new Button(this, "Cancel").setHandler(this, "cancel"), 0.55f, 0.8f, 0.9f, 0.9f);
		
		this.setFocus(1);
	}
	
	public void drawSurface(Graphics2D g2d) {
	}

	public void onClick(Object... signals) {
		switch((String) signals[0]) {
			case "cancel":
				break;
			case "create":
				/*String[] fields = UIText.fetchUserData(this);
								
				if(fields[0].length() > 0 && fields[1].length() > 0 && fields[2].length() > 0) {
					try {
						Project project = Projects.createProject(fields[0]);
						
						project.name = fields[0];
						project.owner = fields[1];
						project.description = fields[2];
						
						project.save();
						
						UIMainView.updatePageID();
						
						//UIMain.show(new UICodeProjectView(this, this.getRootComponent()));
					} catch (Exception exception) {
						Debug.exception(exception);
					}
				}*/
				
				break;
		}
	}
}