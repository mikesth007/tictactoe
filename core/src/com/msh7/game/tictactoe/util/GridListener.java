package com.msh7.game.tictactoe.util;

/**
 * Created by root on 4/21/17.
 */

public interface GridListener {
    void gameWon(int clickedRow, int clickedCol);
    void gameDraw(int clickedRow, int clickedCol);
    void keepPlaying(int clickedRow, int clickedCol);
    void onFinished();
    void checkWinner(int clickedRow, int clickedCol, CellType cellType);
}
