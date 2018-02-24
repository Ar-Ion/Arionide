/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2018 AZEntreprise Corporation. All rights reserved.
 *
 * Arionide is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arionide is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Arionide.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The copy of the GNU General Public License can be found in the 'LICENSE.txt' file inside the src directory or inside the JAR archive.
 *******************************************************************************/
package org.azentreprise.ui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class SharedFramebuffer {
	private final int width;
	private final int height;
	private final int widthX2;
	private final int heightX2;
	private final int[] directBuffer;
	
	private BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	private int[] buffer;
	
	public SharedFramebuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.widthX2 = width * 2;
		this.heightX2 = height * 2;
		
		this.directBuffer = new int[width * height * 4];
	}
	
	public void draw(int x, int y, int color) {
		if(x > -this.width && y > -this.height && x < this.width && y < this.height) {
			int selector = this.width + x + (this.height + y) * this.width * 2;
			if(((color >> 24) & 0xFF) >= ((this.directBuffer[selector] >> 24) & 0xFF)) {
				this.directBuffer[selector] = color;
			}
		}
	}
	
	public void apply(Graphics2D g2d, int x, int y) {
		int width = (int) g2d.getClipBounds().getWidth();
		int height = (int) g2d.getClipBounds().getHeight();
		
		x -= this.width;
		y -= this.height;
				
		if(x < width - this.widthX2) {
			x = width - this.widthX2;
		} else if(x > 0) {
			x = 0;
		}
		
		if(y < height - this.heightX2) {
			y = height - this.heightX2;
		} else if(y > 0) {
			y = 0;
		}
		
		this.updateBuffer(width, height);
		
		int semiSelector = this.widthX2 * y + x;
				
		for(int i = 0; i < height; i++) {
			System.arraycopy(this.directBuffer, this.widthX2 * i - semiSelector, this.buffer, i * width, width);
		}
		
		g2d.drawImage(this.image, 0, 0, width, height, null);
	}
	
	private void updateBuffer(int width, int height) {
		if(this.image.getWidth() != width || this.image.getHeight() != height) {
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			this.buffer = ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData();
		}
	}

	public void clear() {
		if(this.buffer != null) {
			Arrays.fill(this.buffer, 0);
		}
		
		if(this.directBuffer != null) {
			Arrays.fill(this.directBuffer, 0);
		}
	}
}
