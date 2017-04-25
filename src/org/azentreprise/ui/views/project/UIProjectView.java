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
package org.azentreprise.ui.views.project;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.azentreprise.Arionide;
import org.azentreprise.Debug;
import org.azentreprise.Projects;
import org.azentreprise.configuration.Project;
import org.azentreprise.lang.Object;
import org.azentreprise.lang.ObjectType;
import org.azentreprise.ui.UIEvents;
import org.azentreprise.ui.UIMain;
import org.azentreprise.ui.animations.Animation;
import org.azentreprise.ui.animations.FieldModifierAnimation;
import org.azentreprise.ui.components.UIButton;
import org.azentreprise.ui.components.UIButton.UIButtonClickListener;
import org.azentreprise.ui.components.UILabel;
import org.azentreprise.ui.render.ComplexProjectRenderer;
import org.azentreprise.ui.render.ProjectRenderer;
import org.azentreprise.ui.render.RoundRectRenderer;
import org.azentreprise.ui.render.SimpleProjectRenderer;
import org.azentreprise.ui.views.UIView;
import org.azentreprise.ui.views.project.editors.UIGlobalVariablesEditorProjectView;

public abstract class UIProjectView extends UIView implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, UIButtonClickListener {
	
	private static Map<String, Point2D> selected = new HashMap<String, Point2D>();
	
	private final Animation selectionAnimation = new FieldModifierAnimation("selectionOpacity", UIProjectView.class, this);
	private final Animation errorMessageAnimation = new FieldModifierAnimation("errorMessageOpacity", UIProjectView.class, this);
	private final Animation overlayAnimation = new FieldModifierAnimation("overlayOpacity", UIProjectView.class, this);
	
	protected final Project project = Projects.getCurrentProject();
	
	private Point selectionStart;
	private Point currentMousePosition;
	private int selectionOpacity = 0;
	private int errorMessageOpacity = 0;
	private int overlayOpacity = 0;
	private String errorMessage = new String();
	private Shape controls;
	private Rectangle bounds;
	private Map<Point2D, Color> highlighted = new HashMap<>();
	private Shape miniGrid;
	private float xMult, yMult;
	private boolean isMovingUsingMinigrid = false;
	private boolean needValidation = false;
	
	public static final ProjectRenderer simpleRenderer = new SimpleProjectRenderer(Projects.getCurrentProject());
	public static final ProjectRenderer complexRenderer = new ComplexProjectRenderer(Projects.getCurrentProject());

	public UIProjectView(UIView parent, Frame rootComponent) {
		super(parent, rootComponent, 0, 0, 1, 1);
		
		rootComponent.addMouseListener(this);
		rootComponent.addMouseMotionListener(this);
		rootComponent.addMouseWheelListener(this);
		((JFrame) rootComponent).getContentPane().addKeyListener(this);
				
		this.add(new UIButton(this, 0.72f, 0.73f, 0.77f, 0.92f, "<").setHandler(this, "back"));
	}
	
