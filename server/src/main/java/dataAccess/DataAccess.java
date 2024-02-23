package dataAccess;

import chess.ChessGame;
import service.AuthData;
import service.GameData;
import service.UserData;

import java.util.Collection;

public interface DataAccess {
    public UserData getUser(String username);

    public void createUser(UserData user);

    public AuthData createAuth(String username);

    public void deleteAuth(AuthData info);

    public Collection<GameData> listGames();

    public GameData getGame(int gameID);

    public void createGame(String gameName);

    public void updateGame(int gameID, ChessGame.TeamColor clientColor);

    public void clear();
    public boolean authExists(AuthData authRequest);
}
