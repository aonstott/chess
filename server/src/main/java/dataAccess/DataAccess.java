package dataAccess;

import service.AuthData;
import service.GameData;
import service.UserData;

import java.util.Collection;

public interface DataAccess {
    UserData getUser(String username);

    void createUser(UserData user);

    AuthData createAuth(String username);

    void deleteAuth(AuthData info);

    Collection<GameData> listGames();

    GameData getGame(int gameID);

    int createGame(String gameName);

    void updateGame(int gameID, String clientColor, AuthData authData);

    void clear();
    boolean authExists(AuthData authRequest);
}
