package service;

import dataAccess.DataAccess;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

import Exception.*;
import dataAccess.SqlDataAccess;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public int createGame(String gameName, AuthData auth) throws ResponseException {
        if (!checkAuth(auth)) {
            throw new UnauthorizedException(401, "Unauthorized");
        }
        if (Objects.equals(gameName, "")) {
            throw new BadRequest(400, "Bad Request");
        }
        return dataAccess.createGame(gameName);
    }

    public boolean checkAuth(AuthData auth) {
        return dataAccess.authExists(auth);
    }

    public Collection<GameData> listGames(AuthData auth) throws ResponseException {
        if (!checkAuth(auth)) {
            throw new UnauthorizedException(401, "Unauthorized");
        }
        return dataAccess.listGames();
    }

    public void updateGame(int gameID, String clientColor, AuthData authData) throws ResponseException {
        GameData currentGame = dataAccess.getGame(gameID);
        if (clientColor != null) {
            clientColor = clientColor.toUpperCase();
        }
        //check that gameID exists
        if (dataAccess.getGame(gameID) == null) {
            throw new BadRequest(400, "No game with that ID");
        }
        //check that user entered a gameID
        if (gameID == 0) {
            throw new BadRequest(400, "No gameID entered");
        }
        if (!checkAuth(authData)) {
            throw new UnauthorizedException(401, "Unauthorized");
        }
        //check that they entered white or black
        if (!(Objects.equals(clientColor, "WHITE") || Objects.equals(clientColor, "BLACK")
                || Objects.equals(clientColor, "")
                || Objects.equals(clientColor, null))) {
            throw new BadRequest(400, "Bad clientColor request");
        }
        if (Objects.equals(clientColor, "WHITE") && (currentGame.getWhiteUsername() != null))
        {
            throw new AlreadyTakenException(403, "Already taken");
        }
        if (Objects.equals(clientColor, "BLACK") && (currentGame.getBlackUsername() != null))
        {
            throw new AlreadyTakenException(403, "Already taken");
        }
        dataAccess.updateGame(gameID, clientColor, authData);
    }

    public String getUsername(AuthData auth) throws ResponseException {
        try {
            return SqlDataAccess.getUsername(auth);
        } catch (SQLException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public GameData getGame(int gameID)
    {
        return dataAccess.getGame(gameID);
    }
}


