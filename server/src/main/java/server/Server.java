package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import Exception.*;
import dataAccess.SqlDataAccess;
import reqres.*;
import service.*;
import spark.*;

import java.util.Collection;

public class Server {

    private final DataService dataService;
    private final UserService userService;
    private final GameService gameService;

    public Server(DataAccess dataAccess)
    {
        this.dataService = new DataService(dataAccess);
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
    }

    public Server()
    {
        SqlDataAccess sql = new SqlDataAccess();
        this.dataService = new DataService(sql);
        this.userService = new UserService(sql);
        this.gameService = new GameService(sql);
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::updateGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
    }

    private Object clear(Request req, Response res)
    {
        try {
            dataService.clearAll();
            res.status(200);
            return "{}";
        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object register(Request req, Response res)
    {
        try {
            var user = new Gson().fromJson(req.body(), UserData.class);
            AuthData auth = userService.register(user);
            LoginResult result = new LoginResult(user.username(), auth.getAuthToken());
            res.status(200);
            return new Gson().toJson(result);
        } catch (RegisterException e) {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: already taken\" }";
        }
        catch (BadRequest e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: bad request\" }";
        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object login(Request req, Response res)
    {
        var info = new Gson().fromJson(req.body(), LoginRequest.class);
        try {
            AuthData auth = userService.login(info);
            LoginResult result = new LoginResult(info.username(), auth.getAuthToken());
            res.status(200);
            return new Gson().toJson(result);
        }
        catch (LoginFailed e)
        {
            System.out.println(e.getMessage());
            res.status(e.statusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (ResponseException e) {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object logout(Request req, Response res)
    {
        //var info = new Gson().fromJson(req.headers("authorization"), AuthData.class);
        AuthData info = new AuthData(req.headers("authorization"));
        System.out.println(info);
        try
        {
            userService.logout(info);
            res.status(200);
            return "{}";
        }
        catch (UnauthorizedException e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object createGame(Request req, Response res)
    {
        AuthData authorization = new AuthData(req.headers("authorization"));
        var info = new Gson().fromJson(req.body(), CreateGameRequest.class);
        try
        {
            int gameID = gameService.createGame(info.gameName(), authorization);
            CreateGameResponse response = new CreateGameResponse(gameID);
            res.status(200);
            return new Gson().toJson(response);
        }
        catch (UnauthorizedException e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (BadRequest e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: bad request\" }";

        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object listGames(Request req, Response res)
    {
        AuthData authorization = new AuthData(req.headers("authorization"));
        try
        {
            res.status(200);
            Collection<GameData> gamesList = gameService.listGames(authorization);
            String ret = new Gson().toJson(gamesList);
            ret = "{ \"games\": " + ret + "}";
            return ret;
        }
        catch (UnauthorizedException e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object updateGame(Request req, Response res)
    {
        AuthData authorization = new AuthData(req.headers("authorization"));
        var requestInfo = new Gson().fromJson(req.body(), UpdateGameRequest.class);
        try
        {
            gameService.updateGame(requestInfo.gameID(), requestInfo.playerColor(), authorization);
            res.status(200);
            return "{}";
        }
        catch (UnauthorizedException e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (BadRequest e)
        {
            System.out.println(e.getMessage());
            res.status(e.statusCode());
            return "{ \"message\": \"Error: bad request\" }";

        }
        catch (AlreadyTakenException e)
        {
            res.status(e.statusCode());
            return "{ \"message\": \"Error: already taken\" }";

        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    public static void main(String[] args) {
        new Server(new SqlDataAccess()).run(8080);
    }
}
