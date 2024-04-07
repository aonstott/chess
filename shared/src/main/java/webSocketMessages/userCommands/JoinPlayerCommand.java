package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand{

    private final ChessGame.TeamColor teamColor;
    public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(authToken, gameID);
        this.commandType = CommandType.JOIN_PLAYER;
        this.teamColor = teamColor;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
