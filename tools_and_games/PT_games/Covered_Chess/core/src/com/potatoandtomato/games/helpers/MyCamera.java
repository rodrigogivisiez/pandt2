package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by SiongLeng on 31/12/2015.
 */
public class MyCamera extends OrthographicCamera {

    BoundingBox left, right, top, bottom = null;

    public void setWorldBounds(int left, int bottom, int width, int height) {
        int top = bottom + height;
        int right = left + width;

        this.left = new BoundingBox(new Vector3(left - 2, 0, 0), new Vector3(left -1, top, 0));
        this.right = new BoundingBox(new Vector3(right + 1, 0, 0), new Vector3(right + 2, top, 0));
        this.top = new BoundingBox(new Vector3(0, top + 1, 0), new Vector3(right, top + 2, 0));
        this.bottom = new BoundingBox(new Vector3(0, bottom - 1, 0), new Vector3(right, bottom - 2, 0));
    }

    Vector3 lastPosition;
    @Override
    public void translate(float x, float y) {
        lastPosition = new Vector3(position);
        super.translate(x, y);
    }

    public void translateSafe(float x, float y) {
        translate(x, y);
        update();
        ensureBounds();
        update();
    }

    public void ensureBounds() {
        if (frustum.boundsInFrustum(left) || frustum.boundsInFrustum(right) || frustum.boundsInFrustum(top) || frustum.boundsInFrustum(bottom)) {
            position.set(lastPosition);
        }
    }

}
