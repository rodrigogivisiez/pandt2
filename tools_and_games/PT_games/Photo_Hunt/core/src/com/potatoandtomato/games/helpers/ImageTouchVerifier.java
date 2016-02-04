package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.potatoandtomato.games.models.CorrectArea;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 1/2/2016.
 */
public class ImageTouchVerifier {

    private ArrayList<CorrectArea> _correctAreas;
    private float _gameImageWidth, _gameImageHeight;    //the size of image in game, not the real image size
    private float _realImageWidth, _realImageHeight;

    public ImageTouchVerifier(String json, float gameImageWidth, float gameImageHeight) {
        this._gameImageHeight = gameImageHeight;
        this._gameImageWidth = gameImageWidth;
        _correctAreas = new ArrayList<CorrectArea>();
        extractJson(json);
    }

    private void extractJson(String json){
        try {
            json = json.replace("\\", "");
            JSONObject jsonObject = new JSONObject(json);
            _realImageHeight = jsonObject.getInt("height");
            _realImageWidth = jsonObject.getInt("width");
            for(int i = 1; i <=5; i++){
                populateCorrectAreas(jsonObject.getString(String.valueOf(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void populateCorrectAreas(String input){
        //500,149-553,191
        input = input.replace(",-", ",");   //remove redundant minus bug
        String[] tmp = input.split("-");
        String[] topLeft = tmp[0].split(",");
        String[] bottomRight = tmp[1].split(",");
        Integer topLeftX = Integer.valueOf(topLeft[0]);
        Integer topLeftY = Integer.valueOf(topLeft[1]);
        Integer bottomRightX = Integer.valueOf(bottomRight[0]);
        Integer bottomRightY = Integer.valueOf(bottomRight[1]);

        Vector2 topLeftCoord = CoordConverter.convert(topLeftX, topLeftY, _realImageWidth, _realImageHeight, _gameImageWidth, _gameImageHeight);
        Vector2 topRightCoord = CoordConverter.convert(bottomRightX, topLeftY, _realImageWidth, _realImageHeight, _gameImageWidth, _gameImageHeight);
        Vector2 bottomLeftCoord = CoordConverter.convert(topLeftX, bottomRightY, _realImageWidth, _realImageHeight, _gameImageWidth, _gameImageHeight);
        Vector2 bottomRightCoord = CoordConverter.convert(bottomRightX, bottomRightY, _realImageWidth, _realImageHeight, _gameImageWidth, _gameImageHeight);

        _correctAreas.add(new CorrectArea(topLeftCoord, topRightCoord, bottomLeftCoord, bottomRightCoord));
    }

    public CorrectArea getConvertedTouchedCorrectArea(float touchedX, float touchedY){
         for(CorrectArea area : _correctAreas){
            if(area.toRectangle().contains(touchedX, touchedY)){
                return area;
            }
        }
        return null;
    }

}
