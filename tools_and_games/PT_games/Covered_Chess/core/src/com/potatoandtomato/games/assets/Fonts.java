package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.FontAssets;
import com.potatoandtomato.common.assets.FontDetailsGenerator;

/**
 * Created by SiongLeng on 9/2/2016.
 */
public class Fonts extends FontAssets {

    public Fonts(PTAssetsManager _manager) {
        super(_manager);
    }

    @Override
    public void loadFonts() {
        for (FontId id : FontId.values()) {
            addPreloadParameter(id);
        }
    }

    @Override
    public void setFontDetailsGenerator() {
        this.fontDetailsGenerator = new MyFontDetailsGenerator();
    }


    public enum FontId{
        MYRIAD_S_REGULAR,
        MYRIAD_M_REGULAR,
        MYRIAD_M_REGULAR_B_000000_588e54_1,             //dark green
        MYRIAD_XXL_REGULAR,

        HELVETICA_XS_BlACKCONDENSEDITALIC,
        HELVETICA_XL_HEAVY,
        HELVETICA_XXL_BlACKCONDENSEDITALIC_B_ffffff_56380a_1,
        HELVETICA_MAX_BlACKCONDENSEDITALIC_B_ffffff_f0c266_2_S_000000_1_1,         //orange border, black shadow
        HELVETICA_MAX_BlACKCONDENSEDITALIC_B_ffffff_f46767_2_S_000000_1_1,          //red border, black shadow

        PIZZA_XXL_REGULAR,
        PIZZA_XXXL_REGULAR_B_000000_ffffff_3,
    }


    private class MyFontDetailsGenerator extends FontDetailsGenerator {

        @Override
        public String getPath(String fontNameString, String fontStyleString) {
            FontName fontName = FontName.valueOf(fontNameString);
            FontStyle fontStyle = FontStyle.valueOf(fontStyleString);

            String path = "";
            switch (fontName){
                case MYRIAD:
                    path = "fonts/MyriadPro-%s.otf";
                    break;
                case PIZZA:
                    path = "fonts/Pizza-%s.otf";
                    break;
                case HELVETICA:
                    path = "fonts/Helvetica-%s.otf";
                    break;
            }

            String styleName = "";
            switch (fontStyle){
                case SEMIBOLD:
                    styleName = "Semibold";
                    break;
                case REGULAR:
                    styleName = "Regular";
                    break;
                case BOLD:
                    styleName = "Bold";
                    break;
                case CONDENSED:
                    styleName = "Condensed";
                    break;
                case ITALIC:
                    styleName = "It";
                    break;
                case HEAVY:
                    styleName = "Heavy";
                    break;
                case BlACKCONDENSEDITALIC:
                    styleName = "BlkCnO";
                    break;
            }

            return String.format(path, styleName);
        }

        @Override
        public int getSize(String fontSizeString) {
            FontSize fontSize = FontSize.valueOf(fontSizeString);
            switch (fontSize){
                case XS:
                    return 9;
                case S:
                    return 11;
                case M:
                    return 13;
                case L:
                    return 15;
                case XL:
                    return 17;
                case XXL:
                    return 20;
                case XXXL:
                    return 30;
                case MAX:
                    return 30;
            }
            return 0;
        }
    }

    private enum  FontName {
        PIZZA, MYRIAD, HELVETICA
    }

    private enum FontStyle {
        SEMIBOLD, REGULAR, BOLD, CONDENSED, ITALIC, HEAVY, BlACKCONDENSEDITALIC
    }

    private enum FontSize{
        XS, S, M, L, XL, XXL, XXXL, MAX
    }


}

