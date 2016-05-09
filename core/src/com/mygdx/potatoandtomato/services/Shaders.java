package com.mygdx.potatoandtomato.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class Shaders implements Disposable{

    HashMap<String, ShaderProgram> _shaders;

    public Shaders() {
        ShaderProgram.pedantic = false;
        _shaders = new HashMap();
    }

    public ShaderProgram getBlackOverlay(){
        String path = "shaders/disabledStateShader.glsl";
        if(_shaders.containsKey(path)) return _shaders.get(path);

        ShaderProgram shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertexShader.glsl").readString(),
                                    Gdx.files.internal(path).readString());
        _shaders.put(path, shaderProgram);
        return shaderProgram;
    }


    @Override
    public void dispose() {
        for (Map.Entry<String, ShaderProgram> entry : _shaders.entrySet()) {
            ShaderProgram shaderProgram = entry.getValue();
            shaderProgram.dispose();
        }
        _shaders.clear();
    }
}
