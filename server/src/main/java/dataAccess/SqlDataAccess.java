package dataAccess;
import java.sql.*;
import java.util.Collection;

import Exception.*;
import chess.ChessGame;
import service.AuthData;
import service.GameData;
import service.UserData;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, user.username(), user.password(), user.email());
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    public AuthData createAuth(String username)
    {
        var statement = "INSERT INTO auth (auth, username) VALUES (?, ?)";
        try {
            AuthData auth = new AuthData();
            executeUpdate(statement, auth.toString(), username);
            return auth;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
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
        var statements = new String[] { "TRUNCATE auth;", "TRUNCATE users;", "TRUNCATE games;" };
        try {
            for (String statement : statements)
            {
                executeUpdate(statement);
            }
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

    }
    public boolean authExists(AuthData authRequest)
    {
        return false;
    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame g) ps.setString(i + 1, g.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
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
              `auth` varchar(512) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`auth`),
              INDEX(auth)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

}
