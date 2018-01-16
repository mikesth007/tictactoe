package com.msh7.game.tictactoe.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.TicTacToeGame;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;

public class Cell {

    private CellType cellType;

    private float x;
    private float y;

    private static float introTime = 0.2f;
    private float timer = 0;
    private float size = 0;
    private float cell_size = 0;

    private Texture bg;
    private Sprite selected = null;

    private AnimationListener listener;

    private final TweenManager tweenManager = new TweenManager();

    public Cell(float x, float y, float cell_size) {
        cellType = CellType.EMPTY;
        this.x = x;
        this.y = y;
        this.cell_size = cell_size;
        bg = Assets.getInstance().manager.get(Assets.grid_cell);

        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    public void setListener(AnimationListener listener) {
        this.listener = listener;
    }

    public void makeMove(CellType cellType) {
        this.cellType = cellType;
        if (cellType == CellType.X)
            selected = new Sprite(Assets.getInstance().manager.get(Assets.cell_x));
        else if (cellType == CellType.O)
            selected = new Sprite(Assets.getInstance().manager.get(Assets.cell_o));

        //float scale = selected.getWidth() / (cell_size);
        float scale = cell_size / (2 * selected.getWidth());
        selected.setScale(scale);
        Timeline.createParallel()
                .push(Tween.to(selected, SpriteAccessor.OPACITY, 0.5f).target(0f).repeatYoyo(1, 0f).ease(Cubic.IN))
                .push(Tween.to(selected, SpriteAccessor.ROTATION, 0.5f).target(360 * 10).ease(Quad.OUT))
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        listener.onFinished();
                    }
                })
                .start(tweenManager);
    }

    public void setTimer(float f) {
        timer = f;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
        if (cellType == CellType.EMPTY) {
            selected = null;
        }
    }

    public CellType getType() {
        return cellType;
    }

    public boolean contains(float mx, float my) {
        return mx > x && mx < x + cell_size &&
                my > y && my < y + cell_size;
    }

    public void update(float dt) {
        if (timer < introTime) {
            timer += dt;
            if (timer >= introTime) {
                timer = introTime;
            }
            if (timer < 0) {
                size = 0;
            } else {
                size = (timer / introTime) * (cell_size);
            }
        }
    }

    public void render(SpriteBatch sb) {
        tweenManager.update(Gdx.graphics.getDeltaTime());
        //sb.setColor(Color.ORANGE);
        sb.setColor(TicTacToeGame.CELL_COLOR);
        sb.draw(bg, x, y, cell_size, cell_size);
        sb.setColor(Color.WHITE);
        if (cellType != CellType.EMPTY && selected != null) {
            sb.setColor(selected.getColor());
            sb.draw(selected, x + cell_size / 2 - selected.getWidth() / 2, y + cell_size / 2 - selected.getHeight() / 2, selected.getOriginX(), selected.getOriginY(),
                    selected.getWidth(), selected.getHeight(), selected.getScaleX(), selected.getScaleY(), selected.getRotation());
        }
    }

}