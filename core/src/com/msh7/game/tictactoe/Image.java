package com.msh7.game.tictactoe;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Image {

    private Texture image;
    private float x;
    private float y;
    private float w;
    private float h;

    public Image(Texture image, float x, float y) {
        this.image = image;
        this.x = x;
        this.y = y;
        w = image.getWidth();
        h = image.getHeight();
    }

    public void render(SpriteBatch sb) {
        sb.draw(image, x - w / 2, y - h / 2);
    }

    public boolean contains(float mx, float my) {
        return mx > x - w / 2 && mx < x + w / 2 &&
                my > y - h / 2 && my < y + h / 2;
    }
}
