package dataAccess;

import org.eclipse.jetty.server.Authentication;
import service.AuthData;
import service.GameData;
import service.UserData;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private static final HashSet<UserData> users = new HashSet<>();
    private static final HashMap<AuthData, String> auth = new HashMap<>();
    private static final Collection<GameData> games = new ArrayList<>();
    private int nextID = 1;

    //getUser() finds UserData for a given username
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        //return null if not found
        return null;
    }

    public void createUser(UserData user) {
        //add user to data structure
        users.add(user);
    }

    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData();
        auth.put(newAuth, username);
        return newAuth;
    }


    //removes authToken for a user
    public void deleteAuth(AuthData info) {
        auth.remove(info);
    }

    public boolean authExists(AuthData authRequest) {
        return auth.containsKey(authRequest);
    }

    public Collection<GameData> listGames() {
        return games;
    }

    public GameData getGame(int gameID) {
        for (GameData game : games) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public int createGame(String gameName) {
        games.add(new GameData(gameName, nextID, null, null));
        nextID++;
        return nextID - 1;
    }

    public void updateGame(int gameID, String clientColor, AuthData authData) {
        String username = auth.get(authData);
        GameData game = getGame(gameID);

        if (Objects.equals(clientColor, "WHITE")) {
            game.setWhiteUsername(username);
        }
        if (Objects.equals(clientColor, "BLACK")) {
            game.setBlackUsername(username);
        }
    }

    public void clear() {
        users.clear();
        games.clear();
        auth.clear();
    }


}
