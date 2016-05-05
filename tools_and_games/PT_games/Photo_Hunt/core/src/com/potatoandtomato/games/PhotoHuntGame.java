package com.potatoandtomato.games;

import com.potatoandtomato.common.absints.DownloaderListener;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.mockings.MockGame;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.DatabaseListener;
import com.potatoandtomato.games.absintf.mockings.MockModel;
import com.potatoandtomato.games.models.ImageDetails;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.services.Database;
import com.potatoandtomato.games.statics.Global;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PhotoHuntGame extends MockGame {

	private boolean _initialized;
	private Entrance entrance;

	public boolean isContinue;

	public PhotoHuntGame(String gameId) {
		super(gameId);
	}

	@Override
	public void create() {
		super.create();

		initiateMockGamingKit(1, 2, 0, Global.DEBUG);
	}

	@Override
	public void onReady() {
		if(!_initialized){
			_initialized = true;

			entrance = new Entrance(getCoordinator()){
				@Override
				public Services getServices() {
					Services services =  super.getServices();

					if(Global.DEBUG){
						services.setDatabase(new Database(null){
							@Override
							public void getLastImageIndex(DatabaseListener<Integer> listener) {
								listener.onCallback(0, Status.SUCCESS);
							}

							@Override
							public void getImageDetailsByIndex(int index, DatabaseListener<ImageDetails> listener) {
								listener.onCallback(MockModel.mockImageDetails(Strings.generateUniqueRandomKey(15)), Status.SUCCESS);
							}

							@Override
							public void getImageDetailsById(String id, DatabaseListener<ImageDetails> listener) {
								listener.onCallback(MockModel.mockImageDetails(id), Status.SUCCESS);
							}
						});
						getCoordinator().setDownloader(new IDownloader() {
							@Override
							public SafeThread downloadFileToPath(String s, File file, DownloaderListener downloaderListener) {
								return null;
							}

							@Override
							public void downloadData(String s, DownloaderListener downloaderListener) {
								Path path = Paths.get(String.format("testings/%s.jpg", s.equals("1") ? "ONE" : "TWO"));
								try {
									byte[] data = Files.readAllBytes(path);
									downloaderListener.onCallback(data, Status.SUCCESS);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});

					}

					return services;

				}
			};

			Threadings.runInBackground(new Runnable() {
				@Override
				public void run() {
					while (!entrance.getServices().getAssets().getPTAssetsManager().isFinishLoading()){
						Threadings.sleep(100);
					}

					Threadings.postRunnable(new Runnable() {
						@Override
						public void run() {
							if(!isContinue){
								entrance.init();
							}
							else{
								entrance.onContinue();
							}
						}
					});


				}
			});



		}

	}

}
