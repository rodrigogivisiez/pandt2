package com.potatoandtomato.games.screens.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.WebImageListener;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.utils.ThreadsPool;
import com.potatoandtomato.games.absintf.DatabaseListener;
import com.potatoandtomato.games.absintf.ImageStorageListener;
import com.potatoandtomato.games.models.ImageDetails;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 7/4/2016.
 */
public class ImageStorage implements Disposable {

    private Services services;
    private GameCoordinator gameCoordinator;
    private int totalIndex;
    private ArrayList<ImagePair> imagePairs;
    private boolean randomize = true;
    private int imageCountPerDownload = 3;
    private int currentIndex;
    private int orderIndex;           //used to make sure final results is in same order
    private long downloadPeriod = 10000;
    private ArrayList<String> currentStoreImageIds;
    private SafeThread safeThread;

    public ImageStorage(Services services, GameCoordinator gameCoordinator) {
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.imagePairs = new ArrayList();
        this.currentStoreImageIds = new ArrayList();
        safeThread = new SafeThread();
    }

    public void startMonitor(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();
                services.getDatabase().getLastImageIndex(new DatabaseListener<Integer>() {
                    @Override
                    public void onCallback(Integer obj, Status st) {
                        totalIndex = obj;
                        threadFragment.setFinished(true);
                    }
                });

                while (!threadFragment.isFinished()){
                   Threadings.sleep(300);
                }

                while (true){
                    initiateDownloadsIfNoImagesAndIsCoordinator();
                    if(Global.REVIEW_MODE){
                        Threadings.sleep(2000);
                    }
                    else{
                        Threadings.sleep(downloadPeriod);
                    }

                    if(safeThread.isKilled()){
                        break;
                    }
                }
            }
        });
    }


    public void initiateDownloadsIfNoImagesAndIsCoordinator(){
        if(imagePairs.size() < imageCountPerDownload && gameCoordinator.getDecisionsMaker().meIsDecisionMaker()){
            final ArrayList<Integer> indexes = new ArrayList<>();
            for(int i = 0; i < imageCountPerDownload; i++){
                if(randomize){
                    indexes.add(MathUtils.random(0, totalIndex));
                }
                else{
                    indexes.add(currentIndex);
                    currentIndex++;
                }
            }

            convertImageIndexesToImageIds(indexes, new DatabaseListener<ArrayList<String>>() {
                @Override
                public void onCallback(ArrayList<String> imageIds, Status st) {
                    services.getRoomMsgHandler().sendDownloadImageRequest(imageIds);
                }
            });
        }
    }

    public void convertImageIndexesToImageIds(final ArrayList<Integer> indexes, final DatabaseListener<ArrayList<String>> listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ThreadsPool threadsPool = new ThreadsPool();
                final ArrayList<String> imageIds = new ArrayList<String>();

                int i = 0;
                for(Integer index : indexes){
                    final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();
                    final int finalI = i;
                    services.getDatabase().getImageDetailsByIndex(index, new DatabaseListener<ImageDetails>(ImageDetails.class) {
                        @Override
                        public void onCallback(ImageDetails details, Status st) {
                            if(st == Status.SUCCESS){
                                imageIds.add(finalI <= imageIds.size() ? finalI : imageIds.size(), details.getId());
                            }
                            threadFragment.setFinished(true);
                        }
                    });

                    threadsPool.addFragment(threadFragment);
                    i++;
                }

                while (!threadsPool.allFinished()){
                    Threadings.sleep(200);
                }

                listener.onCallback(imageIds, Status.SUCCESS);

            }
        });
    }

    public void receivedDownloadRequest(final ArrayList<String> imageIds){
        for(String id : imageIds){
            if(!currentStoreImageIds.contains(id)){
                currentStoreImageIds.add(id);
            }
        }

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                for(String id : imageIds){
                    if(getImagePairById(id) != null) continue;

                    final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();
                    services.getDatabase().getImageDetailsById(id, new DatabaseListener<ImageDetails>(ImageDetails.class) {
                        @Override
                        public void onCallback(ImageDetails details, Status st) {
                            if (st == Status.SUCCESS) {
                                downloadImages(details, orderIndex);
                            }
                            threadFragment.setFinished(true);
                        }
                    });

                    while (!threadFragment.isFinished()){
                        Threadings.sleep(200);
                    }
                    orderIndex++;
                }
            }
        });
    }

    public synchronized void downloadImages(final ImageDetails imageDetails, final int currentOrderIndex){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                    final Texture[] image1 = new Texture[1];
                    final Texture[] image2 = new Texture[1];

                    ThreadsPool threadsPool = new ThreadsPool();
                    final Threadings.ThreadFragment threadFragment1 = new Threadings.ThreadFragment();
                    final Threadings.ThreadFragment threadFragment2 = new Threadings.ThreadFragment();
                    threadsPool.addFragment(threadFragment1);
                    threadsPool.addFragment(threadFragment2);

                    gameCoordinator.getRemoteImage(imageDetails.getImageOneUrl(), new WebImageListener() {
                        @Override
                        public void onLoaded(Texture texture) {
                            if(texture != null){
                                image1[0] = texture;
                            }
                            threadFragment1.setFinished(true);
                        }
                    });

                    gameCoordinator.getRemoteImage(imageDetails.getImageTwoUrl(), new WebImageListener() {
                        @Override
                        public void onLoaded(Texture texture) {
                            if(texture != null){
                                image2[0] = texture;
                            }
                            threadFragment2.setFinished(true);
                        }
                    });


                    while (!threadsPool.allFinished()){
                        Threadings.sleep(100);
                    }

                    if(!randomize){
                        if(imageDetails.getIndex() > currentIndex){
                            return;
                        }
                    }

                    if(image1[0] != null && image2[0] != null){
                        int addingIndex = getStartIndexWithHigherOrderIndex(currentOrderIndex);
                        addImagePair(addingIndex, imageDetails, image1[0], image2[0], currentOrderIndex);
                    }
                }
        });
    }

    private synchronized void addImagePair(int toIndex,
                                                ImageDetails imageDetails, Texture texture1, Texture texture2, int currentOrderIndex){
        if(getImagePairById(imageDetails.getId()) == null){
            imagePairs.add(toIndex,
                    new ImagePair(imageDetails, texture1, texture2, currentOrderIndex));
        }
    }

    public void onResume(){
        for(final ImagePair imagePair : imagePairs){
            gameCoordinator.getRemoteImage(imagePair.getImageDetails().getImageOneUrl(), new WebImageListener() {
                @Override
                public void onLoaded(Texture texture) {
                    if(texture != null){
                        imagePair.setImageOne(texture);
                    }
                }
            });

            gameCoordinator.getRemoteImage(imagePair.getImageDetails().getImageTwoUrl(), new WebImageListener() {
                @Override
                public void onLoaded(Texture texture) {
                    if(texture != null){
                        imagePair.setImageTwo(texture);
                    }
                }
            });
        }
    }

    public void peek(final ImageStorageListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (imagePairs.size() <= 0){
                    Threadings.sleep(300);
                    if(safeThread.isKilled()) return;
                }

                int min = 0;
                int max = Math.max(imagePairs.size() - 2, 0);        //don't include last downloaded image

                ImagePair first = imagePairs.get(MathUtils.random(min, max));
                listener.onPeeked(first);
            }
        });
    }

    public void peek(final int index, final ImageStorageListener listener){
        if(index == -1){
            peek(listener);
            return;
        }

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (getImagePairByIndex(index) == null){
                    Threadings.sleep(300);
                    if(safeThread.isKilled()) return;
                }

                ImagePair first = getImagePairByIndex(index);
                listener.onPeeked(first);
            }
        });
    }

    public synchronized void pop(final ImageStorageListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (imagePairs.size() <= 0){
                    Threadings.sleep(300);
                    if(safeThread.isKilled()) return;
                }

                ImagePair first = imagePairs.get(0);
                imagePairs.remove(first);
                currentStoreImageIds.remove(first.getImageDetails().getId());
                listener.onPopped(first);
            }
        });
    }

    public synchronized void pop(final String id, final ImageStorageListener listener){
        ImagePair first = getImagePairById(id);
        if(first != null){
            imagePairs.remove(first);
            currentStoreImageIds.remove(first.getImageDetails().getId());
        }
        listener.onPopped(first);
    }

    public synchronized void popWait(final String id, final ImageStorageListener listener){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ImagePair first = getImagePairById(id);
                while (first == null){
                    Threadings.sleep(300);
                    first = getImagePairById(id);
                    if(safeThread.isKilled()) return;
                }

                imagePairs.remove(first);
                currentStoreImageIds.remove(first.getImageDetails().getId());
                listener.onPopped(first);
            }
        });
    }

    public void resendRedownloadCurrentImageStorage(String toUserId){
        services.getRoomMsgHandler().sendPrivateDownloadImageRequest(toUserId, currentStoreImageIds);
    }


    private ImagePair getImagePairById(String id){
        for(ImagePair imagePair : imagePairs){
            if(imagePair.getImageDetails().getId().equals(id)){
                return imagePair;
            }
        }
        return null;
    }

    private ImagePair getImagePairByIndex(int index){
        for(ImagePair imagePair : imagePairs){
            if(imagePair.getImageDetails().getIndex() == index){
                return imagePair;
            }
        }
        return null;
    }

    private synchronized int getStartIndexWithHigherOrderIndex(int orderIndex){
        int i = 0;
        for(ImagePair imagePair : imagePairs){
            if (imagePair.getOrderIndex() >= orderIndex){
                return i;
            }
            i++;
        }
        return imagePairs.size();
    }

    public void disposeAllImages(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                for(ImagePair pair : imagePairs){
                    pair.getImageOne().dispose();
                    pair.getImageTwo().dispose();
                }
                imagePairs.clear();
            }
        });
    }

    @Override
    public void dispose() {
        safeThread.kill();
        disposeAllImages();
    }

    public ArrayList<ImagePair> getImagePairs() {
        return imagePairs;
    }

    public void setRandomize(boolean randomize) {
        this.randomize = randomize;
    }

    public void setDownloadPeriod(long downloadPeriod) {
        this.downloadPeriod = downloadPeriod;
    }

    public void setCurrentIndex(int currentIndex) {
        if(currentIndex > totalIndex){
            currentIndex = 0;
        }
        this.currentIndex = currentIndex;
    }



}
