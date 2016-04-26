package com.potatoandtomato.games.absintf;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by SiongLeng on 13/4/2016.
 */
public abstract class HintsLogicListener {

    public abstract void onHintClicked(Rectangle notYetHandledArea, int newHintLeft);

}
