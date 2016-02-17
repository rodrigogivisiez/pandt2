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
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.*;
import javafx.geometry.Pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DesktopLauncher {

	private static LwjglApplication application;
	private static ImageLoader _imageLoader;
	public static Broadcaster _broadcaster;

	public static void main (String[] arg) {

		_broadcaster = new Broadcaster();
		_imageLoader = new ImageLoader(_broadcaster);

		if(arg.length > 0){
			Terms.PREF_NAME = arg[0];
		}

		_broadcaster.subscribe(BroadcastEvent.LOGIN_GCM_REQUEST, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				_broadcaster.broadcast(BroadcastEvent.LOGIN_GCM_CALLBACK, null, Status.SUCCESS);
			}
		});

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = Positions.getHeight();
		config.width = Positions.getWidth();
		config.resizable = true;


		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 2048;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.filterMin = Texture.TextureFilter.Linear;
		if(Terms.PREF_NAME.equals("pack")) TexturePacker.process(settings, "../../images/ui", "../../android/assets", "ui_pack");

		application = new LwjglApplication(new PTGame(_broadcaster), config);

		subscribeLoadGameRequest();
		subscribeOrientationChanged();
	}

	public static void subscribeOrientationChanged(){
		_broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener<Integer>() {
			@Override
			public void onCallback(Integer obj, Status st) {
				Global.IS_POTRAIT = (obj == 0);
				application.getGraphics().setDisplayMode(Positions.getWidth(), Positions.getHeight(), false);
			}
		});
	}

	public static void subscribeLoadGameRequest(){
		_broadcaster.subscribe(BroadcastEvent.LOAD_GAME_REQUEST, new BroadcastListener<GameCoordinator>() {
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
					_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, obj, Status.SUCCESS);
				}
				else{
					_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
				}

			}
		});
	}


}
