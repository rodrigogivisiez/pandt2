package com.potatoandtomato.games.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class ImageDetails {

    private CorrectArea area1;
    private CorrectArea area2;
    private CorrectArea area3;
    private CorrectArea area4;
    private CorrectArea area5;
    private int width;
    private int height;
    private int index;
    private String imageOneUrl;
    private String imageTwoUrl;
    private String id;

    private int gameImageWidth;
    private int gameImageHeight;
    private ArrayList<Rectangle> correctRectangles;

    public ImageDetails() {
        this.correctRectangles = new ArrayList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getImageOneUrl() {
        return imageOneUrl;
    }

    public void setImageOneUrl(String imageOneUrl) {
        this.imageOneUrl = imageOneUrl;
    }

    public String getImageTwoUrl() {
        return imageTwoUrl;
    }

    public void setImageTwoUrl(String imageTwoUrl) {
        this.imageTwoUrl = imageTwoUrl;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public CorrectArea getArea5() {
        return area5;
    }

    public void setArea5(CorrectArea area5) {
        this.area5 = area5;
    }

    public CorrectArea getArea4() {
        return area4;
    }

    public void setArea4(CorrectArea area4) {
        this.area4 = area4;
    }

    public CorrectArea getArea3() {
        return area3;
    }

    public void setArea3(CorrectArea area3) {
        this.area3 = area3;
    }

    public CorrectArea getArea2() {
        return area2;
    }

    public void setArea2(CorrectArea area2) {
        this.area2 = area2;
    }

    public CorrectArea getArea1() {
        return area1;
    }

    public void setArea1(CorrectArea area1) {
        this.area1 = area1;
    }


    //game size has to be set by logic in runtime
    @JsonIgnore
    public void setGameImageSize(int gameImageWidth, int gameImageHeight) {
        this.gameImageWidth = gameImageWidth;
        this.gameImageHeight = gameImageHeight;
    }


    @JsonIgnore
    public ArrayList<Rectangle> getCorrectRects() {
        if(correctRectangles.size() == 0){
            correctRectangles.add(convertAreaToRectangle(area1));
            correctRectangles.add(convertAreaToRectangle(area2));
            correctRectangles.add(convertAreaToRectangle(area3));
            correctRectangles.add(convertAreaToRectangle(area4));
            correctRectangles.add(convertAreaToRectangle(area5));
        }

        return correctRectangles;
    }

    @JsonIgnore
    public SimpleRectangle getTouchedCorrectRect(float touchedX, float touchedY){
        for(Rectangle rectangle : getCorrectRects()){
            if(rectangle.contains(touchedX, touchedY)){
                return new SimpleRectangle(rectangle);
            }
        }
        return null;
    }

    public Rectangle convertAreaToRectangle(CorrectArea area){

        Vector2 topLeft = convertCoords(area.getTopLeftX(), area.getTopLeftY(), width, height, gameImageWidth, gameImageHeight);
        Vector2 topRight = convertCoords(area.getBottomRightX(), area.getTopLeftY(), width, height, gameImageWidth, gameImageHeight);
        Vector2 bottomLeft = convertCoords(area.getTopLeftX(), area.getBottomRightY(), width, height, gameImageWidth, gameImageHeight);
        Vector2 bottomRight = convertCoords(area.getBottomRightX(), area.getBottomRightY(), width, height, gameImageWidth, gameImageHeight);

        return new Rectangle(topLeft.x, topLeft.y, topRight.x - topLeft.x, bottomLeft.y - topLeft.y);
    }


    //original is the size of picture in PhotoHuntCreator, final is the size of picture in game
    public Vector2 convertCoords(int x, int y, int originalWidth, int originalHeight, int finalWidth, int finalHeight){
        int finalX = (finalWidth * x) / originalWidth;
        int finalY = (finalHeight * y) / originalHeight;

        return new Vector2(finalX, finalY);
    }

}
