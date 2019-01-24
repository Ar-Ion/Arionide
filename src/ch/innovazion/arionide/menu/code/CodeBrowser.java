/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package ch.innovazion.arionide.menu.code;

import java.util.List;

import ch.innovazion.arionide.Utils;
import ch.innovazion.arionide.menu.Browser;
import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.project.HierarchyElement;
import ch.innovazion.arionide.ui.core.geom.WorldElement;

public abstract class CodeBrowser extends Browser {
		
	public CodeBrowser(Menu parent) {
		super(parent);
	}
	
	protected WorldElement getTarget() {
		return getAppManager().getCoreRenderer().getCodeGeometry().getElementByID(getSelectedID());
	}
	
	public List<Integer> loadCurrentElements() {
		return Utils.extract(getProject().getDataManager().getCodeManager().getCurrentCode().list(), HierarchyElement::getID);
	}
}