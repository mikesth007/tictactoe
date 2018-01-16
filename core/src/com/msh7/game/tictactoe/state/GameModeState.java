package com.msh7.game.tictactoe.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.Image;
import com.msh7.game.tictactoe.TextButton;
import com.msh7.game.tictactoe.TicTacToeGame;

public class GameModeState extends State {

    private final Sprite sprite;
    private boolean soundMode;
    private TextButton easy;
    private TextButton hard;

    private Image title;

    private BitmapFont subtitleFont;
    private String choose = "Select Difficulty";

    private final GlyphLayout glyphLayout = new GlyphLayout();

    public GameModeState(GSM gsm) {

        super(gsm);
        Gdx.input.setInputProcessor(this);

        title = new Image(Assets.getInstance().manager.get(Assets.game_logo), TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 + 150);

        easy = new TextButton("Easy", TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 - 30);
        hard = new TextButton("Hard", TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 - 100);

        subtitleFont = Assets.getInstance().manager.get(Assets.designFont);

        soundMode = Gdx.app.getPreferences(TicTacToeGame.MY_PREFS).getBoolean("sound", true);

        Texture background = Assets.getInstance().manager.get(Assets.background);
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        sprite = new Sprite(background);

        float bw = background.getHeight();
        float gw = Gdx.graphics.getHeight();
        sprite.setScale(gw / bw);

        sprite.setColor(1, 1, 1, 1);
        sprite.setX(Gdx.graphics.getWidth() / 2 - (sprite.getWidth() / 2));
        sprite.setY(Gdx.graphics.getHeight() / 2 - (sprite.getHeight() / 2));
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.setColor(Color.WHITE);
        sb.draw(sprite, 0, 0);
        title.render(sb);
        sb.setColor(TicTacToeGame.PRIMARY_COLOR);
        easy.render(sb);
        hard.render(sb);
        subtitleFont.setColor(Color.WHITE);
        glyphLayout.setText(subtitleFont, choose);
        subtitleFont.draw(sb, glyphLayout, TicTacToeGame.WIDTH / 2 - glyphLayout.width / 2, TicTacToeGame.HEIGHT / 2 + 60);
        sb.end();

    }

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        unproject(m, cam);
        if (easy.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            GameState nextState = new GameState(gsm, false);
            CheckeredTransitionState state = new CheckeredTransitionState(gsm, this, nextState);
            gsm.set(state);
        } else if (hard.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            GameState nextState = new GameState(gsm, true);
            CheckeredTransitionState state = new CheckeredTransitionState(gsm, this, nextState);
            gsm.set(state);
        }
        return true;
    }

}
