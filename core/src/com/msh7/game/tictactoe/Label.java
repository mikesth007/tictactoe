package com.msh7.game.tictactoe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Label {
    public enum LabelType {
        TITLE,
        SUBTITLE,
        LABEL
    }

    private float x;
    private float y;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private BitmapFont font;
    private String text;

    public Label(String text, LabelType type, float x, float y) {
        this.x = x;
        this.y = y;
        this.text = text;
        switch (type) {
            case TITLE:
                font = Assets.getInstance().manager.get(Assets.title_font);
                break;
            case SUBTITLE:
                font = Assets.getInstance().manager.get(Assets.subtitle_font);
                break;
            case LABEL:
            default:
                font = Assets.getInstance().manager.get(Assets.font);
        }
        font.setColor(Color.BLACK);
    }

    public void setColor(Color color) {
        font.setColor(color);
    }

    public void render(SpriteBatch sb) {
        glyphLayout.setText(font, text);
        font.draw(sb, glyphLayout, x - glyphLayout.width / 2, y - glyphLayout.height / 2);
    }

}
