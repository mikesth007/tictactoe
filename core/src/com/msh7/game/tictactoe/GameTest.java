package com.msh7.game.tictactoe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.msh7.game.tictactoe.util.CellType;
import com.msh7.game.tictactoe.util.GameResult;
import com.msh7.game.tictactoe.util.GameUtil;
import com.msh7.game.tictactoe.util.Grid;
import com.msh7.game.tictactoe.util.GridListener;

/**
 * Created by root on 4/21/17.
 */

public class GameTest extends Game implements InputProcessor {
    Grid grid;
    SpriteBatch sb;
    Texture bg, logo;
    private OrthographicCamera camera;
    private int moveCount;
    private CellType currentType;

    @Override
    public void create() {
        camera = new OrthographicCamera(TicTacToeGame.WIDTH, TicTacToeGame.HEIGHT);
        camera.position.set(TicTacToeGame.WIDTH / 2, TicTacToeGame.HEIGHT / 2, 0);
        sb = new SpriteBatch();
        Assets.getInstance().load();
        currentType = CellType.X;
        Assets.getInstance().manager.finishLoading();
        logo = Assets.getInstance().manager.get(Assets.game_logo);
        grid = new Grid(5, 5);
        grid.setListener(new GridListener() {
            @Override
            public void gameWon(int clickedRow, int clickedCol) {
                System.out.println("WON");
            }

            @Override
            public void gameDraw(int clickedRow, int clickedCol) {
                System.out.println("DRAW");
            }

            @Override
            public void keepPlaying(int clickedRow, int clickedCol) {
                currentType = currentType == CellType.X ? CellType.O : CellType.X;
            }

            @Override
            public void checkWinner(int clickedRow, int clickedCol, CellType cellType) {
                moveCount++;
                GameResult result = GameUtil.move(grid.getGrid(), clickedRow, clickedCol, cellType, moveCount);
                if (result == GameResult.RUNNING) {
                    currentType = currentType == CellType.X ? CellType.O : CellType.X;
                } else if (result == GameResult.WON) {
                    System.out.println("WON");
                } else if (result == GameResult.DRAW) {
                    System.out.println("DRAW");
                }
            }

            @Override
            public void onFinished() {

            }
        });
        bg = Assets.getInstance().manager.get(Assets.background);
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        sb.setProjectionMatrix(camera.combined);
        camera.update();
        sb.begin();
        sb.draw(bg, 0, 0);
        sb.draw(logo, Gdx.graphics.getWidth() / 2 - logo.getWidth() / 2, Gdx.graphics.getHeight() - 2 * logo.getHeight());
        if (grid != null) {
            grid.render(sb);
            grid.update(Gdx.graphics.getDeltaTime());
        }
        sb.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int p, int b) {
        Vector3 m = new Vector3(x, y, 0);
        camera.unproject(m);
        grid.click(m.x, m.y, currentType);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int p) {
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
