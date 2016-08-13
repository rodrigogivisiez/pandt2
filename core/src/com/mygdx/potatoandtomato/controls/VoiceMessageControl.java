package com.mygdx.potatoandtomato.controls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.absintflis.recorder.PlaybackListener;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.services.Recorder;
import com.mygdx.potatoandtomato.services.SoundsPlayer;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

/**
 * Created by SiongLeng on 10/5/2016.
 */
public class VoiceMessageControl extends Table {

    private Assets assets;
    private SoundsPlayer soundsPlayer;
    private Recorder recorder;
    private String myUserId;
    private ChatMessage chatMessage;
    private FileHandle fileHandle;

    private Table _this;
    private Table root;
    private Image waveOne, waveTwo, waveThree, timeLineImage;
    private Label timerLabel;
    private int totalSecs;
    private final int TIME_LINE_WIDTH = 64;
    private SafeThread safeThread;
    private boolean isPlaying;
    private boolean autoFade;
    private boolean inPlayQueue;

    public VoiceMessageControl(ChatMessage chatMessage, Assets assets,
                               SoundsPlayer soundsPlayer, Recorder recorder, String myUserId,
                               boolean autoFade) {
        this.soundsPlayer = soundsPlayer;
        this.recorder = recorder;
        this.myUserId = myUserId;
        this.chatMessage = chatMessage;
        this.assets = assets;
        this.autoFade = autoFade;
        _this = this;
        _this.align(Align.left);
        fileHandle = Gdx.files.absolute(chatMessage.getMessage());
        totalSecs = Integer.valueOf(chatMessage.getExtra());

        populate();
        setListener();
        autoPlayIfNeeded();
    }

    public void updateTotalSecs(int newTotalSecs){
        this.totalSecs = newTotalSecs;
        changeTimerLabel(totalSecs);
    }

    public void populate(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                root = new Table();
                new DummyButton(root, assets);
                root.align(Align.left);
                root.setBackground(new TextureRegionDrawable(assets.getTextures().get(Textures.Name.AUDIO_MSG_BG)));

                timerLabel = new Label("",
                        new Label.LabelStyle(assets.getFonts().get(Fonts.FontId.MYRIAD_S_REGULAR), Color.valueOf("9b9b9b")));
                timerLabel.setAlignment(Align.center);
                changeTimerLabel(totalSecs);


                waveOne = new Image(assets.getTextures().get(Textures.Name.AUDIO_MSG_WAVE_ONE));
                waveTwo = new Image(assets.getTextures().get(Textures.Name.AUDIO_MSG_WAVE_TWO));
                waveThree = new Image(assets.getTextures().get(Textures.Name.AUDIO_MSG_WAVE_THREE));

                Table timerTable = new Table();
                timeLineImage = new Image(assets.getTextures().get(Textures.Name.AUDIO_MSG_TIMELINE));
                timeLineImage.setPosition(0, -2);
                timeLineImage.setWidth(0);
                timerTable.addActor(timeLineImage);

                root.add(timerLabel).padLeft(5).width(28);
                root.add(waveOne).padLeft(2);
                root.add(waveTwo).padLeft(-3);
                root.add(waveThree).padLeft(-4);
                root.add(timerTable).padLeft(3).width(TIME_LINE_WIDTH);

                if(autoFade){
                    root.getColor().a = 0.7f;
                }

                _this.add(root);
            }
        });
    }

    public void autoPlayIfNeeded(){
        if(!chatMessage.getSenderId().equals(myUserId) && Global.AUTO_PLAY_AUDIO_MSG){
            playVoiceMessage(true);
        }
    }

    public void changeTimerLabel(final int remainingSecs){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(remainingSecs < 0){
                    timerLabel.setText("...");
                }
                else{
                    timerLabel.setText("0:" + String.format("%02d", remainingSecs));
                }
            }
        });
    }

    public void playVoiceMessage(boolean useQueue){
        if(totalSecs > 0){
            inPlayQueue = true;
            recorder.playBack(useQueue, fileHandle, new PlaybackListener(fileHandle.name()) {
                @Override
                public void onStartPlay() {
                    isPlaying = true;
                    startPlayingThread();
                }

                @Override
                public void onEndPlay(Status status) {
                    isPlaying = false;
                    inPlayQueue = false;
                    resetDesign();
                }
            });
        }
    }

    public void stopVoiceMessage(){
        recorder.stopPlayback();
        isPlaying = false;
        resetDesign();
    }

    public void resetDesign(){
        if(safeThread != null) safeThread.kill();
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                changeTimerLabel(totalSecs);
                timerLabel.getStyle().fontColor = Color.valueOf("9b9b9b");
                timeLineImage.setWidth(0);
                waveOne.setColor(Color.valueOf("ffffff"));
                waveTwo.setColor(Color.valueOf("ffffff"));
                waveThree.setColor(Color.valueOf("ffffff"));
                waveOne.setVisible(true);
                waveTwo.setVisible(true);
                waveThree.setVisible(true);
            }
        });
    }

    public void startPlayingThread(){
        safeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                int sleepDuration = 100;
                int elapsed = 0;
                int i = 0;

                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        timerLabel.getStyle().fontColor = Color.valueOf("a86f0e");
                        waveOne.setColor(Color.valueOf("a86f0e"));
                        waveTwo.setColor(Color.valueOf("a86f0e"));
                        waveThree.setColor(Color.valueOf("a86f0e"));
                    }
                });

                while (true){

                    if(elapsed % 300 == 0 || elapsed == 0){
                        if(i == 0){
                            Threadings.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    waveOne.setVisible(true);
                                    waveTwo.setVisible(false);
                                    waveThree.setVisible(false);
                                }
                            });
                            i++;
                        }
                        else if(i == 1){
                            Threadings.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    waveOne.setVisible(true);
                                    waveTwo.setVisible(true);
                                    waveThree.setVisible(false);
                                }
                            });
                            i++;
                        }
                        else if(i == 2){
                            Threadings.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    waveOne.setVisible(true);
                                    waveTwo.setVisible(true);
                                    waveThree.setVisible(true);
                                }
                            });
                            i = 0;
                        }
                    }


                    Threadings.sleep(sleepDuration);
                    elapsed += sleepDuration;
                    if(safeThread.isKilled() || elapsed >= (totalSecs * 1000)){
                        if(elapsed >= (totalSecs * 1000)) {
                            changeTimerLabel(0);
                            Threadings.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    timeLineImage.setWidth(TIME_LINE_WIDTH);
                                }
                            });
                        }
                        if(safeThread.isKilled()){
                            resetDesign();
                        }
                        break;
                    }
                    else{
                        changeTimerLabel((int) Math.round((double) totalSecs - (double) elapsed / 1000));

                        float percent = (float) elapsed / ((float) totalSecs * 1000);
                        final float width = percent * TIME_LINE_WIDTH;

                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                timeLineImage.setWidth(width);
                            }
                        });
                    }
                }
            }
        });
    }

    public void fadeInControl(){
        if(autoFade){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    root.clearActions();
                    root.addAction(fadeIn(0.2f));
                }
            });
        }
    }

    public void fadeOutControl(){
        if(autoFade){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {
                    root.clearActions();
                    root.addAction(alpha(0.7f, 0.2f));
                }
            });
        }
    }

    public void setListener(){
        root.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(isPlaying){
                    stopVoiceMessage();
                }
                else{
                    playVoiceMessage(false);
                }
            }
        });
    }

    public boolean isInPlayQueue() {
        return inPlayQueue;
    }
}
