package com.potatoandtomato.games.absintf.mockings;

import com.potatoandtomato.games.models.CorrectArea;
import com.potatoandtomato.games.models.ImageDetails;
import com.potatoandtomato.games.models.ImagePair;
import com.potatoandtomato.games.models.Services;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class MockModel {

    public static ImagePair mockImagePair(){
        ImagePair imagePair = new ImagePair(mockImageDetails(), null, null, 0);
        return imagePair;
    }

    public static ImageDetails mockImageDetails(){
        ImageDetails imageDetails = new ImageDetails();
        imageDetails.setHeight(400);
        imageDetails.setWidth(600);

        CorrectArea area1 = new CorrectArea();
        area1.setTopLeftX(120);
        area1.setTopLeftY(72);
        area1.setBottomRightX(190);
        area1.setBottomRightY(134);

        CorrectArea area2 = new CorrectArea();
        area2.setTopLeftX(1);
        area2.setTopLeftY(0);
        area2.setBottomRightX(79);
        area2.setBottomRightY(84);

        CorrectArea area3 = new CorrectArea();
        area3.setTopLeftX(166);
        area3.setTopLeftY(1);
        area3.setBottomRightX(217);
        area3.setBottomRightY(35);

        CorrectArea area4 = new CorrectArea();
        area4.setTopLeftX(386);
        area4.setTopLeftY(192);
        area4.setBottomRightX(456);
        area4.setBottomRightY(271);

        CorrectArea area5 = new CorrectArea();
        area5.setTopLeftX(285);
        area5.setTopLeftY(273);
        area5.setBottomRightX(440);
        area5.setBottomRightY(394);

        imageDetails.setArea1(area1);
        imageDetails.setArea2(area2);
        imageDetails.setArea3(area3);
        imageDetails.setArea4(area4);
        imageDetails.setArea5(area5);

        imageDetails.setId("1");

        return imageDetails;

    }



}
