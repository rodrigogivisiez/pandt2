package com.potatoandtomato.common;

import com.badlogic.gdx.InputProcessor;

/**
 * Created by SiongLeng on 27/12/2015.
 */
public interface IPTGame {

    void addInputProcessor(InputProcessor processor, int priority);
    void addInputProcessor(InputProcessor processor);
    void removeInputProcessor(InputProcessor processor);



}
