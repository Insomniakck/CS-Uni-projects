package TicTacToeP;

import java.util.Scanner;

/**
 * Class TicTacToe holds to game information and runs the game.
 */
public final class TicTacToe {
    private static Scanner sc;
    private Player player1 = new Player("PLAYER-X", 1, 'X');
    private Player player2 = new Player("PLAYER-O", 2, 'O');
    private Board board;

    /**
     * TicTacToe constructor.
     */
    public TicTacToe() {
        sc = new Scanner(System.in);
    }

    /**
     * TicTacToe constructor.
     *
     * @param player1 - a Player
     * @param player2 - another Player
     */
    public TicTacToe(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        sc = new Scanner(System.in);
    }

    /**
     * runs the game.
     */
    public void play() {
        Player currentPlayer = this.player1;
        if (!this.playAgain()) {
            this.gameOver();
            return;
        }

        while (true) {
            currentPlayer.move(this.board);
            if (this.board.checkWin(currentPlayer.getMarker())) {
                this.handleWinner(currentPlayer);
                if (!this.playAgain()) {
                    this.gameOver();
                    return;
                }
            } else if (this.board.isFull()) {
                System.out.println("The board is full. It's a tie!");
                if (!this.playAgain()) {
                    this.gameOver();
                    return;
                } else {
                    continue;
                }
            }

            currentPlayer = currentPlayer == this.player1 ? this.player2 : this.player1;
        }
    }

    private void gameOver() {
        this.printResults();
        sc.close();
        this.player1.closeSC();
        this.player2.closeSC();
    }

    void handleWinner(Player winner) {
        winner.incrementNumberOfWins();
        System.out.println(winner.getName() + " has won this round!");
    }

    /**
     * prints an introduction to the game.
     */
    private void welcome() {
        System.out.println("Hit \"y/Y\" to start a new game. Or hit any other key to exit.");
    }

    /**
     * @return - the board size
     */
    private int getBoardSize() {
        while (true) {
            System.out.print("Please enter your preferred SIZE of the board");
            System.out.println(" (from 3 to 10. 3 -> 3x3; 4 -> 4x4; 10 -> 10x10, etc): ");

            if (sc.hasNextLine()) {
                String userInput = sc.nextLine();
                if (this.verifyBoardSize(userInput)) {
                    return Integer.parseInt(userInput);
                }
            }
        }
    }

    /**
     * verifies that the board size is between 3-10.
     *
     * @param boardSize - the given size
     * @return - true or false
     */
    boolean verifyBoardSize(String boardSize) {
        try {
            int i = Integer.parseInt(boardSize);
            return !(i < 3 || Integer.parseInt(boardSize) > 10);
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * checks if the player wants to play another round.
     *
     * @return - true / false
     */
    private boolean playAgain() {
        this.welcome();
        sc = new Scanner((System.in));
        String userDecision = sc.nextLine();

        if (userDecision.equalsIgnoreCase("Y")) {
            int boardSize = this.getBoardSize();
            this.board = new Board(boardSize);
            return true;
        }

        return false;
    }

    /**
     * prints the overall game results.
     */
    public void printResults() {
        System.out.println("Player " + this.player1.getName() + " has won: "
                + this.player1.getNumberOfWins() + " time(s).");
        System.out.println("Player " + this.player2.getName() + " has won: "
                + this.player2.getNumberOfWins() + " time(s).");

        if (this.player1.getNumberOfWins() == this.player2.getNumberOfWins()) {
            System.out.println("Its a tie!");
        } else {
            String winner = this.player1.getNumberOfWins() > this.player2.getNumberOfWins()
                    ? this.player1.getName() : this.player2.getName();
            System.out.println("The final winner is: " + winner + "!!!");
        }

        System.out.println();
    }
}
