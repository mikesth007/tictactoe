package com.msh7.game.tictactoe.util;

public class GameUtil {
    public static GameResult move(Cell[][] board, int x, int y, CellType s, int moveCount) {
        if (board[x][y].getType() == CellType.EMPTY) {
            board[x][y].setCellType(s);
        }

        int n = board[0].length;

        if (checkWinnerFast(board, x, y, s)) {
            System.out.println(s.name() + " wins in " + moveCount);
            return GameResult.WON;
        }

        //check draw
        if (moveCount == (n * n)) {
            //report draw
            System.out.println("DRAW  ::: " + moveCount);
            return GameResult.DRAW;
        }

        //if not found anything
        //check if game is worth playing or already draw
        if (moveCount > (n * (n - 1)) && moveCount < (n * n)) {
            if (!checckCanStillWin(board, CellType.O)) {
                if (!checckCanStillWin(board, CellType.X)) {
                    System.out.println("GET OVER IT DUDE ITS A DRAW");
                    return GameResult.DRAW;
                }
            }
        }
        return GameResult.RUNNING;
    }


    private static boolean checckCanStillWin(Cell[][] board, CellType state) {
        int n = board[0].length;
        CellType[][] temp = new CellType[n][n];

        System.out.println("Checking if has possible winner :: " + state.name());
        boolean canWin = false;
        //check state- winner
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                temp[i][j] = board[i][j].getType() == CellType.EMPTY ? state : board[i][j].getType();
                if (checkWinnerFast(temp, i, j, state)) {
                    canWin = true;
                    break;
                }
            }
        }

        if (canWin) {
            System.out.println(state.name() + " can win");
        }
        return canWin;
    }

    public static boolean checkWinnerFast(Cell[][] temp, int x, int y, CellType s) {
        int n = temp[0].length;
        int col = 0, row = 0, diag = 0, rdiag = 0;
        for (int i = 0; i < n; i++) {
            if (temp[x][i].getType() == s)
                col++;
            if (temp[i][y].getType() == s)
                row++;
            if (temp[i][i].getType() == s)
                diag++;
            if (temp[i][n - (i + 1)].getType() == s)
                rdiag++;
        }
        return (row == n || col == n || diag == n || rdiag == n);
    }

    public static boolean checkWinnerFast(CellType[][] temp, int x, int y, CellType s) {
        int n = temp[0].length;
        int col = 0, row = 0, diag = 0, rdiag = 0;
        for (int i = 0; i < n; i++) {
            if (temp[x][i] == s)
                col++;
            if (temp[i][y] == s)
                row++;
            if (temp[i][i] == s)
                diag++;
            if (temp[i][n - (i + 1)] == s)
                rdiag++;
        }
        return (row == n || col == n || diag == n || rdiag == n);
    }

    public static boolean checkWinner(CellType[][] cells, CellType thePlayer) {
        // check horizontal win
        int rowCount = cells[0].length;
        int colCount = cells[0].length;
        int count;
        for (int row = 0; row < rowCount; row++) {
            count = 0;
            for (int col = 0; col < colCount; col++) {
                if (cells[row][col] == thePlayer) {
                    count++;
                }
            }
            if (count == rowCount) {
                System.out.println("HORIZONTAL WIN");
                return true;
            }
        }

        for (int row = 0; row < rowCount; row++) {
            count = 0;
            for (int col = 0; col < colCount; col++) {
                if (cells[col][row] == thePlayer) {
                    count++;
                }
            }
            if (count == rowCount) {
                System.out.println("VERTICAL WIN");
                return true;
            }
        }

        count = 0;
        for (int row = 0; row < rowCount; row++) {

            if (cells[row][row] == thePlayer)
                count++;

        }
        if (count == rowCount) {
            System.out.println("DIAGONAL WIN");
            return true;
        }

        count = 0;
        for (int row = 0; row < rowCount; row++) {
            if (cells[row][rowCount - 1 - row] == thePlayer)
                count++;
        }
        if (count == rowCount) {
            System.out.println("ANTI-DIAGONAL WIN");
            return true;
        }
        return false;
    }

    public static int evalScore(CellType[][] cells, CellType thePlayer) {
        // check horizontal win
        int rowCount = cells[0].length;
        int colCount = cells[0].length;
        int score = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                if (cells[row][col] == thePlayer)
                    score++;
                if (cells[row][col] != thePlayer)
                    score--;
            }
        }

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                if (cells[col][row] == thePlayer)
                    score++;
                if (cells[col][row] != thePlayer)
                    score--;
            }
        }

        for (int row = 0; row < rowCount; row++) {

            if (cells[row][row] == thePlayer)
                score++;
            if (cells[row][row] != thePlayer)
                score--;
        }

        for (int row = 0; row < rowCount; row++) {
            if (cells[row][rowCount - 1 - row] == thePlayer)
                score++;
            if (cells[row][rowCount - 1 - row] != thePlayer)
                score--;
        }
        return score;
    }

    public static boolean checkDraw(Cell[][] cells) {
        if (!checckCanStillWin(cells, CellType.O)) {
            if (!checckCanStillWin(cells, CellType.X)) {
                System.out.println("SCORE DRAW");
                return true;
            }
        }
        return false;
    }
}