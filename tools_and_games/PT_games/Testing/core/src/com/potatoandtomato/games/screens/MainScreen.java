package com.potatoandtomato.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameScreen;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.games.assets.Fonts;
import com.potatoandtomato.games.assets.Textures;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by SiongLeng on 20/7/2016.
 */
public class MainScreen extends GameScreen {

    private Assets assets;
    private Stage stage;
    private SpriteBatch batch;
    private BitmapFont font;
    FreeTypeFontGenerator generator;
    private  String string;

    public MainScreen(GameCoordinator gameCoordinator, Assets assets) {
        super(gameCoordinator);
        this.assets = assets;
        batch = getCoordinator().getSpriteBatch();
    }



    @Override
    public void show() {

        FreeTypeFontGenerator.setMaxTextureSize(128);

       // generator = new FreeTypeFontGenerator(Gdx.files.internal("data/arial.ttf"));

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Chinese-Regular.otf"));

        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.incremental = true;
        param.size = 24;
        param.characters = "��howdY\u0000";

        FreeTypeFontGenerator.FreeTypeBitmapFontData data = new FreeTypeFontGenerator.FreeTypeBitmapFontData() {
            public int getWrapIndex (Array<BitmapFont.Glyph> glyphs, int start) {
                return SimplifiedChinese.getWrapIndex(glyphs, start);
            }
        };

        // By default latin chars are used for x and cap height, causing some fonts to display non-latin chars out of bounds.
        data.xChars = new char[] {'a'};
        data.capChars = new char[] {'b'};

        font = generator.generateFont(param, data);



        final byte b = (byte) 255;
        int i = b & 0x5e;
        int i1 = b & 0x72;

        byte[] bytesArray = new byte[2]; // array of bytes (0xF0, 0x9F, 0x98, 0x81)
        bytesArray[0] = (byte) i;
        bytesArray[1] = (byte) i1;
        string = new String(bytesArray, Charset.forName("UTF-16"));

        final String s = "��";
        try {
            byte[] b1 = s.getBytes("UTF-8");
            System.out.println("sdsda");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        sample("abc��");


        //font = assets.getFonts().get(Fonts.FontId.CHINESE_XL_REGULAR);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        final Label label = new Label("", labelStyle);
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle(
        font, Color.BLACK,
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.EMPTY)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.EMPTY)),
                new TextureRegionDrawable(assets.getTextures().get(Textures.Name.FULL_WHITE_BG)));
        final TextField textField = new TextField("\u5e72\u5e72\u5e72\u5e73", textFieldStyle);

        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               String result = textField.getText();
                label.setText(textField.getText());

//                try {
//                    byte[] b1 = result.getBytes("UTF-8");
//                    String s = new String(b1, "UTF-8");
//                    label.setText(s);
//                    System.out.println("sdsda");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

            }
        });



        Table table = new Table();
        table.setFillParent(true);
        table.add(textField).size(200, 50).expand().fill();
        table.row();
        table.add(label).size(200, 50).expand().fill();

        stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()),
                                        getCoordinator().getSpriteBatch());
        stage.addActor(table);

        getCoordinator().addInputProcessor(stage);
    }

    public void sample(String input){
        for (final byte b : input.getBytes(Charset.forName("UTF-16"))) {
            int result = (b & 0xFF);
            System.out.printf("%1$02X ", (b & 0xFF));
        }
        System.out.println();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.draw();
        stage.act(delta);


    //    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Draw rects.
        float x = 0, y = Gdx.graphics.getHeight() - font.getRegion().getRegionHeight() - 1;
        for (int i = 0, n = font.getRegions().size; i < n; i++) {
            TextureRegion region = font.getRegions().get(i);
            x += region.getRegionWidth() + 2;
        }

        batch.begin();
        x = 0;
        for (int i = 0, n = font.getRegions().size; i < n; i++) {
            TextureRegion region = font.getRegions().get(i);
            batch.draw(region, x, y);
            x += region.getRegionWidth() + 2;
        }
        font.draw(batch, "LYA" + string, 10, 300); // Shows kerning.
        font.draw(batch, "hello world" + string, 100, 300);
        font.draw(batch,
                "�ɸɸɸɸ�\u5e72�����ܸ���Ϸ�����������������������Ŵ���һ������Ķ�����������Ҫǿ���������ߣ�����Ҫһ��ţ B �Ĺ������̡�" //
                        + "Spineרע�ڴˣ�Ϊ���������޵Ĺ������������������ϵ���Ϸ���У��ṩ��һ�׸�Ч�Ĺ������̡�",
                10, 250, //
                Gdx.graphics.getWidth() - 20, Align.left, true);
        batch.end();


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    static public class SimplifiedChinese {
        public static int getWrapIndex (Array<BitmapFont.Glyph> glyphs, int start) {
            int i = start - 1;
            for (; i >= 1; i--) {
                int startChar = glyphs.get(i).id;
                if (!SimplifiedChinese.legalAtStart(startChar)) continue;
                int endChar = glyphs.get(i - 1).id;
                if (!SimplifiedChinese.legalAtEnd(endChar)) continue;
                // Don't wrap between ASCII chars.
                if (startChar < 127 && endChar < 127 && !Character.isWhitespace(startChar)) continue;
                return i;
            }
            return start;
        }

        static private boolean legalAtStart (int ch) {
            switch (ch) {
//                case '!':
//                case '%':
//                case ')':
//                case ',':
//                case '.':
//                case ':':
//                case ';':
//                case '>':
//                case '?':
//                case ']':
//                case '}':
//                case '?':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '�D':
//                case '��':
//                case '��':
//                case '��':
//                case '?':
//                case '?':
//                case '?':
//                case '?':
//                case '?':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '�e':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '?':
//                case '�w':
//                case '�y':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
                  //  return false;
            }
            return true;
        }

        static private boolean legalAtEnd (int ch) {
//            switch (ch) {
//                case '$':
//                case '(':
//                case '*':
//                case ',':
//                case '?':
//                case '?':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '�u':
//                case '�v':
//                case '�x':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                case '��':
//                    return false;
//            }
            return true;
        }
    }

}
