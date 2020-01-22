package mlg.party.games.tictactoe;


import mlg.party.games.BasicGame;
import mlg.party.games.tictactoe.websocket.requests.TicTacToeMoveRequest;
import mlg.party.games.tictactoe.websocket.responses.TicTacToeErrorResponse;
import mlg.party.games.tictactoe.websocket.responses.TicTacToeMoveResponse;
import mlg.party.lobby.lobby.Player;

import java.io.IOException;
import java.util.List;

public class TicTacToeGame extends BasicGame<TicTacToeGame, TicTacToeSocketHandler> {

    private final String endpoint;
    private TicTacToeLogic gameLogic;

    public TicTacToeGame(String lobbyId, List<Player> participants, String endpoint) {
        super(lobbyId, participants);
        this.endpoint = endpoint;
        this.gameLogic= new TicTacToeLogic(participants);
    }

    @Override
    public void startGame() {
        socketHandler.registerNewGameInstance(this);
    }

    @Override
    public String getGameName() {
        return "TicTacToe";
    }

    @Override
    public String getGameEndpoint() {
        return endpoint;
    }

    public void handleMoveRequest(TicTacToeMoveRequest request){
            int moveResult= gameLogic.newMoveAttempt(request.x,request.y,request.playerId);

            if(moveResult/100 == 2){
                //Notify players about the new gameboard/ gamestatus
                if(moveResult==200){
                   TicTacToeMoveResponse response= new TicTacToeMoveResponse(request.playerId,request.lobbyId,request.x,request.y);
                    try {
                        socketHandler.sendMessageToPlayers(this,response);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        socketHandler.getLogger().error("TicTacToeMoveResponse",e.getMessage());
                    }
                }else{
                    manageGameFinished(moveResult, request.playerId);
                    return;
                }
            }else{
                //TODO:Sending errors only to Player who tried to place the wrong move
                TicTacToeErrorResponse response;
                if(moveResult==400 || moveResult==402){
                    response= new TicTacToeErrorResponse("Error: NotYourTurn!");
                }else {
                    response= new TicTacToeErrorResponse("Error: InvalidField!");
                }
                try {
                    socketHandler.sendMessageToPlayers(this,response);
                } catch (IOException e) {
                    socketHandler.getLogger().error("TicTacToeFinishResponse",e.getMessage());
                }
            }


    }

    private void manageGameFinished(int result, String playerId) {
        if(result==201){
            super.notifyGameFinished(this, "playerId");
        }else {
            super.notifyGameFinished(this, "TIE");
        }

        socketHandler.removeGameInstance(this);
        socketHandler.redirectToNextGame(this);
    }
}
