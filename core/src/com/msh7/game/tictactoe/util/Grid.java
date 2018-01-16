package com.msh7.game.tictactoe.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.msh7.game.tictactoe.Assets;
import com.msh7.game.tictactoe.TicTacToeGame;

public class Grid implements AnimationListener {

    private int PADDING;

    private Texture bg;

    private Cell[][] grid;
    private int numRows;
    private int numCols;

    private float x;
    private float y;

    private int moveCount;

    private Texture logo;
    private GridListener listener;

    private float cell_size;
    private int CELL_PADDING = 8;
    private int WIDTH, HEIGHT;

    public Cell[][] getGrid() {
        return grid;
    }

    public Grid(int rowC, int colC) {

        numRows = rowC;
        numCols = colC;
        grid = new Cell[numRows][numCols];

        int width = TicTacToeGame.WIDTH;
        int height = TicTacToeGame.HEIGHT;

        cell_size = Assets.getInstance().manager.get(Assets.grid_cell).getWidth();
        cell_size = width / (numCols + 1);

        PADDING = (width - (int) cell_size * numCols - CELL_PADDING * numCols) / 2;
        x = PADDING;

        y = (height - (cell_size * numRows) - CELL_PADDING * numRows) / 2;
        y = height - y;

        WIDTH = width - PADDING * 2;

        HEIGHT = (int) (cell_size * numRows + CELL_PADDING * numRows);

        logo = Assets.getInstance().manager.get(Assets.game_logo);

        float timer = 0f;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Cell cell = new Cell(
                        x + col * cell_size + col * CELL_PADDING,
                        y - cell_size - row * cell_size - row * CELL_PADDING, cell_size);
                cell.setListener(this);
                timer -= 0.08f;
                cell.setTimer(timer);
                grid[row][col] = cell;
            }
        }

        y = height - y;
        bg = Assets.getInstance().manager.get(Assets.grid_bg);
    }

    public void setListener(GridListener listener) {
        this.listener = listener;
    }

    public void click(float mx, float my, CellType cellType) {
        int clickedRow = -1;
        int clickedCol = -1;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col].contains(mx, my)) {
                    clickedRow = row;
                    clickedCol = col;
                    break;
                }
            }
            if (clickedRow > -1)
                break;
        }

        if (clickedCol == -1 || clickedRow == -1)
            return;
        move(clickedRow, clickedCol, cellType);
    }

    public void move(int clickedRow, int clickedCol, CellType cellType) {
        System.out.println("TRYING TO MOVE");
        if (grid[clickedRow][clickedCol].getType() == CellType.EMPTY) {
            if (Gdx.app.getPreferences(TicTacToeGame.MY_PREFS).getBoolean("sound", true))
                Assets.getInstance().playBeep();
            grid[clickedRow][clickedCol].makeMove(cellType);
            moveCount++;
            GameResult result = GameUtil.move(grid, clickedRow, clickedCol, cellType, moveCount);
            if (result == GameResult.RUNNING) {
                listener.keepPlaying(clickedRow, clickedCol);
            } else if (result == GameResult.WON) {
                listener.gameWon(clickedRow, clickedCol);
            } else if (result == GameResult.DRAW) {
                listener.gameDraw(clickedRow, clickedCol);
            }
        } else {
            System.out.println("ALREADY FILLED");
        }
    }

    public void update(float dt) {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                grid[row][col].update(dt);
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.draw(logo, TicTacToeGame.WIDTH / 2 - logo.getWidth() / 2, y + HEIGHT + CELL_PADDING + 40);
        sb.setColor(TicTacToeGame.GRID_BG_COLOR);
        sb.draw(bg, x - CELL_PADDING, y, WIDTH + CELL_PADDING, HEIGHT + CELL_PADDING);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                grid[row][col].render(sb);
            }
        }

    }

    public void resetGame() {
        for (Cell[] cells : grid) {
            for (Cell cell : cells) {
                cell.setCellType(CellType.EMPTY);
            }
        }
        moveCount = 0;
    }

    private int animationCount;

    @Override
    public void onStarted() {
        animationCount++;
    }

    @Override
    public void onFinished() {
        animationCount--;
        if (animationCount == 0 && listener != null) {
            listener.onFinished();
        }
    }

}