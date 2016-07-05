package com.mygdx.potatoandtomato.scenes.shop_scene;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.utils.Threadings;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;

/**
 * Created by SiongLeng on 5/7/2016.
 */
public class ShopArcadeScreensAnimation implements Disposable {

    private HashMap<Integer, Image> screensMap;
    private boolean disposed;

    public ShopArcadeScreensAnimation(HashMap<Integer, Image> screensMap) {
        this.screensMap = screensMap;
    }

    public void start(){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                int style = MathUtils.random(1, 6);

                if (style == 1) {
                    style1();
                }
                else if (style == 2) {
                    style2();
                }
                else if (style == 3) {
                    style3();
                }
                else if (style == 4) {
                    style4();
                }
                else if (style == 5) {
                    style5();
                }
                else if (style == 6) {
                    style6();
                }

            }
        });
    }


    public void style1(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(disposed) return;
                    for (int i = 1; i <= 6; i++) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                toggleLight(screensMap.get(finalI));
                            }
                        });
                        Threadings.sleep(300);
                    }

                    for (int i = 12; i >= 7; i--) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                toggleLight(screensMap.get(finalI));
                            }
                        });
                        Threadings.sleep(300);
                    }

                }
            }
        });
    }


    public void style2(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(disposed) return;
                    for (int i = 1; i <= 6; i++) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                int previous = finalI - 1;
                                if(previous == 0){
                                    previous = 6;
                                }

                                dim(screensMap.get(previous));
                                dim(screensMap.get(previous + 6));

                                lightUp(screensMap.get(finalI));
                                lightUp(screensMap.get(finalI + 6));

                            }
                        });
                        Threadings.sleep(300);
                    }
                }
            }
        });
    }

    public void style3(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(disposed) return;
                    for (int i = 1; i <= 6; i++) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                int index = finalI;
                                if(finalI % 2 == 0){
                                    index += 6;
                                }
                                toggleLight(screensMap.get(index));

                            }
                        });
                        Threadings.sleep(300);
                    }

                    for (int i = 12; i >= 7; i--) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                int index = finalI;
                                if(finalI % 2 == 0){
                                    index -= 6;
                                }
                                toggleLight(screensMap.get(index));

                            }
                        });
                        Threadings.sleep(300);
                    }
                }
            }
        });
    }

    public void style4(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(disposed) return;
                    for (int i = 1; i <= 6; i++) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                int index = finalI;
                                if(finalI % 2 == 0){
                                    index += 6;
                                }
                                toggleLight(screensMap.get(index));
                                toggleLight(screensMap.get(index + 1));
                            }
                        });
                        Threadings.sleep(300);
                    }
                }
            }
        });
    }

    public void style5(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(disposed) return;
                    for (int i = 1; i <= 2; i++) {
                        final int finalI = i;
                        Threadings.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                int index = 0;
                                if(finalI == 1){
                                    index = finalI;
                                    lightUp(screensMap.get(index));
                                    for(int q = 1; q<=5; q++){
                                        lightUp(screensMap.get(index + q));
                                    }

                                    int dimIndex = 7;
                                    dim(screensMap.get(dimIndex));
                                    for(int q = 1; q<=5; q++){
                                        dim(screensMap.get(dimIndex + q));
                                    }
                                }
                                else{
                                    index = 7;
                                    lightUp(screensMap.get(index));
                                    for(int q = 1; q<=5; q++){
                                        lightUp(screensMap.get(index + q));
                                    }

                                    int dimIndex = 1;
                                    dim(screensMap.get(dimIndex));
                                    for(int q = 1; q<=5; q++){
                                        dim(screensMap.get(dimIndex + q));
                                    }
                                }
                            }
                        });
                        Threadings.sleep(700);
                    }
                }
            }
        });
    }

    public void style6(){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(disposed) return;

                    int index = MathUtils.random(1, 12);
                    toggleLight(screensMap.get(index));

                    Threadings.sleep(600);

                }
            }
        });
    }


    private void lightUp(Image screenImage){
        if(screenImage != null){
            screenImage.clearActions();
            screenImage.addAction(alpha(0.8f, 0.2f));
        }
    }

    private void dim(Image screenImage){
        if(screenImage != null){
            screenImage.clearActions();
            screenImage.addAction(alpha(0.2f, 0.2f));
        }
    }

    private void toggleLight(Image screenImage){
        if(screenImage != null){
            if(screenImage.getColor().a <= 0.2f){
                lightUp(screenImage);
            }
            else{
                dim(screenImage);
            }
        }
    }

    @Override
    public void dispose() {
        disposed = true;
    }
}



