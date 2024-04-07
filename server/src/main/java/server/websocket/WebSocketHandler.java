package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
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
        }
    }

    public void joinPlayer(String message, Session session) throws ResponseException {
        JoinPlayerCommand cmd = new Gson().fromJson(message, JoinPlayerCommand.class);
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthString();
        ChessGame.TeamColor teamColor = cmd.getTeamColor();
        connections.add(gameID, auth, session);
        String username = gameService.getUsername(new AuthData(auth));
        var message1 = String.format("Player %s has joined team: %s", username, teamColor.toString());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        try {
            connections.broadcast(gameID, auth, notification);
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

    public void makeMove(String auth, ChessMove move) throws IOException{

    }

    public void leaveGame(String auth) throws IOException {

    }

    public void resignGame()
    {

    }

}
