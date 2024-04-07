package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{

    private final ChessMove move;
    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID);
        this.commandType = CommandType.MAKE_MOVE;
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

}