	public void draw(Graphics2D g2d) {
		this.bounds = g2d.getClipBounds();
		
		this.centerGridPosition(this.bounds.width, this.bounds.height);
		
		UIProjectView.complexRenderer.render(g2d);
		
		if(this.selectionStart != null && this.currentMousePosition != null) {
			int x = this.selectionStart.x, y = this.selectionStart.y;
			int w = this.currentMousePosition.x - x, h = this.currentMousePosition.y - y;
			
			Shape selection = new RoundRectangle2D.Float(w > 0 ? x : x + w, h > 0 ? y : y + h, Math.abs(w), Math.abs(h), 15, 15);
			
			g2d.setColor(new Color(0xC0, 0xFF, 0xEE, this.selectionOpacity));
			g2d.fill(selection);
			
			g2d.setColor(new Color(255, 255, 255, this.selectionOpacity * 4));
			g2d.draw(selection);
		}
		
		g2d.setColor(new Color(0x42C0FFEE, true));
						
		for(Point2D point : selected.values()) {
			g2d.fillRect(this.project.gridPosX + (int) (point.getX() * this.project.pixelsPerCell), this.project.gridPosY + (int) (point.getY() * this.project.pixelsPerCell), this.project.pixelsPerCell, this.project.pixelsPerCell);
		}
		
		for(Entry<Point2D, Color> entry : this.highlighted.entrySet()) {
			g2d.setColor(entry.getValue());
			g2d.fillRect(this.project.gridPosX + (int) (entry.getKey().getX() * this.project.pixelsPerCell), this.project.gridPosY + (int) (entry.getKey().getY() * this.project.pixelsPerCell), this.project.pixelsPerCell, this.project.pixelsPerCell);
		}
		
		for(Object object : this.project.objects) {
			if(object.shouldBeRendered()) {
				double x = this.project.gridPosX + object.getPosition(1, 0).getX() * this.project.pixelsPerCell;
				double y = this.project.gridPosY + object.getPosition(1, 0).getY() * this.project.pixelsPerCell;
				
				if(this.bounds.contains(new Point2D.Double(x + this.project.pixelsPerCell / 2.0D, y + this.project.pixelsPerCell / 2.0D))) {
					new UILabel(this, 0.0f, 0.0f, 1.0f, 1.0f, object.getName()).setColor(0x80C0FFEE).draw((Graphics2D) g2d.create((int) x + 10, (int) y + 10, this.project.pixelsPerCell - 20, this.project.pixelsPerCell - 20));					
				}
			}
		}
		
		g2d.setColor(new Color(0, 0, 0, this.overlayOpacity));
		g2d.fillRect(0, 0, this.bounds.width, this.bounds.height);
		
		this.controls = new RoundRectangle2D.Float(this.bounds.width * 0.7f, this.bounds.height * 0.05f, this.bounds.width * 0.25f, this.bounds.height * 0.9f, 15, 15);
		
		g2d.setColor(new Color(0, 0, 0, 127));
		RoundRectRenderer.fill(g2d, this.controls.getBounds());
		g2d.setColor(new Color(0xCAFE));
		RoundRectRenderer.draw(g2d, this.controls.getBounds());
		g2d.drawLine((int) (this.bounds.width * 0.7f), (int) (this.bounds.height * 0.7f), (int) (this.bounds.width * 0.95f), (int) (this.bounds.height * 0.7f));
		
		g2d.setFont(Arionide.getSystemFont());
		g2d.setColor(new Color(255, 0, 0, this.errorMessageOpacity));
		g2d.drawString(this.errorMessage, 20, 30);
		
		this.miniGrid = new Rectangle2D.Float(this.bounds.width * 0.79f, this.bounds.height * 0.73f, this.bounds.width * 0.14f, this.bounds.height * 0.19f);
		
		g2d.setColor(new Color(0, 0, 0, 127));
		g2d.fill(this.miniGrid);
		g2d.setColor(new Color(0xCAFE));
		g2d.draw(this.miniGrid);
		
		int width = this.miniGrid.getBounds().width;
		int height = this.miniGrid.getBounds().height;
		
		Graphics2D miniGfx = (Graphics2D) g2d.create(this.miniGrid.getBounds().x, this.miniGrid.getBounds().y, width, height);
		
		this.xMult = (this.project.gridSize * this.project.pixelsPerCell) / (float) width;
		this.yMult = (this.project.gridSize * this.project.pixelsPerCell) / (float) height;
				
		UIProjectView.simpleRenderer.render((Graphics2D) miniGfx.create());
		
		Shape proj = new Rectangle2D.Double(width / 2.0D - this.project.gridPosX / this.xMult, height / 2.0D - this.project.gridPosY / this.yMult, this.bounds.getWidth() / this.xMult, this.bounds.getHeight() / this.yMult);
		g2d.setColor(new Color(0xCAFE));
		miniGfx.draw(proj);
	}
	
	private void centerGridPosition(int width, int height) {
		if(!this.project.isGridInitialized) {
			this.project.isGridInitialized = true;
			this.project.gridPosX = width / 2 - this.project.pixelsPerCell / 2;
			this.project.gridPosY = height / 2 - this.project.pixelsPerCell / 2;
		}
	}
	
