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

import ch.innovazion.arionide.menu.Menu;

public class CoreCodeBrowser extends CodeBrowser {
	
	private final CodeEditor editor;
	
	public CoreCodeBrowser(Menu parent) {
		super(parent);
		this.editor = new CodeEditor(this);
	}
	
	protected void onClick(int index) {	
		super.onClick(index);
		editor.show();
	}
}
