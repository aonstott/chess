package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import Exception.*;
import org.eclipse.jetty.server.Authentication;
import service.*;
import spark.*;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashSet;

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
        this.dataService = new DataService(new MemoryDataAccess());
        this.userService = new UserService(new MemoryDataAccess());
        this.gameService = new GameService(new MemoryDataAccess());
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
        Spark.exception(ResponseException.class, this::exceptionHandler);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object clear(Request req, Response res) throws ResponseException
    {
        dataService.clearAll();
        res.status(200);
        return "{}";
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
            res.status(e.StatusCode());
            return "{ \"message\": \"Error: already taken\" }";
        }
        catch (BadRequest e)
        {
            res.status(e.StatusCode());
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
            System.out.println("yay");
            return new Gson().toJson(result);
        }
        catch (loginFailed e)
        {
            System.out.println(e.getMessage());
            res.status(e.StatusCode());
            System.out.println("noo");
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
            res.status(e.StatusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    private Object createGame(Request req, Response res) throws ResponseException
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
            res.status(e.StatusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (BadRequest e)
        {
            res.status(e.StatusCode());
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
            return new Gson().toJson(gamesList);
        }
        catch (UnauthorizedException e)
        {
            res.status(e.StatusCode());
            return "{ \"message\": \"Error: unauthorized\" }";
        }
        catch (ResponseException e)
        {
            res.status(500);
            return "{ \"message\": \"Error: unknown\" }";
        }
    }

    public static void main(String[] args) {
        new Server(new MemoryDataAccess()).run(8080);
    }
}