	protected void error(String message) {
		this.errorMessage = message;
		this.errorMessageAnimation.startAnimation(1, animation -> {
			this.errorMessageAnimation.startAnimation(100, 0);
		}, 255);
	}
	
	protected Map<String, Point2D> getSelection() {
		return selected;
	}
	
	private void select(Point selectionEnd) {
		int x1 = (int) Math.round((float) (this.selectionStart.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
		int y1 = (int) Math.round((float) (this.selectionStart.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);
		int x2 = (int) Math.round((float) (selectionEnd.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
		int y2 = (int) Math.round((float) (selectionEnd.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);

		Rectangle selection = new Rectangle((x1 < x2 ? x1 : x2), (y1 < y2 ? y1 : y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
				
		for(Object object : this.project.objects) {
			if(object.shouldBeRendered()) {			
				if(selection.contains(object.getPosition(1, 0))) {
					this.select(object.getID(), object.getPosition(1, 0));
				}
			}
		}
		
		this.resetSelection();
	}
	
	private void select(String id, Point2D position) {
		if(selected.containsKey(id)) {
			selected.remove(id);
		} else {
			selected.put(id, position);
		}
		
		if(this instanceof UISelectionProjectView) {
			if(selected.size() == 0) {
				UIMain.show(new UICodeProjectView(this.getParentView(), this.getRootComponent()));
			}
		} else {
			if(selected.size() > 0) {
				
				Object object = this.project.objectMapping.get(this.project.current);
				
				if(object != null && object.isMutable()) {
					UIMain.show(new UISelectionProjectView(this, this.getRootComponent()));
				} else {
					this.error("This object is immutable");
					selected.remove(id);
				}
			}
		}
	}
	
	protected Map<Point2D, Color> accessHighlighting() {
		return this.highlighted;
	}
	
	private void open(String id) {
		if(id == "" || this.project.objectMapping.get(id).getType().equals(ObjectType.STRUCTURE)) {
			Debug.info("The current branch is '" + (id != "" ? id : "root") + "'");
			selected.clear();
			
			this.project.current = id;
			this.project.isGridInitialized = false;
			
			Object.uploadCurrentID(id);
			
			UIProjectView.complexRenderer.reset(this.project);
			UIProjectView.simpleRenderer.reset(this.project);
			
			this.highlighted.clear();
		} else {
			this.error("This object is atomic");
		}
	}
	
	private void resetSelection() {
		if(this.selectionOpacity > 0) {
			this.selectionAnimation.startAnimation(20, after-> {
				this.selectionStart = null;
				this.currentMousePosition = null;
			}, 0);
		}
	}
	
	private void checkGridPosition() {
		int halfSize = (this.project.gridSize * this.project.pixelsPerCell) / 2;
		
		if(this.project.gridPosX > halfSize) {
			this.project.gridPosX = halfSize;
		} else if(this.project.gridPosX < this.bounds.width - halfSize) {
			this.project.gridPosX = this.bounds.width - halfSize;
		}
		
		if(this.project.gridPosY > halfSize) {
			this.project.gridPosY = halfSize;
		} else if(this.project.gridPosY < this.bounds.height - halfSize) {
			this.project.gridPosY = this.bounds.height - halfSize;
		}
	}
	
	protected void setNeedValidationFlag() {
		this.needValidation = true;
		this.overlayAnimation.startAnimation(30, 127);
	}
	
	protected void clearNeedValidationFlag() {
		this.needValidation = false;
		this.overlayAnimation.startAnimation(30, 0);
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		;
	}

	public void mouseDragged(MouseEvent event) {
		Point point = event.getPoint();
		
		UIEvents.transform(point);
		
		if(!this.needValidation && this.currentMousePosition != null && this.selectionStart == null && SwingUtilities.isLeftMouseButton(event)) {
			this.project.gridPosX += (this.currentMousePosition.x - point.getX()) * (this.isMovingUsingMinigrid ? this.xMult : -1);
			this.project.gridPosY += (this.currentMousePosition.y - point.getY()) * (this.isMovingUsingMinigrid ? this.yMult : -1);
			this.checkGridPosition();
		}
		
		this.currentMousePosition = point;
	}

	public void mouseMoved(MouseEvent event) {
		;
	}

	public void mouseEntered(MouseEvent event) {
		;
	}
	
	public void mouseClicked(MouseEvent event) {
		;
	}

	public void mouseExited(MouseEvent event) {
		this.resetSelection();
	}

	public void mousePressed(MouseEvent event) {
		Point point = event.getPoint();
				
		UIEvents.transform(point);
		
		if(this.needValidation) return;
		
		this.currentMousePosition = null;

		if(this.controls != null && !this.controls.contains(point)) {
			if(event.getButton() == MouseEvent.BUTTON3) {
				if(this.selectionAnimation != null) {
					this.selectionAnimation.stopAnimation();
				}
				
				this.selectionOpacity = 63;
				this.selectionStart = point;
			} else if(event.getButton() == MouseEvent.BUTTON1) {
				this.isMovingUsingMinigrid = false;
				this.currentMousePosition = point;
				
				int x = (int) Math.round((float) (point.getX() - this.project.gridPosX) / this.project.pixelsPerCell - 0.5f);
				int y = (int) Math.round((float) (point.getY() - this.project.gridPosY) / this.project.pixelsPerCell - 0.5f);			
				
				for(Entry<String, Object> object : this.project.objectMapping.entrySet()) {
					if(object.getValue().shouldBeRendered() && object.getValue().getPosition(1, 0).equals(new Point2D.Double(x, y))) {
						this.open(object.getKey());
						break;
					}
				}
			}
		} else if(this.miniGrid != null && this.miniGrid.contains(point) && event.getButton() == MouseEvent.BUTTON1) {
			this.isMovingUsingMinigrid = true;
			this.currentMousePosition = point;
			
			int xOrigin = (int) this.miniGrid.getBounds2D().getCenterX();
			int yOrigin = (int) this.miniGrid.getBounds2D().getCenterY();
			
			this.project.gridPosX = (int) (-this.xMult * (point.getX() - xOrigin) + this.bounds.width / 2);
			this.project.gridPosY = (int) (-this.yMult * (point.getY() - yOrigin) + this.bounds.height / 2);
			this.checkGridPosition();
		} else {
			this.currentMousePosition = null;
		}
	}

	public void mouseReleased(MouseEvent event) {
		if(!this.needValidation && this.selectionStart != null) {
			Point point = event.getPoint();
			UIEvents.transform(point);
			this.select(point);
		}
	} 

	public void keyPressed(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_CONTROL) {			
			UIMain.show(new UIMiscProjectView(this, this.getRootComponent()));
		} else if(event.getKeyCode() == KeyEvent.VK_ALT) {			
			UIMain.show(new UIDesignProjectView(this, this.getRootComponent()));
		} else if(event.getKeyCode() == KeyEvent.VK_G) {
			UIMain.show(new UIGlobalVariablesEditorProjectView(this, this.getRootComponent()));
		}
	}
	
	public void keyReleased(KeyEvent event) {
		;
	}

	public void keyTyped(KeyEvent event) {
		;
	}
	
	public void unregisterListeners() {
		this.getRootComponent().removeMouseListener(this);
		this.getRootComponent().removeMouseMotionListener(this);
		this.getRootComponent().removeMouseWheelListener(this);
		((JFrame) this.getRootComponent()).getContentPane().removeKeyListener(this);
	}
	
	public void onClick(java.lang.Object... signals) {
		if(signals[0] instanceof String && !this.needValidation)  {
			switch((String) signals[0]) {
				case "back":
					if(this.project.current.indexOf('.') > -1) {
						this.open(this.project.current.substring(0, this.project.current.lastIndexOf('.')));
					} else {
						this.open("");
					}
					break;
			}
		}
	}
}
