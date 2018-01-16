package com.msh7.game.tictactoe.ai;

import com.msh7.game.tictactoe.util.Cell;
import com.msh7.game.tictactoe.util.CellType;
import com.msh7.game.tictactoe.util.Grid;

/**
 * Abstract superclass for all AI players with different strategies.
 * To construct an AI player:
 * 1. Construct an instance (of its subclass) with the game Board
 * 2. Call setSeed() to set the computer's seed
 * 3. Call move() which returns the next move in an int[2] array of {row, col}.
 * <p>
 * The implementation subclasses need to override abstract method move().
 * They shall not modify Cell[][], i.e., no side effect expected.
 * Assume that next move is available, i.e., not game-over yet.
 */
public abstract class AIPlayer {

    protected int ROWS;
    protected int COLS;
    protected CellType[][] cells; // the board's ROWS-by-COLS array of Cells
    protected CellType myCellType;    // computer's seed
    protected CellType oppCellType;   // opponent's seed

    /**
     * Constructor with reference to game board
     */
    public AIPlayer(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        cells = new CellType[rows][cols];
    }

    /**
     * Set/change the seed used by computer and opponent
     */
    public void setSeed(CellType cellType) {
        this.myCellType = cellType;
        oppCellType = (myCellType == CellType.X) ? CellType.O : CellType.X;
    }

    /**
     * Abstract method to get next move. Return int[2] of {row, col}
     */
    public abstract int[] move(CellType[][] cells, boolean isHard);  // to be implemented by subclasses
}