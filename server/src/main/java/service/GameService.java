package service;

import dataAccess.DataAccess;

import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public int createGame(String gameName)
    {
        return dataAccess.createGame(gameName);
    }

    public boolean checkAuth(AuthData auth)
    {
        return dataAccess.authExists(auth);
    }
    public Collection<GameData> listGames()
    {
        return dataAccess.listGames();
    }
}
