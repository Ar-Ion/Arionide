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
package org.azentreprise.arionide.ui.menu.edition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.azentreprise.arionide.Utils;
import org.azentreprise.arionide.events.MessageEvent;
import org.azentreprise.arionide.events.MessageType;
import org.azentreprise.arionide.project.Project;
import org.azentreprise.arionide.project.StructureMeta;
import org.azentreprise.arionide.ui.AppManager;
import org.azentreprise.arionide.ui.core.opengl.WorldElement;
import org.azentreprise.arionide.ui.menu.MainMenus;
import org.azentreprise.arionide.ui.menu.SpecificMenu;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Coloring extends SpecificMenu {
	
	private static final List<String> names = new ArrayList<>();
	private static final List<Vector3f> colors = new ArrayList<>();

	static {
		addColor("Red", 0xED0A3F);
		addColor("Maroon", 0xC32148);
		addColor("Scarlet", 0xFD0E35);
		addColor("Brick Red", 0xC62D42);
		addColor("English Vermilion", 0xCC474B);
		addColor("Madder Lake", 0xCC3336);
		addColor("Permanent Geranium Lake", 0xE12C2C);
		addColor("Maximum Red", 0xD92121);
		addColor("Indian Red", 0xB94E48);
		addColor("Orange-Red", 0xFF5349);
		addColor("Sunset Orange", 0xFE4C40);
		addColor("Bittersweet", 0xFE6F5E);
		addColor("Dark Venetian Red", 0xB33B24);
		addColor("Venetian Red", 0xCC553D);
		addColor("Light Venetian Red", 0xE6735C);
		addColor("Vivid Tangerine", 0xFF9980);
		addColor("Middle Red", 0xE58E73);
		addColor("Burnt Orange", 0xFF7F49);
		addColor("Red-Orange", 0xFF681F);
		addColor("Orange", 0xFF8833);
		addColor("Macaroni and Cheese", 0xFFB97B);
		addColor("Middle Yellow Red", 0xECB176);
		addColor("Mango Tango", 0xE77200);
		addColor("Yellow-Orange", 0xFFAE42);
		addColor("Maximum Yellow Red", 0xF2BA49);
		addColor("Banana Mania", 0xFBE7B2);
		addColor("Maize", 0xF2C649);
		addColor("Orange-Yellow", 0xF8D568);
		addColor("Goldenrod", 0xFCD667);
		addColor("Dandelion", 0xFED85D);
		addColor("Yellow", 0xFBE870);
		addColor("Green-Yellow", 0xF1E788);
		addColor("Middle Yellow", 0xFFEB00);
		addColor("Olive Green", 0xB5B35C);
		addColor("Spring Green", 0xECEBBD);
		addColor("Maximum Yellow", 0xFAFA37);
		addColor("Canary", 0xFFFF99);
		addColor("Lemon Yellow", 0xFFFF9F);
		addColor("Maximum Green Yellow", 0xD9E650);
		addColor("Middle Green Yellow", 0xACBF60);
		addColor("Inchworm", 0xAFE313);
		addColor("Light Chrome Green", 0xBEE64B);
		addColor("Yellow-Green", 0xC5E17A);
		addColor("Maximum Green", 0x5E8C31);
		addColor("Asparagus", 0x7BA05B);
		addColor("Granny Smith Apple", 0x9DE093);
		addColor("Fern", 0x63B76C);
		addColor("Middle Green", 0x4D8C57);
		addColor("Green", 0x3AA655);
		addColor("Medium Chrome Green", 0x6CA67C);
		addColor("Forest Green", 0x5FA777);
		addColor("Sea Green", 0x93DFB8);
		addColor("Shamrock", 0x33CC99);
		addColor("Mountain Meadow", 0x1AB385);
		addColor("Jungle Green", 0x29AB87);
		addColor("Caribbean Green", 0x00CC99);
		addColor("Tropical Rain Forest", 0x00755E);
		addColor("Middle Blue Green", 0x8DD9CC);
		addColor("Pine Green", 0x01786F);
		addColor("Maximum Blue Green", 0x30BFBF);
		addColor("Robin's Egg Blue", 0x00CCCC);
		addColor("Teal Blue", 0x008080);
		addColor("Light Blue", 0x8FD8D8);
		addColor("Aquamarine", 0x95E0E8);
		addColor("Turquoise Blue", 0x6CDAE7);
		addColor("Outer Space", 0x2D383A);
		addColor("Sky Blue", 0x76D7EA);
		addColor("Middle Blue", 0x7ED4E6);
		addColor("Blue-Green", 0x0095B7);
		addColor("Pacific Blue", 0x009DC4);
		addColor("Cerulean", 0x02A4D3);
		addColor("Maximum Blue", 0x47ABCC);
		addColor("Blue (I)", 0x4997D0);
		addColor("Cerulean Blue", 0x339ACC);
		addColor("Cornflower", 0x93CCEA);
		addColor("Green-Blue", 0x2887C8);
		addColor("Midnight Blue", 0x00468C);
		addColor("Navy Blue", 0x0066CC);
		addColor("Denim", 0x1560BD);
		addColor("Blue (III)", 0x0066FF);
		addColor("Cadet Blue", 0xA9B2C3);
		addColor("Periwinkle", 0xC3CDE6);
		addColor("Blue (II)", 0x4570E6);
		addColor("Wild Blue Yonder", 0x7A89B8);
		addColor("Indigo", 0x4F69C6);
		addColor("Manatee", 0x8D90A1);
		addColor("Cobalt Blue", 0x8C90C8);
		addColor("Celestial Blue", 0x7070CC);
		addColor("Blue Bell", 0x9999CC);
		addColor("Maximum Blue Purple", 0xACACE6);
		addColor("Violet-Blue", 0x766EC8);
		addColor("Blue-Violet", 0x6456B7);
		addColor("Ultramarine Blue", 0x3F26BF);
		addColor("Middle Blue Purple", 0x8B72BE);
		addColor("Purple Heart", 0x652DC1);
		addColor("Royal Purple", 0x6B3FA0);
		addColor("Violet (II)", 0x8359A3);
		addColor("Medium Violet", 0x8F47B3);
		addColor("Wisteria", 0xC9A0DC);
		addColor("Lavender (I)", 0xBF8FCC);
		addColor("Vivid Violet", 0x803790);
		addColor("Maximum Purple", 0x733380);
		addColor("Purple Mountains' Majesty", 0xD6AEDD);
		addColor("Fuchsia", 0xC154C1);
		addColor("Pink Flamingo", 0xFC74FD);
		addColor("Violet (I)", 0x732E6C);
		addColor("Brilliant Rose", 0xE667CE);
		addColor("Orchid", 0xE29CD2);
		addColor("Plum", 0x8E3179);
		addColor("Medium Rose", 0xD96CBE);
		addColor("Thistle", 0xEBB0D7);
		addColor("Mulberry", 0xC8509B);
		addColor("Red-Violet", 0xBB3385);
		addColor("Middle Purple", 0xD982B5);
		addColor("Maximum Red Purple", 0xA63A79);
		addColor("Jazzberry Jam", 0xA50B5E);
		addColor("Eggplant", 0x614051);
		addColor("Magenta", 0xF653A6);
		addColor("Cerise", 0xDA3287);
		addColor("Wild Strawberry", 0xFF3399);
		addColor("Lavender (II)", 0xFBAED2);
		addColor("Cotton Candy", 0xFFB7D5);
		addColor("Carnation Pink", 0xFFA6C9);
		addColor("Violet-Red", 0xF7468A);
		addColor("Razzmatazz", 0xE30B5C);
		addColor("Pig Pink", 0xFDD7E4);
		addColor("Carmine", 0xE62E6B);
		addColor("Blush", 0xDB5079);
		addColor("Tickle Me Pink", 0xFC80A5);
		addColor("Mauvelous", 0xF091A9);
		addColor("Salmon", 0xFF91A4);
		addColor("Middle Red Purple", 0xA55353);
		addColor("Mahogany", 0xCA3435);
		addColor("Melon", 0xFEBAAD);
		addColor("Pink Sherbert", 0xF7A38E);
		addColor("Burnt Sienna", 0xE97451);
		addColor("Brown", 0xAF593E);
		addColor("Sepia", 0x9E5B40);
		addColor("Fuzzy Wuzzy", 0x87421F);
		addColor("Beaver", 0x926F5B);
		addColor("Tumbleweed", 0xDEA681);
		addColor("Raw Sienna", 0xD27D46);
		addColor("Van Dyke Brown", 0x664228);
		addColor("Tan", 0xD99A6C);
		addColor("Desert Sand", 0xEDC9AF);
		addColor("Peach", 0xFFCBA4);
		addColor("Burnt Umber", 0x805533);
		addColor("Apricot", 0xFDD5B1);
		addColor("Almond", 0xEED9C4);
		addColor("Raw Umber", 0x665233);
		addColor("Shadow", 0x837050);
		addColor("Raw Sienna (I)", 0xE6BC5C);
		addColor("Timberwolf", 0xD9D6CF);
		addColor("Gold (I)", 0x92926E);
		addColor("Gold (II)", 0xE6BE8A);
		addColor("Silver", 0xC9C0BB);
		addColor("Copper", 0xDA8A67);
		addColor("Antique Brass", 0xC88A65);
		addColor("Black", 0x000000);
		addColor("Charcoal Gray", 0x736A62);
		addColor("Gray", 0x8B8680);
		addColor("Blue-Gray", 0xC8C8CD);
		addColor("White", 0xFFFFFF);
	}
	
	public static final int WHITE = 161;
	
	private static final String back = "Back";
	
	private static void addColor(String name, int color) {
		names.add(name);
		colors.add(new Vector3f(Utils.getRed(color) / 255.0f, Utils.getGreen(color) / 255.0f, Utils.getBlue(color) / 255.0f));
	}
	
	public static Vector3f getColorByName(String id) {
		return getColorByID(names.indexOf(id));
	}
	
	public static Vector3f getColorByID(int id) {
		int delta = colors.size() + 1 - id;
		return colors.get(id < 0 ? colors.size() - 1 : delta > 1 ? id : id - delta);
	}
	
	public static boolean hasColor(int id) {
		return id > -1 && id < colors.size();
	}
	
	
	public Coloring(AppManager manager) {
		super(manager);
		this.getElements().addAll(names);
		this.getElements().add(back);
	}

	protected void onSelect(String element) {
		this.getCurrent().setColor(new Vector4f(getColorByName(element), 0.3f));
	}
	
	public void setCurrent(WorldElement current) {
		super.setCurrent(current);
		
		Map<Integer, StructureMeta> metaData = this.getManager().getWorkspace().getCurrentProject().getStorage().getStructureMeta();
		
		if(metaData.containsKey(current.getID())) {
			this.setMenuCursor(metaData.get(current.getID()).getColorID());
		}
	}
	
	protected void onClick(String element) {		
		if(element != back) {
			Project project = this.getManager().getWorkspace().getCurrentProject();
			
			MessageEvent message = null;
			
			if(project != null) {
				message = project.getDataManager().setColor(this.getCurrent().getID(), names.indexOf(element));
			} else {
				message = new MessageEvent("No project is currently loaded", MessageType.ERROR);
			}
			
			this.getManager().getEventDispatcher().fire(message);
		}
		
		MainMenus.STRUCT_LIST.show();
	}
	
	public String getDescription() {
		return "Please select a color for " + super.getDescription();
	}
}