package com.potatoandtomato.common.controls;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by SiongLeng on 9/5/2016.
 */
public class AutoDisposeTable extends Table {


    @Override
    public void clearChildren() {
        disposeChildren(this);
        super.clearChildren();
    }

    @Override
    public void clear() {
        super.clear();
    }

    public void dispose(){
        disposeChildren(this);
    }

    private void disposeChildren(Group group){
        for(Actor actor : group.getChildren()){
            if(actor instanceof Disposable){
                ((Disposable) actor).dispose();
            }
            else if(actor instanceof Group){
                disposeChildren((Group) actor);
            }
        }
    }

}
