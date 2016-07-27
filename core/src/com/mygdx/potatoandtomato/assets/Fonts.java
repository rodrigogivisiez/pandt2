package com.mygdx.potatoandtomato.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.FontAssets;
import com.potatoandtomato.common.assets.FontDetailsGenerator;
import com.potatoandtomato.common.assets.MyFreetypeFontLoader;
import com.potatoandtomato.common.utils.Strings;

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

    public BitmapFont get(FontId fontId){
        return get(fontId.name());
    }

    public enum FontId{
        PT_S_REGULAR,
        PT_S_REGULAR_B_ffffff_000000_1,
        PT_M_REGULAR,

        MYRIAD_XS_BOLD_B_ffffff_000000_1,
        MYRIAD_S_SEMIBOLD,
        MYRIAD_S_REGULAR,
        MYRIAD_S_REGULAR_B_ffffff_000000_1,
        MYRIAD_S_BOLD,
        MYRIAD_S_ITALIC,
        MYRIAD_M_REGULAR,
        MYRIAD_M_SEMIBOLD,
        MYRIAD_L_REGULAR,
        MYRIAD_L_BOLD,
        MYRIAD_XL_REGULAR,
        MYRIAD_XL_BOLD_S_000000_1_1,

        HELVETICA_XXS_REGULAR,
        HELVETICA_XS_REGULAR,
        HELVETICA_XS_BOLD,
        HELVETICA_S_HEAVYITALIC_B_ffffff_81562c_2,         //dark brown
        HELVETICA_S_HEAVYITALIC_B_ffffff_9e9d9c_2,          //gray
        HELVETICA_M_REGULAR,
        HELVETICA_M_BOLD,
        HELVETICA_M_HEAVY,
        HELVETICA_L_BOLD,
        HELVETICA_L_HEAVY,
        HELVETICA_XL_HEAVYITALIC_B_ffffff_81562c_2,         //dark brown
        HELVETICA_XL_CONDENSED_S_a05e00_1_1,       //dark orange
        HELVETICA_XXXL_REGULAR,

        PIZZA_S_REGULAR_B_ffffff_000000_1,
        PIZZA_M_REGULAR_B_ffffff_000000_1,
        PIZZA_L_REGULAR_S_a05e00_2_2,              //dark orange
        PIZZA_XL_REGULAR_S_a05e00_1_1,              //dark orange
        PIZZA_XXL_REGULAR_S_a05e00_1_1,             //dark orange
        PIZZA_XXL_REGULAR_B_ffffff_0f5673_1_S_0e516c_1_3, //dark blue
        PIZZA_XXL_REGULAR_B_000000_fed271_3,               //light orange
        PIZZA_XXL_REGULAR_B_000000_ffffff_3,               //light orange
        PIZZA_XXL_REGULAR_B_ffffff_000000_2,
        PIZZA_XXXL_REGULAR,

        CARTER_M_REGULAR_B_ffffff_000000_2,
        CARTER_L_REGULAR_B_ffffff_000000_2,

        IMPACT_XS_REGULAR_S_a74828_1_1,
        IMPACT_S_REGULAR_S_a74828_1_1,
        IMPACT_M_REGULAR,
        IMPACT_S_REGULAR,

        DIGIVOLVE_XS_REGULAR,
        DIGIVOLVE_S_REGULAR,
        DIGIVOLVE_M_REGULAR,
        DIGIVOLVE_XL_REGULAR_B_ffffff_580202_1,
        DIGIVOLVE_XXL_REGULAR,
        DIGIVOLVE_50_REGULAR,

        ARCADE_XS_REGULAR,
        ARCADE_S_REGULAR,

    }


    private class MyFontDetailsGenerator extends FontDetailsGenerator{

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
                case CARTER:
                    path = "fonts/CarterOne-%s.otf";
                    break;
                case IMPACT:
                    path = "fonts/Impact-%s.otf";
                    break;
                case DIGIVOLVE:
                    path = "fonts/Digivolve-%s.otf";
                    break;
                case ARCADE:
                    path = "fonts/Arcade-%s.otf";
                    break;
                case PT:
                    path = "fonts/PTFonts-%s.otf";
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
                case HEAVYITALIC:
                    styleName = "HvIt";
                    break;
            }

            return String.format(path, styleName);
        }

        @Override
        public int getSize(String fontSizeString) {
            if(Strings.isNumeric(fontSizeString)){
                return Integer.valueOf(fontSizeString);
            }
            FontSize fontSize = FontSize.valueOf(fontSizeString);
            switch (fontSize){
                case XXS:
                    return 8;
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
                    return 22;
                case MAX:
                    return 30;
            }
            return 0;
        }
    }

    private enum  FontName {
        PIZZA, MYRIAD, HELVETICA, CARTER, IMPACT, DIGIVOLVE, ARCADE, PT
    }

    private enum FontStyle {
        SEMIBOLD, REGULAR, BOLD, CONDENSED, ITALIC, HEAVY, HEAVYITALIC
    }

    private enum FontSize{
        XXS, XS, S, M, L, XL, XXL, XXXL, MAX
    }

}

