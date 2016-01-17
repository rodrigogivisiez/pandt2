package com.mygdx.potatoandtomato.models;

/**
 * Created by SiongLeng on 18/1/2016.
 */
public class NativeLibgdxTextInfo {

    String text;
    int cursorPosition;

    public NativeLibgdxTextInfo(String text, int cursorPosition) {
        this.text = text;
        this.cursorPosition = cursorPosition;
    }

    public String getText() {
        return text;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }
}
