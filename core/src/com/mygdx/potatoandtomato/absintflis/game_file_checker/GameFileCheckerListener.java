package com.mygdx.potatoandtomato.absintflis.game_file_checker;

import com.mygdx.potatoandtomato.scenes.room_scene.GameFileChecker;
import com.potatoandtomato.common.enums.Status;

/**
 * Created by SiongLeng on 8/1/2016.
 */
public abstract class GameFileCheckerListener {

    public abstract void onCallback(GameFileChecker.GameFileResult result, Status st);

    public void onStep(double percentage){

    }


}
