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
package ch.innovazion.arionide.menu.structure;

import ch.innovazion.arionide.menu.Menu;
import ch.innovazion.arionide.ui.AppManager;

public class CoreStructureBrowser extends StructureBrowser {

	private final Menu editor;
	
	public CoreStructureBrowser(AppManager manager) {
		super(manager);
		this.editor = new StructureEditor(this);
	}

	public void onClick(int index) {
		super.onClick(index);
		editor.show();
	}
}