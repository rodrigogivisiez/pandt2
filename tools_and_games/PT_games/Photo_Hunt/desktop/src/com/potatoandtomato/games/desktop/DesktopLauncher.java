package com.potatoandtomato.games.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.potatoandtomato.games.PhotoHuntGame;
import com.potatoandtomato.games.statics.Global;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;

		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 2048;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.filterMin = Texture.TextureFilter.Linear;
		if(arg.length > 0 && arg[0].equals("pack")) TexturePacker.process(settings, "../../images", "../../android/assets", "pack");

		if(arg.length > 0 && (arg[0].equals("debug"))) Global.setDEBUG(true);

		if(arg.length > 0 && (arg[0].equals("single"))) Global.EXPECTED_PLAYERS_DEBUG = 1;

		if(arg.length > 1 && (arg[1].equals("bonus"))) Global.setDEBUG_BONUS(true);

		PhotoHuntGame photoHuntGame = new PhotoHuntGame("photo_hunt");
		if(arg.length > 0 && arg[0].equals("continue")) photoHuntGame.isContinue = true;

		if(arg.length > 0 && arg[0].equals("pack")) return;

		new LwjglApplication(photoHuntGame, config);




	}
}
