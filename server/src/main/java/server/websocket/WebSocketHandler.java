package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.Gson;
import Exception.ResponseException;
import dataAccess.SqlDataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.AuthData;
import service.GameData;
import service.GameService;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService = new GameService(new SqlDataAccess());

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);

        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(message, session);
            case JOIN_OBSERVER -> joinObserver(message, session);
            case MAKE_MOVE -> makeMove(message, session);
            case LEAVE -> leaveGame(message, session);
            case RESIGN -> resignGame(message, session);
        }
    }

    public void joinPlayer(String message, Session session) throws ResponseException {
        JoinPlayerCommand cmd = new Gson().fromJson(message, JoinPlayerCommand.class);
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthString();
        ChessGame.TeamColor teamColor = cmd.getPlayerColor();
        connections.add(gameID, auth, session);
        String username = gameService.getUsername(new AuthData(auth));
        var message1 = String.format("Player %s has joined team: %s", username, teamColor.toString());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.getGame();
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        try {
            connections.broadcast(gameID, auth, notification);
            connections.sendOneLoadCommand(gameID, auth, loadGameMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void joinObserver(String message, Session session) throws ResponseException {
        JoinObserverCommand cmd = new Gson().fromJson(message, JoinObserverCommand.class);
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthString();
        connections.add(gameID, auth, session);
        String username = gameService.getUsername(new AuthData(auth));
        var message1 = String.format("Player %s has joined as observer", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        try {
            connections.broadcast(gameID, auth, notification);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String message, Session session) throws ResponseException{
        MakeMoveCommand cmd = new Gson().fromJson(message, MakeMoveCommand.class);
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthString();
        String username = gameService.getUsername(new AuthData(auth));
        ChessGame game = gameService.getGame(gameID).getGame();
        ChessMove move = cmd.getMove();
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
        gameService.makeMove(gameID, new AuthData(auth), move);
        var message1 = String.format("Player %s has moved %s from %s to %s", username, piece.toString(), move.getStartPosition().toString(), move.getEndPosition().toString());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        game = gameService.getGame(gameID).getGame();
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);


        if (game.isInCheckmate(ChessGame.TeamColor.BLACK))
        {
            game.setTeamTurn(null);
            gameService.setGame(gameID, new AuthData(auth) , game);
            var endGameNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "White wins!");
            try {
                connections.broadcast(gameID, null, endGameNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE))
        {
            game.setTeamTurn(null);
            gameService.setGame(gameID, new AuthData(auth) , game);
            var endGameNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "Black wins!");
            try {
                connections.broadcast(gameID, null, endGameNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
        try {
            connections.broadcast(gameID, auth, notification);
            connections.sendLoadCommand(gameID, loadGameMessage);
        } catch (IOException e) {
            throw new ResponseException(400, "makeMove wsHandler: " + e.getMessage());
        }
    }

    public void leaveGame(String message, Session session) throws ResponseException {
        LeaveCommand leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
        int gameID = leaveCommand.getGameID();
        String auth = leaveCommand.getAuthString();
        String username = gameService.getUsername(new AuthData(auth));
        var message1 = String.format("Player %s has left", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        try {
            connections.removeSessionFromGame(gameID, auth);
            connections.removeSession(session);
            connections.broadcast(gameID, auth, notification);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resignGame(String message, Session session) throws ResponseException {
        ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
        int gameID = resignCommand.getGameID();
        String auth = resignCommand.getAuthString();
        String username = gameService.getUsername(new AuthData(auth));
        var message1 = String.format("Player %s has resigned", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);

        ChessGame game = gameService.getGame(gameID).getGame();
        game.setTeamTurn(null);
        gameService.setGame(gameID, new AuthData(auth), game);
        try {
            connections.broadcast(gameID, auth, notification);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

}
