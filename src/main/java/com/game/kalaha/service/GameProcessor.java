package com.game.kalaha.service;

public class GameProcessor {

    /**
     * Generates new board
     *
     * @param pits   number of pits
     * @param stones number of stones in each of pits
     * @return 2D array, consists of two identical arrays rotated 180 degrees,
     * where the elements store the number of stones in the pits, and the last
     * element stores the contents of the large pit. By default game rules it
     * should be next:
     * [
     * [0, 6, 6, 6, 6, 6, 6],
     * [6, 6, 6, 6, 6, 6, 0]
     * ]
     */
    public static int[][] boardSetup(int pits, int stones) {
        int[][] board = new int[2][pits + 1];
        for (int i = 1; i < board[0].length; i++) {
            board[0][i] = stones;
        }
        for (int i = 0; i < board[1].length - 1; i++) {
            board[1][i] = stones;
        }
        return board;
    }

    /**
     * This method implements game play.
     *
     * @param board 2D array, consists of two arrays and stores current board state
     * @param row   selected row
     * @param col   selected column
     * @return current player has finished his turn and can no longer move until
     * the opponent makes his move
     */
    public static boolean sow(int[][] board, int row, int col) {
        int playerBigPitRow;
        int playerBigPitCol;
        int opponentBigPitRow;
        int opponentBigPitCol;
        if (row == 0) {
            playerBigPitRow = 0;
            playerBigPitCol = 0;
            opponentBigPitRow = 1;
            opponentBigPitCol = board[0].length - 1;
        } else {
            playerBigPitRow = 1;
            playerBigPitCol = board[0].length - 1;
            opponentBigPitRow = 0;
            opponentBigPitCol = 0;
        }
        int captured = board[row][col];
        board[row][col] = 0;
        while (captured > 0) {
            if (row == 0 && col == 0) {
                row = 1;
            } else if (row == 0) {
                col--;
            } else if (row == 1 && col == board[0].length - 1) {
                row = 0;
            } else if (row == 1) {
                col++;
            }
            if (!(row == opponentBigPitRow && col == opponentBigPitCol)) {
                board[row][col]++;
                captured--;
                if (captured == 0 && board[row][col] == 1
                        && !(row == playerBigPitRow && col == playerBigPitCol)
                        && row == playerBigPitRow) {
                    int shift;
                    if (playerBigPitRow == 0) {
                        shift = -1;
                    } else {
                        shift = 1;
                    }
                    board[playerBigPitRow][playerBigPitCol] = board[playerBigPitRow][playerBigPitCol]
                            + board[row][col] + board[opponentBigPitRow][col + shift];
                    board[row][col] = 0;
                    board[opponentBigPitRow][col + shift] = 0;
                }
            }
        }
        return row != playerBigPitRow || col != playerBigPitCol;
    }

    /**
     * Calculates the game status and process final state of the board.
     *
     * @param board 2D array, consists of two arrays and stores current board state
     * @return true if one of the sides or both sides run out of stones
     */
    public static boolean gameOver(int[][] board) {
        boolean beginnerHasStones = false;
        boolean opponentHasStones = false;
        for (int i = 1; i < board[0].length; i++) {
            beginnerHasStones = beginnerHasStones || board[0][i] != 0;
            opponentHasStones = opponentHasStones || board[1][i - 1] != 0;
            if (beginnerHasStones && opponentHasStones) {
                return false;
            }
        }
        if (beginnerHasStones) {
            for (int i = 1; i < board[0].length; i++) {
                board[0][0] = board[0][0] + board[0][i];
                board[0][i] = 0;
            }
        }
        if (opponentHasStones) {
            for (int i = 0; i < board[1].length - 1; i++) {
                board[1][board[1].length - 1] = board[1][board[1].length - 1] + board[1][i];
                board[1][i] = 0;
            }
        }
        return true;
    }

    /**
     * @param board 2D array, consists of two arrays and stores current board state
     * @return difference between number of captured stones, if the number is positive,
     * the beginner wins, if negative, then the opponent wins, if 0, then a draw
     */
    public static int compareScore(int[][] board) {
        return board[0][0] - board[1][board[1].length - 1];
    }


}
