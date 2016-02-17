package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.*;
import com.potatoandtomato.games.abs.database.DatabaseListener;
import com.potatoandtomato.games.abs.database.IDatabase;
import com.potatoandtomato.games.abs.image_getter.PeekImageListener;
import com.potatoandtomato.games.abs.image_getter.PopImageListener;
import com.potatoandtomato.games.models.ImageData;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.UpdateMsg;
import com.potatoandtomato.games.utils.Strings;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 2/2/2016.
 */
public class ImageGetter implements Disposable {

    private ArrayList<ImagePair> _imagePairs;
    private GameCoordinator _coordinator;
    private IDatabase _database;
    private SafeThread _requestDownloadSafeTread;
    private final String _domain = "http://www.potato-and-tomato.com/photo_hunt_images/%s/%s";
    public static int INDEX = 0;

    public ImageGetter(GameCoordinator gameCoordinator, IDatabase database) {
        this._coordinator = gameCoordinator;
        this._database = database;
        this._imagePairs = new ArrayList<ImagePair>();

        _coordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String s, String s1) {
                UpdateMsg updateMsg = new UpdateMsg(s);
                if(updateMsg.getUpdateCode() == UpdateCode.DOWNLOAD_IMAGES){
                    downloadImages(updateMsg.getMsg().split(","));
                }
            }
        });
    }

    public void init(){
        startRequestDownloadTread();
    }

    public void peekImage(final PeekImageListener listener){
        if(_imagePairs.size() > 0){
            ImagePair imagePair = _imagePairs.get(0);
            listener.onImagePeeked(imagePair);
        }
        else{
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    while (_imagePairs.size() <= 0){
                        Threadings.sleep(100);
                    }
                    peekImage(listener);
                }
            });
        }

    }

    public void goToIndex(int index){
        _imagePairs.clear();
        INDEX = index;
        randomGetImages();
    }

    public ImagePair popImageById(final String id){
        ImagePair result = null;
        for(ImagePair imagePair : _imagePairs){
            if(imagePair.getId().equals(id)){
                result = imagePair;
                _imagePairs.remove(imagePair);
                break;
            }
        }
        return  result;
    }

    public void downloadImages(final String[] ids){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                for(String id : ids){

                    final Texture[] image1 = new Texture[1];
                    final Texture[] image2 = new Texture[1];
                    final boolean[] finishDownloadImage1 = {false};
                    final boolean[] finishDownloadImage2 = {false};
                    _coordinator.getDownloader().downloadData(String.format(_domain, id, "1.jpg"), new DownloaderListener() {
                        @Override
                        public void onCallback(byte[] bytes, Status status) {
                            image1[0] = processTextureBytes(bytes);
                            finishDownloadImage1[0] = true;
                        }
                    });
                    _coordinator.getDownloader().downloadData(String.format(_domain, id, "2.jpg"), new DownloaderListener() {
                        @Override
                        public void onCallback(byte[] bytes, Status status) {
                            image2[0] = processTextureBytes(bytes);
                            finishDownloadImage2[0] = true;
                        }
                    });

                    while (!finishDownloadImage1[0] || !finishDownloadImage2[0]){
                        Threadings.sleep(100);
                    }

                    if(image1[0] == null || image2[0] == null){

                    }
                    else{
                        _database.getImageDataById(id, new DatabaseListener<ImageData>(ImageData.class) {
                            @Override
                            public void onCallback(ImageData obj, Status st) {
                                if (st == Status.SUCCESS && obj != null) {
                                    addImagePairs(new ImagePair(image1[0], image2[0], obj.getJson(), obj.getId(), obj.getIndex()));
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private Texture processTextureBytes(byte[] textureBytes) {
        if(textureBytes != null){
            try {
                Pixmap pixmap = new Pixmap(textureBytes, 0, textureBytes.length);
                Texture texture = new Texture(pixmap);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                pixmap.dispose();
                return texture;

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return null;
    }

    public void startRequestDownloadTread(){
        _requestDownloadSafeTread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if(_requestDownloadSafeTread.isKilled()) break;
                    else{
                        if(_coordinator.meIsDecisionMaker() && _imagePairs.size() < 5){
                            randomGetImages();
                        }
                    }
                    System.out.println("Current images count: " + _imagePairs.size());
                    Threadings.sleep(10000);
                }
            }
        });
    }

    public void randomGetImages(){
        _database.getTotalImagesCount(new DatabaseListener<Long>() {
            @Override
            public void onCallback(final Long totalIndex, Status st) {
                if (st == Status.SUCCESS) {
                    final ArrayList<String> ids = new ArrayList<String>();

                    for (int i = 0; i < 3; i++) {
                        //MathUtils.random(0, safeLongToInt(totalIndex))
                        if(INDEX > totalIndex) return;
                        _database.getImageIdByIndex(INDEX, new DatabaseListener<String>() {
                            @Override
                            public void onCallback(String obj, Status st) {
                                if (_requestDownloadSafeTread.isKilled()) return;
                                if (st == Status.SUCCESS) {
                                    if(obj == null){
                                        randomGetImages();
                                    }
                                    else{
                                        ids.add(obj);
                                    }
                                }
                                if(ids.size() == 3 || INDEX > totalIndex){
                                    _coordinator.sendRoomUpdate(new UpdateMsg(UpdateCode.DOWNLOAD_IMAGES, Strings.joinArr(ids)).toJson());
                                }
                            }
                        });
                        INDEX++;
                    }
                }
            }
        });
    }

    public void addImagePairs(ImagePair imagePair) {
        this._imagePairs.add(imagePair);
    }

    public int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {

        }
        return (int) l;
    }

    @Override
    public void dispose() {
        _requestDownloadSafeTread.kill();
        for(ImagePair imagePair : _imagePairs){
            imagePair.dispose();
        }
    }



}

