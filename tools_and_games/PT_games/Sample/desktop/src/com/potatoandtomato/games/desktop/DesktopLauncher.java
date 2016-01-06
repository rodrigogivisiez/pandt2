package com.potatoandtomato.games.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.potatoandtomato.games.Entrance;
import com.potatoandtomato.games.SampleGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		//Entrance.setGameLibCoordinator(new com.potatoandtomato.common.GameLibCoordinator("", "", "", null, ));
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 360;
		config.height = 640;
		new LwjglApplication(new SampleGame(true), config);
	}
}
