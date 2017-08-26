/*******************************************************************************
 * This file is part of Arionide.
 *
 * Arionide is an IDE whose purpose is to build a language from scratch. It is the work of Arion Zimmermann in context of his TM.
 * Copyright (C) 2017 AZEntreprise Corporation. All rights reserved.
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
package org.azentreprise.arionide.ui.menu;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Coloring extends Menu {
	
	private final HashMap<String, Vector3f> colors = new LinkedHashMap<>();
	private WorldElement current;
	
	public Coloring(AppManager manager) {
		super(manager);

		this.addColor("Red", 0xED0A3F);
		this.addColor("Maroon", 0xC32148);
		this.addColor("Scarlet", 0xFD0E35);
		this.addColor("Brick Red", 0xC62D42);
		this.addColor("English Vermilion", 0xCC474B);
		this.addColor("Madder Lake", 0xCC3336);
		this.addColor("Permanent Geranium Lake", 0xE12C2C);
		this.addColor("Maximum Red", 0xD92121);
		this.addColor("Indian Red", 0xB94E48);
		this.addColor("Orange-Red", 0xFF5349);
		this.addColor("Sunset Orange", 0xFE4C40);
		this.addColor("Bittersweet", 0xFE6F5E);
		this.addColor("Dark Venetian Red", 0xB33B24);
		this.addColor("Venetian Red", 0xCC553D);
		this.addColor("Light Venetian Red", 0xE6735C);
		this.addColor("Vivid Tangerine", 0xFF9980);
		this.addColor("Middle Red", 0xE58E73);
		this.addColor("Burnt Orange", 0xFF7F49);
		this.addColor("Red-Orange", 0xFF681F);
		this.addColor("Orange", 0xFF8833);
		this.addColor("Macaroni and Cheese", 0xFFB97B);
		this.addColor("Middle Yellow Red", 0xECB176);
		this.addColor("Mango Tango", 0xE77200);
		this.addColor("Yellow-Orange", 0xFFAE42);
		this.addColor("Maximum Yellow Red", 0xF2BA49);
		this.addColor("Banana Mania", 0xFBE7B2);
		this.addColor("Maize", 0xF2C649);
		this.addColor("Orange-Yellow", 0xF8D568);
		this.addColor("Goldenrod", 0xFCD667);
		this.addColor("Dandelion", 0xFED85D);
		this.addColor("Yellow", 0xFBE870);
		this.addColor("Green-Yellow", 0xF1E788);
		this.addColor("Middle Yellow", 0xFFEB00);
		this.addColor("Olive Green", 0xB5B35C);
		this.addColor("Spring Green", 0xECEBBD);
		this.addColor("Maximum Yellow", 0xFAFA37);
		this.addColor("Canary", 0xFFFF99);
		this.addColor("Lemon Yellow", 0xFFFF9F);
		this.addColor("Maximum Green Yellow", 0xD9E650);
		this.addColor("Middle Green Yellow", 0xACBF60);
		this.addColor("Inchworm", 0xAFE313);
		this.addColor("Light Chrome Green", 0xBEE64B);
		this.addColor("Yellow-Green", 0xC5E17A);
		this.addColor("Maximum Green", 0x5E8C31);
		this.addColor("Asparagus", 0x7BA05B);
		this.addColor("Granny Smith Apple", 0x9DE093);
		this.addColor("Fern", 0x63B76C);
		this.addColor("Middle Green", 0x4D8C57);
		this.addColor("Green", 0x3AA655);
		this.addColor("Medium Chrome Green", 0x6CA67C);
		this.addColor("Forest Green", 0x5FA777);
		this.addColor("Sea Green", 0x93DFB8);
		this.addColor("Shamrock", 0x33CC99);
		this.addColor("Mountain Meadow", 0x1AB385);
		this.addColor("Jungle Green", 0x29AB87);
		this.addColor("Caribbean Green", 0x00CC99);
		this.addColor("Tropical Rain Forest", 0x00755E);
		this.addColor("Middle Blue Green", 0x8DD9CC);
		this.addColor("Pine Green", 0x01786F);
		this.addColor("Maximum Blue Green", 0x30BFBF);
		this.addColor("Robin's Egg Blue", 0x00CCCC);
		this.addColor("Teal Blue", 0x008080);
		this.addColor("Light Blue", 0x8FD8D8);
		this.addColor("Aquamarine", 0x95E0E8);
		this.addColor("Turquoise Blue", 0x6CDAE7);
		this.addColor("Outer Space", 0x2D383A);
		this.addColor("Sky Blue", 0x76D7EA);
		this.addColor("Middle Blue", 0x7ED4E6);
		this.addColor("Blue-Green", 0x0095B7);
		this.addColor("Pacific Blue", 0x009DC4);
		this.addColor("Cerulean", 0x02A4D3);
		this.addColor("Maximum Blue", 0x47ABCC);
		this.addColor("Blue (I)", 0x4997D0);
		this.addColor("Cerulean Blue", 0x339ACC);
		this.addColor("Cornflower", 0x93CCEA);
		this.addColor("Green-Blue", 0x2887C8);
		this.addColor("Midnight Blue", 0x00468C);
		this.addColor("Navy Blue", 0x0066CC);
		this.addColor("Denim", 0x1560BD);
		this.addColor("Blue (III)", 0x0066FF);
		this.addColor("Cadet Blue", 0xA9B2C3);
		this.addColor("Periwinkle", 0xC3CDE6);
		this.addColor("Blue (II)", 0x4570E6);
		this.addColor("Wild Blue Yonder", 0x7A89B8);
		this.addColor("Indigo", 0x4F69C6);
		this.addColor("Manatee", 0x8D90A1);
		this.addColor("Cobalt Blue", 0x8C90C8);
		this.addColor("Celestial Blue", 0x7070CC);
		this.addColor("Blue Bell", 0x9999CC);
		this.addColor("Maximum Blue Purple", 0xACACE6);
		this.addColor("Violet-Blue", 0x766EC8);
		this.addColor("Blue-Violet", 0x6456B7);
		this.addColor("Ultramarine Blue", 0x3F26BF);
		this.addColor("Middle Blue Purple", 0x8B72BE);
		this.addColor("Purple Heart", 0x652DC1);
		this.addColor("Royal Purple", 0x6B3FA0);
		this.addColor("Violet (II)", 0x8359A3);
		this.addColor("Medium Violet", 0x8F47B3);
		this.addColor("Wisteria", 0xC9A0DC);
		this.addColor("Lavender (I)", 0xBF8FCC);
		this.addColor("Vivid Violet", 0x803790);
		this.addColor("Maximum Purple", 0x733380);
		this.addColor("Purple Mountains' Majesty", 0xD6AEDD);
		this.addColor("Fuchsia", 0xC154C1);
		this.addColor("Pink Flamingo", 0xFC74FD);
		this.addColor("Violet (I)", 0x732E6C);
		this.addColor("Brilliant Rose", 0xE667CE);
		this.addColor("Orchid", 0xE29CD2);
		this.addColor("Plum", 0x8E3179);
		this.addColor("Medium Rose", 0xD96CBE);
		this.addColor("Thistle", 0xEBB0D7);
		this.addColor("Mulberry", 0xC8509B);
		this.addColor("Red-Violet", 0xBB3385);
		this.addColor("Middle Purple", 0xD982B5);
		this.addColor("Maximum Red Purple", 0xA63A79);
		this.addColor("Jazzberry Jam", 0xA50B5E);
		this.addColor("Eggplant", 0x614051);
		this.addColor("Magenta", 0xF653A6);
		this.addColor("Cerise", 0xDA3287);
		this.addColor("Wild Strawberry", 0xFF3399);
		this.addColor("Lavender (II)", 0xFBAED2);
		this.addColor("Cotton Candy", 0xFFB7D5);
		this.addColor("Carnation Pink", 0xFFA6C9);
		this.addColor("Violet-Red", 0xF7468A);
		this.addColor("Razzmatazz", 0xE30B5C);
		this.addColor("Pig Pink", 0xFDD7E4);
		this.addColor("Carmine", 0xE62E6B);
		this.addColor("Blush", 0xDB5079);
		this.addColor("Tickle Me Pink", 0xFC80A5);
		this.addColor("Mauvelous", 0xF091A9);
		this.addColor("Salmon", 0xFF91A4);
		this.addColor("Middle Red Purple", 0xA55353);
		this.addColor("Mahogany", 0xCA3435);
		this.addColor("Melon", 0xFEBAAD);
		this.addColor("Pink Sherbert", 0xF7A38E);
		this.addColor("Burnt Sienna", 0xE97451);
		this.addColor("Brown", 0xAF593E);
		this.addColor("Sepia", 0x9E5B40);
		this.addColor("Fuzzy Wuzzy", 0x87421F);
		this.addColor("Beaver", 0x926F5B);
		this.addColor("Tumbleweed", 0xDEA681);
		this.addColor("Raw Sienna", 0xD27D46);
		this.addColor("Van Dyke Brown", 0x664228);
		this.addColor("Tan", 0xD99A6C);
		this.addColor("Desert Sand", 0xEDC9AF);
		this.addColor("Peach", 0xFFCBA4);
		this.addColor("Burnt Umber", 0x805533);
		this.addColor("Apricot", 0xFDD5B1);
		this.addColor("Almond", 0xEED9C4);
		this.addColor("Raw Umber", 0x665233);
		this.addColor("Shadow", 0x837050);
		this.addColor("Raw Sienna (I)", 0xE6BC5C);
		this.addColor("Timberwolf", 0xD9D6CF);
		this.addColor("Gold (I)", 0x92926E);
		this.addColor("Gold (II)", 0xE6BE8A);
		this.addColor("Silver", 0xC9C0BB);
		this.addColor("Copper", 0xDA8A67);
		this.addColor("Antique Brass", 0xC88A65);
		this.addColor("Black", 0x000000);
		this.addColor("Charcoal Gray", 0x736A62);
		this.addColor("Gray", 0x8B8680);
		this.addColor("Blue-Gray", 0xC8C8CD);
		this.addColor("White", 0xFFFFFF);
		
		this.getElements().addAll(this.colors.keySet());
	}
	
	public Vector3f getColorForID(String id) {
		return this.colors.get(id);
	}
	
	private void addColor(String name, int color) {
		this.colors.put(name, new Vector3f(Utils.getRed(color) / 255.0f, Utils.getGreen(color) / 255.0f, Utils.getBlue(color) / 255.0f));
	}
	
	public void setCurrent(WorldElement current) {
		this.current = current;
	}

	protected void onSelect(String element) {
		assert this.current != null;
		
		this.current.setColor(new Vector4f(this.colors.get(element), 0.3f));
	}
	
	protected void onClick(String element) {
		assert this.current != null;
		
		// TODO Save
		
		this.show(MainMenus.STRUCT_LIST);
	}
}