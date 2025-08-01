package  TicTacToeP;

import java.util.Scanner;


/**
 * Player class holds all the fields and methods for a player in TicTacToe.
 */
public class Player {
    private final int id;
    private final String name;
    private final char marker;
    private final Scanner sc;
    private int numberOfWins;

    /**
     * Player constructor.
     *
     * @param name   - player's name
     * @param id     - player's id
     * @param marker - player's mark
     */
    public Player(String name, int id, char marker) {
        this.id = id;
        this.name = name;
        this.numberOfWins = 0;
        this.marker = marker;
        sc = new Scanner(System.in);
    }

    /**
     * @return - the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return - name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return - marker
     */
    public char getMarker() {
        return this.marker;
    }

    /**
     * @return - scanner
     */
    public Scanner getSC() {
        return this.sc;
    }

    /**
     * close scanner.
     */
    public void closeSC() {
        this.sc.close();
    }

    /**
     * @return - number if wins
     */
    public int getNumberOfWins() {
        return this.numberOfWins;
    }

    /**
     * add 1 to the number of wins.
     */
    public void incrementNumberOfWins() {
        this.numberOfWins++;
    }

    /**
     * reset the number of wins.
     */
    public void resetNumberOfWins() {
        this.numberOfWins = 0;
    }

    /**
     * player places their mark on the board.
     *
     * @param board - the given board
     */
    public void move(Board board) {
        String movePos;

        while (true) {
            System.out.println("Player " + this.name + ", please enter your move. (enter a value from 1 - "
                    + board.getBoardSize() * board.getBoardSize() + ")");
            board.print();

            if (sc.hasNextLine()) {
                movePos = sc.nextLine();

                if (!board.isValidPosition(movePos)) {
                    System.out.println("Invalid move. Please try again.");
                } else {
                    break;
                }
            }
        }

        board.placeTheMove(this.marker, Integer.parseInt(movePos));
    }
}
