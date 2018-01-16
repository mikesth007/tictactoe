package com.msh7.game.tictactoe.state;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.TicTacToeGame;

public class FadeTransitionState extends TransitionState {

    private Texture pixel;
    private float alpha;

    public FadeTransitionState(GSM gsm, State prevState, State nextState) {
        super(gsm, prevState, nextState);
        pixel = Assets.getInstance().manager.get(Assets.pixel);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        alpha = timer < totalTime / 2 ? timer / (totalTime / 2) : 1 - (timer - totalTime / 2) / (totalTime / 2);
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 1) {
            alpha = 1;
        }
        if (timer >= totalTime) {
            finish();
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        if (timer < totalTime / 2) {
            prevState.render(sb);
        } else {
            nextState.render(sb);
        }
        sb.begin();
        sb.setColor(1, 1, 1, alpha);
        sb.draw(pixel, 0, 0, TicTacToeGame.WIDTH, TicTacToeGame.HEIGHT);
        sb.end();
    }

}
