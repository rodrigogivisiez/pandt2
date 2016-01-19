package com.mygdx.potatoandtomato.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.helpers.utils.JarUtils;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.*;
import javafx.geometry.Pos;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DesktopLauncher {

	private static ImageLoader _imageLoader;

	public static void main (String[] arg) {

		_imageLoader = new ImageLoader();

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
		config.height = 800;
		config.width = 480;


		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 2048;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.filterMin = Texture.TextureFilter.Linear;
		//TexturePacker.process(settings, "../../images/ui", "../../android/assets", "ui_pack");

		new LwjglApplication(new PTGame(), config);

		subscribeLoadGameRequest();
	}

	public static void subscribeLoadGameRequest(){
		Broadcaster.getInstance().subscribe(BroadcastEvent.LOAD_GAME_REQUEST, new BroadcastListener<GameCoordinator>() {
			@Override
			public void onCallback(GameCoordinator obj, Status st) {
				String jarPath = "file:///" + obj.getJarPath();
				URLClassLoader child = null;
				Class classToLoad = null;
				boolean success;
				try {
					child = new URLClassLoader(new URL[]{new URL(jarPath)}, this.getClass().getClassLoader());
					classToLoad = Class.forName (Terms.GAME_ENTRANCE, true, child);
					obj = JarUtils.fillGameEntrance(classToLoad, obj);
				    success = true;
				} catch (MalformedURLException e) {
					success = false;
				} catch (ClassNotFoundException e) {
					success = false;
				}

				if(success){
					Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, obj, Status.SUCCESS);
				}
				else{
					Broadcaster.getInstance().broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
				}

			}
		});
	}


}
