package mlg.party.games.tictactoe;

import com.google.gson.JsonSyntaxException;

import mlg.party.lobby.lobby.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TicTacToeLogicTest {
    private TicTacToeLogic gameLogic;
    private  int[][] testBoard;
    private List<Player> players;
    final String player1="Alex";
    final String player2="Max";


    @Before
    public void before(){
        players=new ArrayList<Player>(2);
        players.add(new Player(player1,"WOW"));
        players.add(new Player(player2,"notWOW"));
        System.out.println(players.get(0));
        gameLogic=new TicTacToeLogic(players);
        //Creating an empty board
        testBoard= new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
    }

    @Test
    public void simpleInsertionTest(){
        int x=0;
        int y=1;
        gameLogic.newMoveAttempt(x,y,player1);
        testBoard[x][y]=1;
        Assert.assertArrayEquals(gameLogic.getGameBoard(), testBoard);
    }

    @Test
    public void simpleInsertionReturnTest(){
        Assert.assertEquals(200 , gameLogic.newMoveAttempt(0,2,player1));
    }

    @Test
    public void duplicateInsertionTest(){
        testBoard= new int[][]{
                {0, 0, 1},
                {0, 0, 0},
                {0, 0, 0}
        };
        gameLogic.setGameBoard(testBoard);
        Assert.assertEquals(401,gameLogic.newMoveAttempt(0,2,player1));
    }

    @Test
    public void notCurrentPlayerTest(){
        gameLogic.newMoveAttempt(0,0,player1);
        Assert.assertEquals(400,gameLogic.newMoveAttempt(0,0,player1));
    }

    @Test
    public void wrongPlayerDuplicateInsertTest(){
        testBoard= new int[][]{
                {2, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        gameLogic.setGameBoard(testBoard);
        Assert.assertEquals(401,gameLogic.newMoveAttempt(0,0,player2));
    }
    @Test
    public void winHorizontalTest(){
        testBoard= new int[][]{
                {2, 1, 1},
                {2, 0, 2},
                {1, 1, 0}
        };
        gameLogic.setGameBoard(testBoard);
        Assert.assertEquals(201,gameLogic.newMoveAttempt(2,2,player1));
    }
    @Test
    public void winVerticalTest(){
        testBoard= new int[][]{
                {0, 1, 0},
                {0, 0, 2},
                {1, 0, 2}
        };
        gameLogic.newMoveAttempt(1,1,player1);//to shift current player to 2
        gameLogic.setGameBoard(testBoard);

        Assert.assertEquals(201,gameLogic.newMoveAttempt(0,2,player2));
    }
    @Test
    public void winDiagonalToprightToBotleftTest(){
        testBoard= new int[][]{
                {2, 0, 0},
                {0, 0, 0},
                {0, 0, 2}
        };
        gameLogic.newMoveAttempt(1,1,player1);//to shift current player to 2
        gameLogic.setGameBoard(testBoard);
        Assert.assertEquals(201,gameLogic.newMoveAttempt(1,1,player2));
    }
    @Test
    public void winDiagonalTopleftToBotleftTest(){
        testBoard= new int[][]{
                {0, 0, 1},
                {0, 1, 0},
                {0, 0, 0}
        };
        gameLogic.setGameBoard(testBoard);
        Assert.assertEquals(201,gameLogic.newMoveAttempt(2,0,player1));
    }
    @Test
    public void drawP1Test(){
        testBoard= new int[][]{
                {1, 2, 1},
                {1, 2, 2},
                {2, 1, 0}
        };
        gameLogic.setGameBoard(testBoard);
        Assert.assertEquals(202,gameLogic.newMoveAttempt(2,2,player1));
    }
    @Test
    public void drawP2Test(){
        testBoard= new int[][]{
                {1, 0, 2},
                {2, 1, 0},
                {1, 1, 2}
        };
        gameLogic.setGameBoard(testBoard);
        gameLogic.newMoveAttempt(1,2,player1);// To give turn to player 2
        Assert.assertEquals(202,gameLogic.newMoveAttempt(0,1,player2));
    }
    @Test
    public void SetAndGetBoardTest(){
        testBoard= new int[][]{
                {0, 2, 2},
                {2, 1, 1},
                {1, 2, 0}
        };
        gameLogic.setGameBoard(testBoard);
        Assert.assertArrayEquals(testBoard,gameLogic.getGameBoard());
    }
    @Test
    public void unknownPlayerTest(){
        Assert.assertEquals(402,gameLogic.newMoveAttempt(1,2,"UNKNOWN"));
    }

    @Test
    public void XOutOfBoundsTest(){
        Assert.assertEquals(404,gameLogic.newMoveAttempt(3,0,player1));
    }
    @Test
    public void YOutOfBoundsTest(){
        Assert.assertEquals(404,gameLogic.newMoveAttempt(2,-1,player1));
    }

}
