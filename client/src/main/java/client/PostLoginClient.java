package client;

import chess.ChessGame;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketFacade;
import reqres.*;
import server.ServerFacade;
import Exception.ResponseException;
import service.AuthData;
import service.GameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class PostLoginClient {
    private final String serverURL;

    private WebSocketFacade ws;

    private int state = 1;
    private String authData;
    private final ServerFacade serverFacade;

    private int gameID = 0;

    private final ServerMessageHandler serverMessageHandler;
    public PostLoginClient(String serverURL, String authData, ServerMessageHandler serverMessageHandler)
    {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
        this.serverMessageHandler = serverMessageHandler;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logOut(params);
                case "list" -> listGames(params);
                case "create" -> createGame(params);
                case "join" -> updateGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - Help
                - Quit
                - Logout
                - Create
                - List
                - Join
                """;
    }

    public String logOut(String... params) throws ResponseException {
        if (params.length == 0) {
            AuthData info = new AuthData(authData);
            serverFacade.logout(info);
            state = 0;
            return "Logged out succesfully";
        }
        throw new ResponseException(400, "Expected: logout");
    }

    public String listGames(String... params) throws ResponseException {
        if (params.length == 0)
        {
            StringBuilder result = new StringBuilder("GAMES LIST:\n");
            AuthData info = new AuthData(authData);
            ListGamesResponse res = serverFacade.listGames(info);
            Collection<GameData> gamesList = res.games();
            for (GameData game : gamesList)
            {
                result.append("Game ID: ").append(game.getGameID()).append("\n");
                result.append("Game Name: ").append(game.getGameName()).append("\n");
                result.append("White: ").append(game.getWhiteUsername()).append("\n");
                result.append("Black: ").append(game.getBlackUsername()).append("\n");
                result.append("\n");
            }
            return result.toString();
        }
        throw new ResponseException(400, "Expected: list");
    }

    public String createGame(String... params) throws ResponseException
    {
        if (params.length == 1)
        {
            AuthData info = new AuthData(authData);
            String gameName = params[0];
            CreateGameRequest req = new CreateGameRequest(gameName);
            CreateGameResponse res = serverFacade.createGame(req, info);
            int gameID = res.gameID();
            return String.format("Created Game: %s (id: %s)", gameName, gameID);
        }
        throw new ResponseException(400, "Expected: create <game_name>");
    }

    public String updateGame(String... params) throws ResponseException {
        if (params.length == 2 || params.length == 1)
        {
            this.ws = new WebSocketFacade(serverURL, serverMessageHandler);
            AuthData info = new AuthData(authData);
            String playerColor = null;
            int gameID = 0;
            if (params.length == 2) {
                playerColor = params[0];
                gameID = Integer.parseInt(params[1]);

                ChessGame.TeamColor teamColor = null;
                if (Objects.equals(playerColor, "black"))
                    teamColor = ChessGame.TeamColor.BLACK;
                else if (Objects.equals(playerColor, "white"))
                {
                    teamColor = ChessGame.TeamColor.WHITE;
                }


                ws.joinPlayer(info, gameID, teamColor);
            }
            else {
                gameID = Integer.parseInt(params[0]);
                ws.joinObserver(info, gameID);
            }
            UpdateGameRequest req = new UpdateGameRequest(playerColor, gameID);
            serverFacade.updateGame(info, req);
            this.state = 2;
            this.gameID = gameID;
            if (playerColor == null)
            {
                return "Joined as observer";
            }
            return String.format("Joined as team %s", playerColor);
        }
        throw new ResponseException(400, "Expected: join <black or white> <game_id>");
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public int getGameID() {
        return gameID;
    }
}
