package com.msh7.game.tictactoe.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.Image;
import com.msh7.game.tictactoe.TextButton;
import com.msh7.game.tictactoe.TicTacToeGame;

public class MenuState extends State {

    private final Sprite sprite;
    private final Preferences prefs;
    private TextButton singlePlayer;
    private TextButton multiplayerOffline;
    private TextButton multiplayerOnline;

    private Image title;
    private Image soundOn;
    private Image soundOff;
    private Image settings;
    private Image rate;

    private boolean soundMode;

    private BitmapFont subtitleFont;
    private String choose = "How Would You Like To Play?";

    private final GlyphLayout glyphLayout = new GlyphLayout();

    public MenuState(GSM gsm) {

        super(gsm);
        Gdx.input.setInputProcessor(this);

        title = new Image(Assets.getInstance().manager.get(Assets.game_logo), TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 + 150);

        singlePlayer = new TextButton("Single Player", TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 - 30);
        multiplayerOffline = new TextButton("MultiPlayer - Offline", TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 - 100);
        multiplayerOnline = new TextButton("MultiPlayer - Online", TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2 - 170);

        prefs = Gdx.app.getPreferences(TicTacToeGame.MY_PREFS);

        soundMode = prefs.getBoolean("sound", true);
        soundOn = new Image(Assets.getInstance().manager.get(Assets.sound_on), TicTacToeGame.WIDTH / 2 - 96, 64);
        soundOff = new Image(Assets.getInstance().manager.get(Assets.sound_off), TicTacToeGame.WIDTH / 2 - 96, 64);
        settings = new Image(Assets.getInstance().manager.get(Assets.settings), TicTacToeGame.WIDTH / 2, 64);
        rate = new Image(Assets.getInstance().manager.get(Assets.rate), TicTacToeGame.WIDTH / 2 + 96, 64);

        subtitleFont = Assets.getInstance().manager.get(Assets.designFont);

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
        if (soundMode)
            soundOn.render(sb);
        else soundOff.render(sb);
        settings.render(sb);
        rate.render(sb);
        sb.setColor(TicTacToeGame.PRIMARY_COLOR);
        singlePlayer.render(sb);
        multiplayerOffline.render(sb);
        multiplayerOnline.render(sb);
        subtitleFont.setColor(Color.WHITE);
        glyphLayout.setText(subtitleFont, choose);
        subtitleFont.draw(sb, glyphLayout, TicTacToeGame.WIDTH / 2 - glyphLayout.width / 2, TicTacToeGame.HEIGHT / 2 + 60);
        sb.end();

    }

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        unproject(m, cam);
        if (singlePlayer.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            GameModeState nextState = new GameModeState(gsm);
            CheckeredTransitionState state = new CheckeredTransitionState(gsm, this, nextState);
            gsm.set(state);
        } else if (multiplayerOffline.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            MultiPlayerGameState nextState = new MultiPlayerGameState(gsm, false);
            CheckeredTransitionState state = new CheckeredTransitionState(gsm, this, nextState);
            gsm.set(state);
        } else if (multiplayerOnline.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            if (gsm.getGoogleServices().isSignedIn()) {
                gsm.getGoogleServices().QuickGame();
            } else
                gsm.getGoogleServices().SignIn();
        } else if (soundOn.contains(m.x, m.y) && soundMode) {
            if (soundMode)
                Assets.getInstance().playClick();
            soundMode = !soundMode;
            prefs.putBoolean("sound", soundMode).flush();
        } else if (soundOff.contains(m.x, m.y) && !soundMode) {
            if (soundMode)
                Assets.getInstance().playClick();
            soundMode = !soundMode;
            prefs.putBoolean("sound", soundMode).flush();
        } else if (settings.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            //open settings
        } else if (rate.contains(m.x, m.y)) {
            if (soundMode)
                Assets.getInstance().playClick();
            gsm.getGoogleServices().rate();
        }
        return true;
    }

}
