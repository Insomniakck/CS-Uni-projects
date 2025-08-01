/**
 * Board class holds the game board information.
 */
package TicTacToeP;

/**
 * Board class holds the game board information for the game TicTacToe.
 */
public class Board {
    private int boardSize;
    private char[][] board;


    private Board() {
    }

    /**
     * Board constructor.
     *
     * @param boardSize - the size of the board
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.setBoard();
    }

    /**
     * @param boardSize - the new board size
     */
    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * @return - the board size
     */
    public int getBoardSize() {
        return this.boardSize;
    }

    private void setBoard() {
        this.board = new char[this.boardSize][this.boardSize];

        for (int i = 0; i < this.boardSize; ++i) {
            for (int j = 0; j < board[0].length; ++j) {
                board[i][j] = ' ';
            }
        }
    }

    /**
     * prints the board.
     */
    public void print() {
        StringBuilder topBottomBoundary = new StringBuilder();

        topBottomBoundary.append("+---".repeat(Math.max(0, this.boardSize)));
        topBottomBoundary.append("+");

        for (char[] row : this.board) {
            System.out.println(topBottomBoundary);

            for (char cell : row) {
                System.out.print("| " + cell + " ");
            }
            System.out.println("|");
        }
        System.out.println(topBottomBoundary);
        System.out.println();
    }

    /**
     * places a checkMark on the board.
     *
     * @param checkMark    - the checkMark
     * @param movePosition - the index of the board.
     */
    public void placeTheMove(char checkMark, int movePosition) {
        int i = (movePosition - 1) / this.board.length;
        int j = (movePosition - 1) % this.board.length;
        this.board[i][j] = checkMark;
    }

    /**
     * checks if the given player won.
     *
     * @param player - the player's checkMark
     * @return - true or false
     */
    public boolean checkWin(char player) {
        //checks rows
        int counter1 = 0;
        //checks columns
        int counter2 = 0;
        //checks first diag
        int counter3 = 0;
        //checks second diag
        int counter4 = 0;
        for (int i = 0; i < boardSize; i++) {
            counter1 = 0;
            counter2 = 0;
            if (board[i][i] == player) {
                counter3++;
            }
            if (board[i][boardSize - 1 - i] == player) {
                counter4++;
            }
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == player) {
                    counter1++;
                }
                if (board[j][i] == player) {
                    counter2++;
                }
            }
            if (counter1 == boardSize || counter2 == boardSize || counter3 == boardSize || counter4 == boardSize) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if the position input is in bounds.
     *
     * @param position - the placement in the 2D array
     * @return - true or false
     */
    public boolean isValidPosition(String position) {
        try {
            int k = Integer.parseInt(position);
            int i = (k - 1) / boardSize;
            int j = (k - 1) % boardSize;
            return !(k < 1 || k > boardSize * boardSize || board[i][j] != ' ');
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * checks if the board is full.
     *
     * @return - true or false
     */
    public boolean isFull() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
