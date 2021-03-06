package com.msh7.game.tictactoe;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextButton {

    private Texture image;
    private float x;
    private float y;
    private float width;
    private float height;

    private BitmapFont font;
    private String text;
    private float tWidth;
    private float tHeight;

    public TextButton(String text, float x, float y) {
        this.x = x;
        this.y = y;
        image = Assets.getInstance().manager.get(Assets.button);
        width = image.getWidth();
        height = image.getHeight();
        font = Assets.getInstance().manager.get(Assets.subtitle_font);
        setText(text);
    }

    public void setText(String text) {
        this.text = text;
        GlyphLayout gl = new GlyphLayout(font, text);
        tWidth = gl.width;
        tHeight = gl.height;
    }

    public boolean contains(float mx, float my) {
        return mx > x - width / 2 && mx < x + width / 2 &&
                my > y - height / 2 && my < y + height / 2;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(TicTacToeGame.PRIMARY_COLOR);
        sb.draw(image, x - width / 2, y - height / 2);
        font.setColor(1, 1, 1, 1);
        font.draw(sb, text, x - tWidth / 2, y + tHeight / 2);
    }

}
