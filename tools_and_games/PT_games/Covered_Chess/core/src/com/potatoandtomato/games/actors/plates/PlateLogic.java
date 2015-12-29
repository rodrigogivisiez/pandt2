package com.potatoandtomato.games.actors.plates;

import com.potatoandtomato.games.helpers.Assets;

import java.util.PriorityQueue;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class PlateLogic {

    private Assets _assets;
    private PlateActor _plateActor;

    public PlateLogic(Assets _assets) {
        this._assets = _assets;
        _plateActor = new PlateActor(_assets);
    }

    public PlateActor getPlateActor() {
        return _plateActor;
    }
}
