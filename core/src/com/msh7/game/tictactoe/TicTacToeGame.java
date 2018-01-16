package com.msh7.game.tictactoe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.interfaces.IGoogleServices;
import com.msh7.game.tictactoe.state.Splash;
import com.msh7.game.tictactoe.state.GSM;
import com.msh7.game.tictactoe.state.MultiPlayerGameState;

public class TicTacToeGame extends Game {
    public static final String LOG = "TICTACTOEGAME";
    public static final int WIDTH = 480;
    public static final int HEIGHT = 800;
    public static final String MY_PREFS = "my_prefs";
    public static final Color GRID_BG_COLOR = new Color(0x303d3955);
    public static final Color GRID_BG_COLOR_DARK = new Color(0xFFFFFF99);
    public static final Color GAME_OVER_BG_COLOR = new Color(0x87CEEBAA);
    public static final Color PRIMARY_COLOR = new Color(0x303d39ff);
    public static final Color CELL_COLOR = new Color(0xFFFFFFBB);
    public final IGoogleServices googleServices;
    public SpriteBatch batch;
    private GSM gsm;

    public TicTacToeGame(IGoogleServices googleServices) {
        this.googleServices = googleServices;
        googleServices.setGame(this);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        gsm = new GSM(this);
        Assets.getInstance().load();
        gsm.push(new Splash(gsm));
    }

    @Override
    public void dispose() {
        Assets.getInstance().dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        super.render();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        gsm.render(batch);
        gsm.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    public void multiplayerGameReady(boolean meFirst) {
        gsm.push(new MultiPlayerGameState(gsm, true, meFirst));
    }

    public void opponentMove(int row, int col) {
        gsm.moveReceived(row, col);
    }

    public void playerLeft() {
        gsm.playerLeft();
    }

    public void messageReceived(int messageId) {
        gsm.messageReceived(messageId);
    }
}
