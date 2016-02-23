package com.potatoandtomato.games.controls;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by SiongLeng on 31/12/2015.
 */
public class AnimateLabel extends Table {


    public AnimateLabel(String msg, BitmapFont font) {
        this.align(Align.center);
        this.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        for (int i = 0; i < msg.length(); i++){
            char c = msg.charAt(i);

            Label label = new Label(String.valueOf(c), labelStyle);
            this.add(label);

        }

        float distance = 1f;


        for(int i = 0; i < this.getChildren().size; i++){
            Label label = (Label) this.getChildren().get(i);
            if(i % 2 == 0){
                label.addAction(forever(sequence(Actions.moveBy(0, -distance, 0.1f), Actions.moveBy(0, distance, 0.1f))));
            }
            else{
                label.addAction(forever(sequence(Actions.moveBy(0, distance, 0.1f), Actions.moveBy(0, -distance, 0.1f))));
            }
        }
    }




}
