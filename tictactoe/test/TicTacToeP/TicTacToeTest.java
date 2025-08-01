package TicTacToeP;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TicTacToeTest {

    @Test
    public void checkWinnerTest() {
        //small board 3X3
        Board checkRow1 = new Board(3);
        //check empty board + check row
        assertFalse(checkRow1.checkWin('X'));
        //check column
        Board checkColumn1 = new Board(3);
        //check main diagonal
        Board checkMainDiag1 = new Board(3);
        //check secondary diagonal
        Board checkSecondDiag1 = new Board(3);

        for(int i = 0;i<3;i++){
            checkRow1.placeTheMove('X',i+1);
            checkColumn1.placeTheMove('X',3*i +1);
            checkMainDiag1.placeTheMove('X',4*i +1);
            checkSecondDiag1.placeTheMove('X',3 +2*i);
        }
        assertTrue(checkRow1.checkWin('X'));
        assertTrue(checkColumn1.checkWin('X'));
        assertTrue(checkMainDiag1.checkWin('X'));
        assertTrue(checkSecondDiag1.checkWin('X'));

        //big board 10X10
        Board checkRow2 = new Board(10);
        //check empty board + check row
        assertFalse(checkRow2.checkWin('X'));
        //check column
        Board checkColumn2 = new Board(10);
        //check main diagonal
        Board checkMainDiag2 = new Board(10);
        //check secondary diagonal
        Board checkSecondDiag2 = new Board(10);


        for(int i = 0;i<10;i++){
            checkRow2.placeTheMove('X',i+1);
            checkColumn2.placeTheMove('X',10*i +1);
            checkMainDiag2.placeTheMove('X',11*i +1);
            checkSecondDiag2.placeTheMove('X',10 +9*i);
        }

        assertTrue(checkRow2.checkWin('X'));
        assertTrue(checkColumn2.checkWin('X'));
        assertTrue(checkMainDiag2.checkWin('X'));
        assertTrue(checkSecondDiag2.checkWin('X'));
    }

    @Test
    public void handleWinnerTest() {
        Player p1 = new Player("Moshe", 1, 'X');
        Player p2 = new Player("Marina", 2, 'O');
        TicTacToe game = new TicTacToe(p1,p2);
        game.handleWinner(p1);
        assertEquals(1,p1.getNumberOfWins());
        assertEquals(0,p2.getNumberOfWins());

        game.handleWinner(p2);
        assertEquals(1,p2.getNumberOfWins());

        game.handleWinner(p1);
        assertEquals(2,p1.getNumberOfWins());
        assertEquals(1,p2.getNumberOfWins());

    }

    @Test
    public void isValidPositionTest() {
        Board board1 = new Board(3);
        Board board2 = new Board(10);

        //checks if the position is within bounds
        assertFalse(board1.isValidPosition("0"));
        assertTrue(board1.isValidPosition("1"));
        assertFalse(board1.isValidPosition("10"));
        assertTrue(board1.isValidPosition("9"));
        assertFalse(board1.isValidPosition("-2"));
        assertFalse(board2.isValidPosition("0"));
        assertTrue(board2.isValidPosition("1"));
        assertFalse(board2.isValidPosition("110"));
        assertTrue(board2.isValidPosition("99"));
        assertFalse(board2.isValidPosition("-2"));

        //checks the result is false when the position is taken
        board1.placeTheMove('X',1);
        assertFalse(board1.isValidPosition("1"));

        //checks that there is not error if the input isn't an integer
        assertFalse(board1.isValidPosition("y"));

    }

    @Test
    public void isFullTest() {
        Board board1 = new Board(3);

        for (int i = 1; i < board1.getBoardSize() * board1.getBoardSize(); i++) {
            board1.placeTheMove('X', i);
            assertFalse(board1.isFull());
        }
        board1.placeTheMove('X', board1.getBoardSize() * board1.getBoardSize());
        assertTrue(board1.isFull());
    }

    @Test
    public void verifyBoardSizeTest() {

        TicTacToe game = new TicTacToe();
        //checks if the size is within bounds
        assertFalse(game.verifyBoardSize("1"));
        assertFalse(game.verifyBoardSize("2"));
        assertTrue(game.verifyBoardSize("3"));
        assertFalse(game.verifyBoardSize("15"));
        assertTrue(game.verifyBoardSize("7"));
        assertTrue(game.verifyBoardSize("10"));

        //checks that there is not error if the input isn't an integer
        assertFalse(game.verifyBoardSize("sfesf"));
    }

}
