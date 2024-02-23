package dataAccess;

import chess.ChessGame;
import org.eclipse.jetty.server.Authentication;
import service.AuthData;
import service.GameData;
import service.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess{
    private final HashSet<UserData> users = new HashSet<>();
    private final HashMap<AuthData, String> auth = new HashMap<>();
    private final HashSet<GameData> games = new HashSet<>();

    //getUser() finds UserData for a given username
    public UserData getUser(String username)
    {
        for (UserData user : users)
        {
            if (Objects.equals(user.username(), username))
            {
                return user;
            }
        }
        //return null if not found
        return null;
    }

    public void createUser(UserData user)
    {
        //add user to data structure
        users.add(user);
    }

    public AuthData createAuth(String username)
    {
        AuthData newAuth = new AuthData();
        auth.put(newAuth, username);
        return newAuth;
    }

    public String getAuth(AuthData info)
    {
        return auth.get(info);
    }

    //removes authToken for a user
    public void deleteAuth(AuthData info)
    {
        auth.remove(info);
    }

    public boolean authExists(AuthData authRequest)
    {
        return auth.containsKey(authRequest);
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
