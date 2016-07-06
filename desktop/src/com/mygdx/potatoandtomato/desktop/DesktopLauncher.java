package com.mygdx.potatoandtomato.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.mygdx.potatoandtomato.PTGame;
import com.mygdx.potatoandtomato.absintflis.entrance.EntranceLoaderListener;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.utils.JarUtils;
import com.mygdx.potatoandtomato.utils.ForAppwarpTesting;
import com.mygdx.potatoandtomato.utils.Positions;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.*;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.helpers.DesktopImageLoader;
import com.potatoandtomato.common.utils.RunnableArgs;
import com.potatoandtomato.common.utils.Threadings;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class DesktopLauncher {

	private static LwjglApplication application;
	private static DesktopImageLoader _desktopImageLoader;
	public static Broadcaster _broadcaster;

	public static void main (String[] arg) {
		_broadcaster = new Broadcaster();
		_desktopImageLoader = new DesktopImageLoader(_broadcaster);

		Global.DEBUG = true;

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
		config.title = Terms.PREF_NAME;


		TexturePacker.Settings settings = new TexturePacker.Settings();
		settings.maxWidth = 2048;
		settings.maxHeight = 2048;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.filterMin = Texture.TextureFilter.Linear;
		if(Terms.PREF_NAME.equals("pack")){
			TexturePacker.process(settings, "../../images/ui", "../../android/assets", "ui_pack");
			return;
		}

		application = new LwjglApplication(new PTGame(_broadcaster), config);

		subscribeLoadGameRequest();
		subscribeOrientationChanged();
		subscribeAndroidSpecific();
	}

	public static void subscribeAndroidSpecific(){
		_broadcaster.subscribe(BroadcastEvent.HAS_REWARD_VIDEO, new BroadcastListener<RunnableArgs<Boolean>>() {
			@Override
			public void onCallback(RunnableArgs<Boolean> obj, Status st) {
				obj.run(false);
			}
		});

		_broadcaster.subscribe(BroadcastEvent.IAB_PRODUCTS_REQUEST, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				ArrayList<CoinProduct> coinProducts = new ArrayList<CoinProduct>();
				CoinProduct coinProduct = new CoinProduct("1", 5, "hehe");
				coinProduct.setCurrency("RM");
				coinProduct.setPrice(5.00);
				coinProducts.add(coinProduct);

				_broadcaster.broadcast(BroadcastEvent.IAB_PRODUCTS_RESPONSE, coinProducts);
			}
		});

		_broadcaster.subscribe(BroadcastEvent.IAB_PRODUCT_PURCHASE, new BroadcastListener() {
			@Override
			public void onCallback(Object obj, Status st) {
				Threadings.delay(5000, new Runnable() {
					@Override
					public void run() {
						_broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE);
					}
				});
			}
		});

	}

	public static void subscribeOrientationChanged(){
		_broadcaster.subscribe(BroadcastEvent.DEVICE_ORIENTATION, new BroadcastListener<Integer>() {
			@Override
			public void onCallback(Integer obj, Status st) {
				Global.IS_POTRAIT = (obj == 0);
				Threadings.postRunnable(new Runnable() {
					@Override
					public void run() {
						application.getGraphics().setWindowedMode(Positions.getWidth(), Positions.getHeight());
					}
				});
			}
		});
	}

	public static void subscribeLoadGameRequest(){
		_broadcaster.subscribe(BroadcastEvent.LOAD_GAME_REQUEST, new BroadcastListener<GameCoordinator>() {
			@Override
			public void onCallback(final GameCoordinator obj, Status st) {
				String jarPath = "file:///" + obj.getJarPath();
				URLClassLoader child = null;
				Class classToLoad = null;
				boolean success;
				try {
					child = new URLClassLoader(new URL[]{new URL(jarPath)}, this.getClass().getClassLoader());
					classToLoad = Class.forName (Terms.GAME_ENTRANCE, true, child);
					JarUtils.fillGameEntrance(classToLoad, obj, new EntranceLoaderListener() {
						@Override
						public void onLoadedSuccess() {
							_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, obj, Status.SUCCESS);
						}

						@Override
						public void onLoadedFailed() {
							_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
						}
					});
				    success = true;
				} catch (MalformedURLException e) {
					_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
				} catch (ClassNotFoundException e) {
					_broadcaster.broadcast(BroadcastEvent.LOAD_GAME_RESPONSE, null, Status.FAILED);
				}


			}
		});
	}


}
