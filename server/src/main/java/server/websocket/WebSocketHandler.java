package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
import java.util.Objects;


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
        connections.add(gameID, auth, session);

        if (gameService.getGame(gameID) == null)
        {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: bad gameID"));
                System.out.println("sent id error");
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        ChessGame.TeamColor teamColor = cmd.getPlayerColor();
        //check for errors
        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.getGame();
        String username = gameService.getUsername(new AuthData(auth));
        if (teamColor == ChessGame.TeamColor.WHITE)
        {
            if (!Objects.equals(gameData.getWhiteUsername(), username))
            {
                try {
                    connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Already taken!!"));
                    System.out.println("sent error");
                } catch (IOException e) {
                    throw new ResponseException(500, e.getMessage());
                }
                return;
            }
        }

        if (teamColor == ChessGame.TeamColor.BLACK)
        {
            if (!Objects.equals(gameData.getBlackUsername(), username))
            {
                try {
                    connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Already taken!!"));
                    System.out.println("sent error");
                } catch (IOException e) {
                    throw new ResponseException(500, e.getMessage());
                }
                return;
            }
        }


        var message1 = String.format("Player %s has joined team: %s", username, teamColor.toString());
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
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
        if (gameService.getGame(gameID) == null)
        {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: bad gameID"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        if (!gameService.checkAuth(new AuthData(auth)))
        {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: bad auth"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        GameData gameData = gameService.getGame(gameID);
        ChessGame game = gameData.getGame();
        String username = gameService.getUsername(new AuthData(auth));
        var message1 = String.format("Player %s has joined as observer", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        try {
            connections.broadcast(gameID, auth, notification);
            connections.sendOneLoadCommand(gameID, auth, loadGameMessage);
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
        GameData gameData = gameService.getGame(gameID);
        ChessMove move = cmd.getMove();
        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());

        try {
            gameService.makeMove(gameID, new AuthData(auth), move);
        } catch (ResponseException e) {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: bad move"));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }

        if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && !(Objects.equals(gameData.getWhiteUsername(), username))) ||
                (game.getTeamTurn() == ChessGame.TeamColor.BLACK && !(Objects.equals(gameData.getBlackUsername(), username))))
        {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: not your turn"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }


        var message1 = String.format("Player %s has moved %s from %s to %s", username, piece.getPieceType().toString(), convertPos(move.getStartPosition()), convertPos(move.getEndPosition()));
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message1);
        game = gameService.getGame(gameID).getGame();
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);

        if (game.isInCheck(ChessGame.TeamColor.BLACK)
        ) {
            var checkNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "Black is in check!");
            try {
                connections.broadcast(gameID, null, checkNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
        if (game.isInCheck(ChessGame.TeamColor.WHITE)
        ) {
            var checkNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, "White is in check!");
            try {
                connections.broadcast(gameID, null, checkNotification);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
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

    public String convertPos(ChessPosition pos)
    {
        String str = "";
        switch (pos.getColumn())
        {
            case 1: {
                str += "a";
                break;
            }
            case 2: {
                str += "b";
                break;
            }
            case 3: {
                str += "c";
                break;
            }
            case 4: {
                str += "d";
                break;
            }
            case 5: {
                str += "e";
                break;
            }
            case 6: {
                str += "f";
                break;
            }
            case 7: {
                str += "g";
                break;
            }
            case 8: {
                str += "h";
                break;
            }
        }
        str += (pos.getRow());
        return str;
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

        GameData gameData = gameService.getGame(gameID);
        if (!Objects.equals(username, gameData.getBlackUsername()) && !Objects.equals(username, gameData.getWhiteUsername()))
        {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: not in game"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        if (gameData.getGame().getTeamTurn() == null)
        {
            try {
                connections.sendError(auth, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: game is over"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        ChessGame game = gameService.getGame(gameID).getGame();
        game.setTeamTurn(null);
        gameService.setGame(gameID, new AuthData(auth), game);
        try {
            connections.broadcast(gameID, null, notification);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }


}
