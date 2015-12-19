package com.mygdx.potatoandtomato.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Terms;

public class DesktopLauncher {
	public static void main (String[] arg) {

		if(arg.length > 0 && arg[0].equals("another")){
			Terms.PREF_NAME = "anotheruser";
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = Positions.getHeight();
		config.width = Positions.getWidth();
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 2048;
		//TexturePacker.process(settings, "../../images/ui", "../../android/assets", "ui_pack");
		new LwjglApplication(new PTGame(), config);
	}
}
