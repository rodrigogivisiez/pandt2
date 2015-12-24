package com.mygdx.potatoandtomato.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.BroadcastListener;
import com.potatoandtomato.common.Broadcaster;

public class DesktopLauncher {
	public static void main (String[] arg) {

		if(arg.length > 0){
			Terms.PREF_NAME = arg[0];
		}

		Broadcaster.getInstance().subscribe(BroadcastEvent.LOGIN_GCM_REQUEST, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				Broadcaster.getInstance().broadcast(BroadcastEvent.LOGIN_GCM_CALLBACK, null, Status.SUCCESS);
			}
		});

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = Positions.getHeight();
		config.width = Positions.getWidth();
		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 2048;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.filterMin = Texture.TextureFilter.Linear;
		//TexturePacker.process(settings, "../../images/ui", "../../android/assets", "ui_pack");
		new LwjglApplication(new PTGame(), config);
	}
}
