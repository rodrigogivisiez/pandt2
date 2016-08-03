package com.mygdx.potatoandtomato.scenes.room_scene;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.game_file_checker.GameFileCheckerListener;
import com.mygdx.potatoandtomato.models.FileData;
import com.mygdx.potatoandtomato.models.Game;
import com.mygdx.potatoandtomato.services.Loggings;
import com.mygdx.potatoandtomato.services.Preferences;
import com.mygdx.potatoandtomato.services.VersionControl;
import com.potatoandtomato.common.absints.DownloaderListener;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.statics.Vars;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.common.utils.ThreadsPool;
import com.shaded.fasterxml.jackson.core.type.TypeReference;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 17/12/2015.
 */
public class GameFileChecker implements Disposable {

    private Preferences preferences;
    private IDownloader downloader;
    private Game gameModel;
    private IDatabase database;
    private VersionControl versionControl;

    private GameFileCheckerListener listener;
    private boolean downloadKilled;
    private ArrayList<SafeThread> downloadGameThreads;
    private long currentDownloadSize, totalSize;

    public GameFileChecker(Game game, Preferences preferences,
                           IDownloader downloader, IDatabase database, VersionControl versionControl,
                           GameFileCheckerListener listener) {
        this.listener = listener;
        this.database = database;
        this.preferences = preferences;
        this.downloader = downloader;
        this.versionControl = versionControl;
        this.totalSize = game.getGameSize();
        this.downloadGameThreads = new ArrayList();
        getLatestGameModel(game);
    }

    public void getLatestGameModel(final Game game){
        database.getGameByAbbr(game.getAbbr(), new DatabaseListener<Game>(Game.class) {
            @Override
            public void onCallback(Game obj, Status st) {
                if (st == Status.SUCCESS && obj != null) {
                    if (Integer.valueOf(obj.getCommonVersion()) > Integer.valueOf(versionControl.getCommonVersion())) {
                        listener.onCallback(GameFileResult.CLIENT_OUTDATED, Status.FAILED);
                    } else {
                        if (!obj.getVersion().equals(game.getVersion())) {
                            listener.onCallback(GameFileResult.GAME_OUTDATED, Status.FAILED);
                        } else {
                            gameModel = obj;
                            checkGameFiles();
                        }
                    }
                } else {
                    listener.onCallback(GameFileResult.FAILED_RETRIEVE, Status.FAILED);
                }
            }
        });
    }

