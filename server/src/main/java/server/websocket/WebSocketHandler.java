package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import Exception.ResponseException;
import dataAccess.DataAccess;
import dataAccess.SqlDataAccess;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameData;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private ChessGame game = null;
    private final DataAccess dataAccess = new SqlDataAccess();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case JOIN_PLAYER -> beginSession(cmd.getAuthString(), session);
        }
    }

    private void beginSession(String auth, Session session) throws IOException {
        connections.add(auth, session);
    }

    public void joinGame(String auth, int gameID) throws IOException {
        GameData gameData = dataAccess.getGame(gameID);
        this.game = gameData.getGame();
        var message = String.format("New user has joined game: %d", gameID);
        var notification = new Notification(Notification.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(auth, notification);
    }

    public void makeMove(String auth, ChessMove move) throws IOException{
        ChessGame.TeamColor teamColor = game.getTeamTurn();
        ChessPiece movePiece = game.getBoard().getPiece(move.getStartPosition());
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println(e.getMessage());
        }

        var message = String.format("Team %s Moved %s from %s to %s", teamColor, movePiece, move.getStartPosition().toString(), move.getEndPosition().toString());
        var notification = new Notification(Notification.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(auth, notification);
    }

    public void leave(String auth) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification., message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
