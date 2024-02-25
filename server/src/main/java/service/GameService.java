package service;

import dataAccess.DataAccess;

import java.util.Collection;
import java.util.Objects;

import Exception.*;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public int createGame(String gameName, AuthData auth) throws ResponseException
    {
        if (!checkAuth(auth))
        {
            throw new UnauthorizedException(401, "Unauthorized");
        }
        if (Objects.equals(gameName, ""))
        {
            throw new BadRequest(400, "Bad Request");
        }
        return dataAccess.createGame(gameName);
    }

    public boolean checkAuth(AuthData auth)
    {
        return dataAccess.authExists(auth);
    }
    public Collection<GameData> listGames(AuthData auth) throws ResponseException
    {
        if (!checkAuth(auth))
        {
            throw new UnauthorizedException(401, "Unauthorized");
        }
        return dataAccess.listGames();
    }
}