    public void checkGameFiles(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                HashMap<String, FileData> toDownloadFiles = new HashMap();
                HashMap<String, FileData> toDeleteFiles = new HashMap();
                HashMap<String, FileData> cloudFiles = gameModel.getGameFilesMap();
                HashMap<String, FileData> currentFiles = new HashMap();

                currentFiles = restoreLocalGameFilesData(currentFiles);

                for (Map.Entry<String, FileData> entry : cloudFiles.entrySet()) {
                    String cloudFileName = entry.getKey();
                    FileData cloudFileData = entry.getValue();

                    if(currentFiles.containsKey(cloudFileName)){
                        FileData currentFileData = currentFiles.get(cloudFileName);
                        if(currentFileData.getModifiedAt().equals(cloudFileData.getModifiedAt())){
                            FileHandle toCheckFile = gameModel.getFileRelativeToGameBasePath(cloudFileName);
                            if(toCheckFile.exists()){
                                if(toCheckFile.file().length() == cloudFileData.getSize()){
                                    currentDownloadSize += cloudFileData.getSize();
                                    continue;
                                }
                            }
                        }
                    }

                    toDownloadFiles.put(cloudFileName, cloudFileData);
                }

                for (Map.Entry<String, FileData> entry : currentFiles.entrySet()) {
                    String currentFileName = entry.getKey();
                    FileData currentFileData = entry.getValue();

                    if(!cloudFiles.containsKey(currentFileName)){
                        toDeleteFiles.put(currentFileName, currentFileData);
                    }
                }

                for(String toDeleteFilePath : toDeleteFiles.keySet()){
                    FileHandle toDeleteFile = gameModel.getFileRelativeToGameBasePath(toDeleteFilePath);
                    if(toDeleteFile.exists()) toDeleteFile.delete();
                }

                int maxBranch = 5;

                if(toDownloadFiles.size() > 0){
                    updateDownloaded(0);
                }

                ThreadsPool threadsPool = new ThreadsPool();

                for (Map.Entry<String, FileData> entry : toDownloadFiles.entrySet()) {
                    String downloadFileName = entry.getKey();
                    FileData downloadFileData = entry.getValue();

                    if(downloadKilled) return;

                    final Threadings.ThreadFragment threadFragment = new Threadings.ThreadFragment();

                    SafeThread safeThread = downloadFile(downloadFileData.getUrl(), gameModel.getFileRelativeToGameBasePath(downloadFileName).file(), new Runnable() {
                        @Override
                        public void run() {
                            threadFragment.setFinished(true);
                        }
                    });

                    downloadGameThreads.add(safeThread);
                    threadsPool.addFragment(threadFragment);

                    while (true){
                        if(downloadKilled) return;
                        if(threadsPool.getUnfinishedFragmentSize() > maxBranch){
                            Threadings.sleep(100);
                        }
                        else{
                            break;
                        }
                    }
                }

                while (!threadsPool.allFinished()){
                    if(downloadKilled) return;
                    Threadings.sleep(100);
                }

                saveGameFilesData();

                listener.onCallback(null, Status.SUCCESS);

            }
        });
    }


    public SafeThread downloadFile(String url, final File f, final Runnable complete){
        final long[] _prevDownloaded = new long[1];
        _prevDownloaded[0] = 0;

        return downloader.downloadFileToPath(url, f, new DownloaderListener() {
            @Override
            public void onCallback(byte[] bytes, Status st) {
                if(st == Status.FAILED){
                    killDownloads();
                }
                else{
                    complete.run();
                }
            }

            @Override
            public void onStep(double percentage, long totalSize, long downloadedSize) {
                super.onStep(percentage, totalSize, downloadedSize);
                updateDownloaded(downloadedSize - _prevDownloaded[0]);
                _prevDownloaded[0] = downloadedSize;
            }
        });
    }

    public void updateDownloaded(long toAdd){
        currentDownloadSize += toAdd;

        float currentPercent = ((float) currentDownloadSize / (float) totalSize) * 100;

        if(currentPercent > 100) currentPercent = 100;
        listener.onStep(Math.round(currentPercent));
    }

    public HashMap<String, FileData> restoreLocalGameFilesData(HashMap<String, FileData> fileDataHashMap){
        String fileData =  preferences.get(gameModel.getAbbr());
        if(Strings.isEmpty(fileData)){
            fileData = "0";
        }
        try
        {
            Double.parseDouble(fileData);
            fileData = "";
        }
        catch(NumberFormatException e)
        {
        }

        if(!Strings.isEmpty(fileData)){
            ObjectMapper objectMapper = Vars.getObjectMapper();
            TypeReference<HashMap<String,FileData>> typeRef
                    = new TypeReference<HashMap<String,FileData>>() {};
            try {
                fileDataHashMap = objectMapper.readValue(fileData, typeRef);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileDataHashMap;
    }

    public void saveGameFilesData() {
        preferences.put(gameModel.getAbbr(), gameModel.getGameFiles());
    }

    public void killDownloads(){
        downloadKilled = true;
        for(SafeThread safeThread : downloadGameThreads){
            safeThread.kill();
        }
        listener.onCallback(GameFileResult.FAILED_RETRIEVE, Status.FAILED);

    }

    @Override
    public void dispose() {
        killDownloads();
    }


    public enum GameFileResult {
        FAILED_RETRIEVE, CLIENT_OUTDATED, GAME_OUTDATED
    }

}
