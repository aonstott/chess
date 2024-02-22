package dataAccess;

import chess.ChessGame;
import service.AuthData;
import service.GameData;
import service.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryDataAccess implements DataAccess{
    private ArrayList<UserData> users = new ArrayList<>();
    private ArrayList<AuthData> auth = new ArrayList<>();
    private ArrayList<GameData> games = new ArrayList<>();

    public UserData getUser(String username)
    {
        return null;
    }

    public void createUser(String username, String password)
    {

    }

    public AuthData createAuth(String username)
    {
        return null;
    }

    public void deleteAuth(String username)
    {

    }

    public Collection<GameData> listGames()
    {
        return null;
    }

    public GameData getGame(int gameID)
    {
        return null;
    }

    public void createGame(String gameName)
    {

    }

    public void updateGame(int gameID, ChessGame.TeamColor clientColor)
    {

    }

    public void clear()
    {
        users.clear();
        games.clear();
        auth.clear();
    }



}
