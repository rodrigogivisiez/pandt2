package com.potatoandtomato.common;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.firebase.client.Firebase;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by SiongLeng on 27/12/2015.
 */
public abstract class MockGame extends Game implements IPTGame {

    Array<InputProcessor> _processors;
    SpriteBatch _spriteBatch;
    MockGamingKit _mockGamingKit;
    GameCoordinator _gameCoordinator;

    public MockGame(String gameId) {

        try {
            PrintWriter out = new PrintWriter("common_version.txt");
            out.print(CommonVersion.VERSION);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Firebase _ref = new Firebase("https://forunittest.firebaseio.com");


        _processors = new Array<InputProcessor>();
        _gameCoordinator = new GameCoordinator("", "", "", new ArrayList<Team>(), 360, 640, this, _spriteBatch, "", new IGameSandBox() {
            @Override
            public void useConfirm(String msg, Runnable yesRunnable, Runnable noRunnable) {
                System.out.println("show confirm: " + msg);
            }

            @Override
            public void userAbandoned() {

            }
        },  _ref.child("gameBelongData").child(gameId), "1", new MockSoundManager());
    }

    public void initiateMockGamingKit(int expectedTeamCount, int eachTeamExpectedPlayers){
        _mockGamingKit = new MockGamingKit(_gameCoordinator, expectedTeamCount, eachTeamExpectedPlayers, new Runnable() {
            @Override
            public void run() {
                onReady();
                _gameCoordinator.setUserId(_mockGamingKit.getUserId());
            }
        });

    }

    @Override
    public void create() {
        _spriteBatch = new SpriteBatch();
        _gameCoordinator.setSpriteBatch(_spriteBatch);
    }

    public SpriteBatch getSpriteBatch() {
        return _spriteBatch;
    }

    public GameCoordinator getCoordinator() {
        return _gameCoordinator;
    }

    @Override
    public void addInputProcessor(InputProcessor processor, int index){
        _processors.insert(index, processor);
        setInputProcessors();
    }

    @Override
    public void addInputProcessor(InputProcessor processor) {
        _processors.add(processor);
        setInputProcessors();
    }

    @Override
    public void removeInputProcessor(InputProcessor processor) {
        _processors.removeValue(processor, false);
        setInputProcessors();
    }

    private void setInputProcessors(){
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.setProcessors(_processors);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public abstract void onReady();

}
