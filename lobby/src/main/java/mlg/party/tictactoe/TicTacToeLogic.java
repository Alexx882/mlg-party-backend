package at.aau.ase.mlg_party_app.tictactoe;
/*
    Handles the gamelogic
 */

class TicTacToeLogic {
    private int [][] gameBoard;
    final private int boardSize=3; // for easy acces instead of .length
    private int currentPlayer;
    TicTacToeLogic() {
       resetGameBoard();
        //Player 1 starts first
         this.currentPlayer=1;
    }
    /*
    Check if the move is valid,
    if yes -> apply the move
    after that check if the game is won.
    Parameter explanation:
    x,y -> where the Player wants to place his move
    playerId    -> used to identify the player (either 1 or 2)

    RETURN VALUES:
        20x -> Valid move
            1 = MOVE ADDED TO BOARD
            1 = CURRENT MOVE WON THE GAME
            2 = TIE
       40 -> ERROR
            0 = Not your turn
            1 = Not an empty field
            4 = Out of bounds
     */
    int checkMoveStatus(int x, int y, int playerId){
        int count=0;
        //Check if the move is valid
        //Right player?
        if(currentplayer != playerId)return 400;
        //Out of bounds? valid indexes are 0-2 for x and y
        if(x<0||x>2 ||y<0||y>2)return 404;
        //Field is not empty
        if(gameboard[x][y]!=0)return 401;

        //Update the game board (move is valid)
        gameBoard[x][y]=playerId;

        //Check if the move won vertically
        for(int j=0;j<gameBoard[x].length;j++){
            if(gameBoard[x][j]==playerId){
                count++;
            }else{
                break;
            }
        }
        if(count==3){
            return 201;
        }
        count=0;

        //Check if the move won horizontally
        for (int[] ints : gameBoard) {
            if (ints[y] == playerId) {
                count++;
            } else {
                break;
            }
        }
        if(count==3){
            return 201;
        }
        count=0;

        //Check if the Diagonal TopRight -> BottomLeft won
        for(int i=0;i<gameBoard.length;i++){
            if(gameBoard[i][i]==playerId){
                count++;
            }
        }
        if(count==3){
            return 201;
        }
        count=0;
        //Check if Diagonal BottomLeft -> TopRight won
        for(int i=0;i<gameBoard.length;i++){
            int tmp=gameBoard.length-1;
            if(gameBoard[tmp-i][i]==playerId){
                count++;
            }
        }
        if(count==3){
            return 201;
        }

        //Check if there is still a move possible
        if(checkDraw()) return 202;
        return 200;
    }

    //Run through all fields until one is 0
    private boolean checkDraw(){
        for(int [] i:gameBoard){
            for(int j:i){
                if(j==0)return false;
            }
        }
        return true;
    }

    //GETTER AND SETTERS
    int[][] getGameBoard() {
        return gameBoard;
    }

    //Mainly for Testing purposes -> to test for different boards.
    void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    void resetGameBoard() {
        //Creating new game board
        this.gameBoard= new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
    }

    int getBoardSize() {
        return boardSize;
    }
}
