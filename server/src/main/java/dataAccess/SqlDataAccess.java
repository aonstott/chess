package dataAccess;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import Exception.*;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.server.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }

    public void createUser(UserData user)
    {
        var statement = "INSERT INTO users (username, password, email, json) VALUES (?, ?, ?, ?)";
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(user.password());
            UserData forDB = new UserData(user.username(), hashedPassword, user.email());
            executeUpdate(statement, user.username(), hashedPassword, user.email(), new Gson().toJson(forDB));
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
        var statement = "DELETE FROM auth WHERE auth=?";
        try {
            executeUpdate(statement, info.toString());
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    public Collection<GameData> listGames()
    {
        Collection<GameData> gamesList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gamesList.add(new Gson().fromJson(rs.getString("json"), GameData.class));
                    }
                    return gamesList;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public GameData getGame(int gameID)
    {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }


    public int createGame(String gameName)
    {
        var statement = "INSERT INTO games (name) VALUES (?)";
        try {
            int id = executeUpdate(statement, gameName);
            var statement2 = "UPDATE games SET game = ?, json = ? WHERE id = ?";
            GameData game = new GameData(gameName, id, null, null);
            executeUpdate(statement2, new Gson().toJson(game.getGame()), new Gson().toJson(game), id);
            return id;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void updateGame(int gameID, String clientColor, AuthData authData)
    {
        try {
            GameData game = getGame(gameID);
            String username = getUsername(authData);
            var statement = "SELECT username FROM auth WHERE auth=?";
            var statement2 = "";
            if (Objects.equals(clientColor, "WHITE"))
            {
                game.setWhiteUsername(username);
                statement2 = "UPDATE games SET whiteUsername = ?, json = ? WHERE id = ?";
            }
            else if (Objects.equals(clientColor, "BLACK"))
            {
                game.setBlackUsername(username);
                statement2 = "UPDATE games SET blackUsername = ?, json = ? WHERE id = ?";
            }
            try
            {
                executeUpdate(statement2, username, new Gson().toJson(game), gameID);
            }
            catch (ResponseException e)
            {
                System.out.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getUsername(AuthData authData) throws SQLException {
        String username = "";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE auth=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.getAuthToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next())
                    {
                        username = rs.getString("username");
                    }
                }
            }
        }
        catch (ResponseException e)
        {
            System.out.println(e.getMessage());
        }
        return username;
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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE auth=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authRequest.getAuthToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("authExists");
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void makeMove(int gameID, ChessGame game) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            GameData gameData = getGame(gameID);
            GameData newData = new GameData(gameData.getGameName(), gameID, gameData.getWhiteUsername(), gameData.getBlackUsername(), game);
            var statement = "UPDATE games SET game=?, json=? WHERE id=?";
            executeUpdate(statement, gson.toJson(game), gson.toJson(newData), gameID);
            System.out.println("updated");
        } catch (ResponseException e) {
            System.out.println("Exception at makeMove in SQL Data access: " + e.getMessage());
        }
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
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `game` varchar(2048),
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(id)
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
