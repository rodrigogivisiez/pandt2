package com.mygdx.potatoandtomato.scenes.input_name_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.controls.BtnColor;
import com.mygdx.potatoandtomato.controls.PTTextField;
import com.mygdx.potatoandtomato.models.Services;

/**
 * Created by SiongLeng on 10/1/2016.
 */
public class InputNameScene extends SceneAbstract {

    TextField displayNameTextField;
    BtnColor btnConfirm;

    public InputNameScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    public TextField getDisplayNameTextField() {
        return displayNameTextField;
    }

    public BtnColor getBtnConfirm() {
        return btnConfirm;
    }

    @Override
    public void populateRoot() {

        _root.pad(20);

        Table questionTable = new Table();
        questionTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));
        questionTable.pad(15);

        Label.LabelStyle questionLabelStyle = new Label.LabelStyle();
        questionLabelStyle.fontColor = Color.valueOf("fff6d8");
        questionLabelStyle.font = _assets.getFonts().get(Fonts.FontId.PIZZA_XXL_REGULAR_S_a05e00_1_1);
        Label questionLabel = new Label(_texts.askForName(), questionLabelStyle);
        questionLabel.setWrap(true);
        questionLabel.setAlignment(Align.center);

        displayNameTextField = new PTTextField(_assets);
        displayNameTextField.setText(_services.getProfile().getGameName());

        btnConfirm = new BtnColor(BtnColor.ColorChoice.GREEN, _assets);
        btnConfirm.setText(_texts.confirm());

        questionTable.add(questionLabel).expandX().fillX();
        questionTable.row();
        questionTable.add(displayNameTextField).expandX().fillX().padTop(20);
        questionTable.row();
        questionTable.add(btnConfirm).width(150).padTop(20);

        _root.add(questionTable).expandX().fillX().padBottom(30);
    }
}
