package com.msh7.game.tictactoe.ai;

import com.badlogic.gdx.utils.Array;
import com.msh7.game.tictactoe.util.CellType;
import com.msh7.game.tictactoe.util.GameUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AIPlayer using Minimax algorithm
 */
public class ComputeAIPlayer extends AIPlayer {

    /**
     * Constructor with the given game board
     */
    public ComputeAIPlayer(int row, int col) {
        super(row, col);
    }

    /**
     * Get next best move for computer. Return int[2] of {row, col}
     */
    @Override
    public int[] move(CellType[][] board, boolean isHard) {
        this.cells = board;
        int[] result;
        if (isHard) {
            Float[] values = alphaBeta(myCellType, 0, -1F, 1F, true);
            return new int[]{values[1].intValue(), values[2].intValue()};
        } else
            result = simpleMove(myCellType);
        return result;   // row, col
    }

    private CellType getEnemy(CellType player) {
        return player == myCellType ? oppCellType : myCellType;
    }

    private boolean isGameOver(CellType cellType) {
        return (GameUtil.checkWinner(cells, cellType));
    }

    /**
     * Find all valid next moves.
     * Return List of moves in int[2] of {row, col} or empty list if gameover
     */
    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<int[]>(); // allocate List

        // If gameover, i.e., no next move
        if (hasWon(myCellType) || hasWon(oppCellType)) {
            return nextMoves;   // return empty list
        }

        // Search for empty cells and add to the List
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col] == CellType.EMPTY) {
                    nextMoves.add(new int[]{row, col});
                }
            }
        }
        System.out.println(nextMoves.size());
        return nextMoves;
    }

    private int[] simpleMove(CellType player) {

        //TODO
        //copy all values to temp first then only do some logic of checking winner by filling empty data
        System.out.println("SIMPLE");
        CellType[][] temp = new CellType[ROWS][COLS];

        Array<Index> emptyCells = new Array<Index>();
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                temp[row][col] = cells[row][col];
                if (temp[row][col] == CellType.EMPTY)
                    emptyCells.add(new Index(row, col));
            }
        }

        for (Index index : emptyCells) {
            int row = index.row;
            int col = index.col;
            temp[row][col] = player;
            if (GameUtil.checkWinnerFast(temp, row, col, player)) {
                return new int[]{row, col};
            }
            temp[row][col] = CellType.EMPTY;
        }

        player = getEnemy(player);

        for (Index index : emptyCells) {
            int row = index.row;
            int col = index.col;
            temp[row][col] = player;
            if (GameUtil.checkWinnerFast(temp, row, col, player)) {
                return new int[]{row, col};
            }
            temp[row][col] = CellType.EMPTY;
        }

        System.out.println("FIRST EMPTY SENDING");
        //find first empty Cell and return
        int random = new Random().nextInt(emptyCells.size);
        return new int[]{emptyCells.get(random).row, emptyCells.get(random).col};
    }

    class Index {
        public int row;
        public int col;

        public Index(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public Float[] score(CellType player, Integer depth) {
        if (player == myCellType) {
            return new Float[]{1F / depth};
        }
        if (player == null) {
            return new Float[]{0F};
        }
        return new Float[]{-1F / depth};
    }

    public Float[] alphaBeta(CellType player, Integer depth, Float alpha, Float beta, boolean maximizing) {
        List<int[]> moves = generateMoves();
        if (moves.size() == 0) {
            if (isGameOver(CellType.X)) {
                return score(CellType.X, depth);
            }

            if (isGameOver(CellType.O)) {
                return score(CellType.O, depth);
            }

            System.out.println("gsme over ::: draw");
            return score(null, depth);
        }

        Float bestRow = null;
        Float bestCol = null;
        Integer bestMove = 0;
        Float bestScore = null;
        if (maximizing) {
            for (int[] move : generateMoves()) {
                cells[move[0]][move[1]] = player;
                //Gdx.app.log(TicTacToe.COMPUTER_LOG+" MOVE", String.valueOf(move));	// Uncomment for logging
                Float score = alphaBeta(getEnemy(player), depth + 1, alpha, beta, false)[0];
                if (score > alpha) {
                    alpha = score;
                }
                cells[move[0]][move[1]] = CellType.EMPTY;

                if (bestScore == null || (player == myCellType && bestScore < score) || (player != myCellType && bestScore > score)) {
                    //Gdx.app.log(TicTacToe.COMPUTER_LOG+" Calculating Best Move", String.valueOf(move));	// Uncomment for logging
                    //Gdx.app.log(TicTacToe.COMPUTER_LOG+" Calculating Best SCORE", String.valueOf(score));	// Uncomment for logging
                    bestScore = score;
                    bestRow = (float) move[0];
                    bestCol = (float) move[1];
                }
            }
        } else {
            for (int[] move : generateMoves()) {
                cells[move[0]][move[1]] = player;
                //Gdx.app.log(TicTacToe.COMPUTER_LOG+" MOVE", String.valueOf(move));	// Uncomment for logging
                Float score = alphaBeta(getEnemy(player), depth + 1, alpha, beta, true)[0];
                cells[move[0]][move[1]] = CellType.EMPTY;
                if (score < beta) {
                    beta = score;
                }
                if (bestScore == null || (player == myCellType && bestScore < score) || (player != myCellType && bestScore > score)) {
                    //Gdx.app.log(TicTacToe.COMPUTER_LOG+" Calculating Best Move", String.valueOf(move));	// Uncomment for logging
                    //Gdx.app.log(TicTacToe.COMPUTER_LOG+" Calculating Best SCORE", String.valueOf(score));	// Uncomment for logging
                    bestScore = score;
                    bestRow = (float) move[0];
                    bestCol = (float) move[1];
                }
            }
        }
        //Gdx.app.log(TicTacToe.COMPUTER_LOG+" Calculated Best Move", String.valueOf(bestMove));	// Uncomment for logging
        return new Float[]{(depth == 0) ? bestMove : bestScore, bestRow, bestCol};
    }

    /**
     * Returns true if thePlayer wins
     */
    private boolean hasWon(CellType thePlayer) {
        return GameUtil.checkWinner(cells, thePlayer);
    }

}