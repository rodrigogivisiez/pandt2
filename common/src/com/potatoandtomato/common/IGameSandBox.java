package com.potatoandtomato.common;

/**
 * Created by SiongLeng on 6/1/2016.
 */
public interface IGameSandBox {

    void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable);
    void userAbandoned();
    void onGameLoaded();


}
