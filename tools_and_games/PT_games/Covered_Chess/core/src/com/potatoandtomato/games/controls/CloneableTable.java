package com.potatoandtomato.games.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by SiongLeng on 20/2/2016.
 */
public class CloneableTable extends Table {

    public Table clone(){
        Table table = new Table();
        table.setSize(this.getWidth(), this.getHeight());
        table.setBackground(this.getBackground());

        for(Actor actor : this.getChildren()){
            if(actor instanceof Image){
                Image img2 = new Image(((Image)actor).getDrawable());
                table.add(img2);
            }
        }

        table.setTransform(true);
        return table;
    }


}
