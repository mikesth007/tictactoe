package com.msh7.game.tictactoe.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.util.SpriteAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

public class Splash extends State {

    private Texture texture;
    private Sprite sprite;
    private TweenManager tweenManager;

    public Splash(GSM gsm) {
        super(gsm);

        texture = new Texture(Gdx.files.internal("img/logo_try.png"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        sprite = new Sprite(texture);
        sprite.setColor(1, 1, 1, 0);
        sprite.setX(Gdx.graphics.getWidth() / 2 - (sprite.getWidth() / 2));
        sprite.setY(Gdx.graphics.getHeight() / 2 - (sprite.getHeight() / 2));

        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
        startTween();
    }

    @Override
    public void render(SpriteBatch batch) {
        Assets.getInstance().manager.update();
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    @Override
    public void update(float dt) {
        tweenManager.update(dt);
    }

    private void startTween() {
        Tween.to(sprite, SpriteAccessor.OPACITY, 0.75f).target(1).repeatYoyo(1, 0.75f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                if (Assets.getInstance().manager.update()) {
                    MenuState menuState = new MenuState(gsm);
                    CheckeredTransitionState fadeTransitionState = new CheckeredTransitionState(gsm, Splash.this, menuState);
                    gsm.set(fadeTransitionState);
                } else
                    startTween();

            }
        }).start(tweenManager);
    }
}
