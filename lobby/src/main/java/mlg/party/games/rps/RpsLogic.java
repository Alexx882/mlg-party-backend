package mlg.party.games.rps;

import mlg.party.games.rps.websocket.requests.RpsResult;
import mlg.party.games.rps.websocket.responses.RpsReply;

import java.security.SecureRandom;

class RpsLogic {

    enum Result {DRAW, WON, LOST, ERROR}

    Result checkResult(RpsResult.Option userInput, RpsResult.Option enemyInput) {

        if (userInput == null || enemyInput == null) {
            return Result.ERROR;
        }

        if (userInput == enemyInput) {
            // Draw
            return Result.DRAW;
        } else if (userInput == RpsResult.Option.ROCK && enemyInput == RpsResult.Option.PAPER) {
            // Lost
            return Result.LOST;
        } else if (userInput == RpsResult.Option.ROCK && enemyInput == RpsResult.Option.SCISSOR) {
            // Won
            return Result.WON;
        } else if (userInput == RpsResult.Option.PAPER && enemyInput == RpsResult.Option.SCISSOR) {
            // Lost
            return Result.LOST;
        } else if (userInput == RpsResult.Option.PAPER && enemyInput == RpsResult.Option.ROCK) {
            // Won
            return Result.WON;
        } else if (userInput == RpsResult.Option.SCISSOR && enemyInput == RpsResult.Option.ROCK) {
            // Lost
            return Result.LOST;
        } else if (userInput == RpsResult.Option.SCISSOR && enemyInput == RpsResult.Option.PAPER) {
            // Won
            return Result.WON;
        }
        return Result.ERROR;
    }
}

