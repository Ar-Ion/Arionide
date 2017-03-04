/*******************************************************************************
 * This file is part of ArionIDE.
 *
 * ArionIDE is an IDE whose purpose is to build a language from assembly. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
 *
 * ArionIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArionIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with ArionIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the JAR archive or in your personal directory as 'arionide/LICENSE.txt'.
 *******************************************************************************/
package org.azentreprise.ui;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.Performances;
import org.azentreprise.Projects;
import org.azentreprise.ui.animations.Animation;
import org.azentreprise.ui.animations.FieldModifierAnimation;
import org.azentreprise.ui.views.UIMainView;
import org.azentreprise.ui.views.UINullView;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.UIProjectView;

public class UIMain extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static UIMain singleton;
	
	public static boolean repairRequested = false;
		
	private UIView view = new UINullView(null, null);
	private UIView auxView = null;
	private float viewAlpha = 0.0f;
	private float auxViewAlpha = 0.0f;
	
	private final Animation viewTransition = new FieldModifierAnimation("viewAlpha", this.getClass(), this);
	private final Animation auxViewTransition = new FieldModifierAnimation("auxViewAlpha", this.getClass(), this);

	public UIMain() {
		Rectangle size = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		JFrame frame = new JFrame(Arionide.getClientIdentifier());
		frame.setSize((int) (size.width * 0.8), (int) (size.height * 0.8));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent event) {
				Arionide.shutdown();
			}
		});
		frame.setLocationRelativeTo(null);
		frame.setContentPane(this);
		frame.setVisible(true);
				
		UIMain.singleton = this;
		
		UIEvents.addListeners(frame);
		
		this.setFocusTraversalKeysEnabled(false);
		this.createKeyStroke("F4", "toggle_debug");
		this.createKeyStroke("F5", "refresh");
		this.createKeyStroke("F6", "repair");
		this.createKeyStroke("ENTER", "action");
		this.createKeyStroke("ESCAPE", "back");
		this.createKeyStroke("TAB", "next_focus");
		this.createKeyStroke("shift TAB", "prev_focus");

		UIMainView.updatePageID();
		this.showView(new UIMainView(this.view, frame), true);
	}
	
	private void createKeyStroke(String keyStroke, String identifier) {
		this.getInputMap().put(KeyStroke.getKeyStroke(keyStroke), identifier);
		this.getActionMap().put(identifier, new KeyStrokeHandler(this, identifier));
	}
	
	public void paintComponent(Graphics g) {
		try {						
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			
			g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			if(this.viewAlpha < 1.0f) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.viewAlpha));
			}
			
			if(this.view != null) {
				this.view.drawView(g2d);
			}
			
			if(this.auxView != null && this.auxViewAlpha > 0.0f) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.auxViewAlpha));
				this.auxView.drawView(g2d);
			}
			
			Performances.repaint();
			
			this.repaint();
			
		} catch(Exception exception) {
			Debug.taskBegin("(re)painting the user interface");
			Debug.exception(exception);
			Debug.taskEnd();
			Arionide.shutdown();
		}
	}
	
	public void showView(UIView view, boolean transition) {
		if(this.view != view) {
			if(!(this.view instanceof UIProjectView) && view instanceof UIProjectView) {
				UIProjectView.simpleRenderer.reset(Projects.getCurrentProject());
				UIProjectView.complexRenderer.reset(Projects.getCurrentProject());
			}
			
			if(!(view instanceof UINullView)) {
				this.showView0(view, transition);
			}
		}
	}
	
	private void showView0(UIView view, boolean transition) {
		UIEvents.unregisterSubHandlers(this.view);
		try { this.removeKeyListener((KeyListener) this.view); } catch(Exception e) {}
		try { this.view.getRootComponent().removeMouseListener((MouseListener) this.view); } catch(Exception e) {}
		try { this.view.getRootComponent().removeMouseMotionListener((MouseMotionListener) this.view); } catch(Exception e) {}
		try { this.view.getRootComponent().removeMouseWheelListener((MouseWheelListener) this.view); } catch(Exception e) {}

		if(this.viewTransition != null)  {
			this.viewTransition.stopAnimation();
		}
		
		if(this.auxViewTransition != null) {
			this.auxViewTransition.stopAnimation();
		}
		
		if(transition) {
			if(this.view instanceof UINullView) {
				this.view = view;
				this.viewTransition.startAnimation(30, 1.0f);
			} else {
				this.auxView = this.view;
				this.view = view;
				this.auxViewAlpha = 1.0f;
				this.viewAlpha = 0.0f;
				
				this.viewTransition.startAnimation(30, 1.0f);
				this.auxViewTransition.startAnimation(30, 0.0f);
			}
		} else {
			this.view = view;
		}
	}
	
	public static void back(UIView view) {
		if(!(view.getParentView() instanceof UINullView)) {
			UIEvents.unregisterSubHandlers(view);
			
			try { 
				UIMain.singleton.removeKeyListener((KeyListener) view);
				
				view.getRootComponent().removeMouseListener((MouseListener) view);
				view.getRootComponent().removeMouseMotionListener((MouseMotionListener) view);
				view.getRootComponent().removeMouseWheelListener((MouseWheelListener) view);
			} catch(Exception e) {
				;
			}
			
			UIView parentView = view.getParentView();
			
			try {
				UIMain.show(parentView.getClass().getDeclaredConstructor(UIView.class, Frame.class).newInstance(parentView.getParentView(), parentView.getRootComponent()));
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
	}
	
	private synchronized void refreshView() throws Exception {
		Thread[] buffer = new Thread[128];
		
		int count = Thread.enumerate(buffer);
		
		for(int i = 0; i < count; i++) {
			if(buffer[i].getName().contains("Arionide")) {
				buffer[i].interrupt();
			} else if(buffer[i] == null) {
				break;
			}
		}
		
		this.view.getClass().getDeclaredConstructor(UIView.class, Frame.class).newInstance(this.view.getParentView(), this.view.getRootComponent());
	}
	
	public static synchronized void setFrameCursor(Cursor cursor) {
		UIMain.singleton.setCursor(cursor);
	}
	
	public static synchronized void show(UIView view) {
		UIMain.singleton.showView(view, true);
	}

	public static void init() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new UIMain();
			}
		});
	}
	
	private static strictfp final class KeyStrokeHandler extends AbstractAction {
		
		private static final long serialVersionUID = 1L;
		private final UIMain instance;
		private final String identifier;
		private final Thread debug;
		private final JFrame frame;
		
		private boolean debugging = false;
		
		private KeyStrokeHandler(UIMain instance, String identifier) {
			this.instance = instance;
			this.identifier = identifier;
			this.frame = (JFrame) this.instance.getTopLevelAncestor();
			this.debug = new Thread() {
				public void run() {
					while(!this.isInterrupted()) {
						KeyStrokeHandler.this.frame.setTitle(Arionide.getClientIdentifier() + " - " + Performances.getPerfs());
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							;
						}
					}
				}
			};
		}
		
		public void actionPerformed(ActionEvent event) {
			try {
				switch(this.identifier) {
					case "refresh":
						Debug.info("Refreshing...");
						this.instance.refreshView();
						break;
					case "repair":
						JOptionPane.showMessageDialog(this.frame, "A repair prompt will show up if you open a project.");
						UIMain.repairRequested = true;
						break;
					case "toggle_debug":
						this.toggleDebug();
						break;
					case "next_focus":
						this.instance.view.nextFocus();
						break;
					case "prev_focus":
						this.instance.view.prevFocus();
						break;
					case "action":
						this.instance.view.action();
						break;
					case "back":
						if(this.instance.view.flagNoFocus()) {
							UIMain.back(this.instance.view);
						}
				}
			} catch (Exception exception) {
				Debug.exception(exception);
			}
		}
		
		public void toggleDebug() {
			this.debugging = !this.debugging;
			
			if(this.debugging) {
				this.frame.setTitle(Arionide.getClientIdentifier() + " - Loading debug data...");
				this.debug.start();
			} else {
				this.frame.setTitle(Arionide.getClientIdentifier());
				this.debug.interrupt();
			}
		}
	}
}