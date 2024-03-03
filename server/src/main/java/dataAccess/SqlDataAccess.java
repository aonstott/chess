package dataAccess;
import java.sql.*;
import java.util.Collection;

import Exception.*;
import service.AuthData;
import service.GameData;
import service.UserData;

public class SqlDataAccess implements DataAccess {

    public SqlDataAccess()
    {
        try
        {
            configureDatabase();
        }
        catch (ResponseException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public UserData getUser(String username)
    {
        return null;
    }

    public void createUser(UserData user)
    {

    }

    public AuthData createAuth(String username)
    {
        return null;
    }

    public void deleteAuth(AuthData info)
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

    public int createGame(String gameName)
    {
        return 0;
    }

    public void updateGame(int gameID, String clientColor, AuthData authData)
    {

    }

    public void clear()
    {

    }
    public boolean authExists(AuthData authRequest)
    {
        return false;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
            """
           CREATE TABLE IF NOT EXISTS  auth (
              `auth` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`auth`),
              INDEX(auth)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

}
