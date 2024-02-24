package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import Exception.ResponseException;
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
        var user = new Gson().fromJson(req.body(), UserData.class);
        AuthData auth = userService.register(user);
        LoginResult result = new LoginResult(user.username(), auth.getAuthToken());
        res.status(200);
        return new Gson().toJson(result);
    }

    private Object login(Request req, Response res)
    {
        var info = new Gson().fromJson(req.body(), LoginRequest.class);
        AuthData auth = userService.login(info);
        LoginResult result = new LoginResult(info.username(), auth.getAuthToken());
        res.status(200);
        return new Gson().toJson(result);

    }

    private Object logout(Request req, Response res)
    {
        //var info = new Gson().fromJson(req.headers("authorization"), AuthData.class);
        AuthData info = new AuthData(req.headers("authorization"));
        System.out.println(info);
        if (userService.logout(info))
        {
            System.out.println("Success");
            res.status(200);
            return "{}";
        }
        else
        {
            System.out.println("Failure");
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    private Object createGame(Request req, Response res) throws ResponseException
    {
        AuthData authorization = new AuthData(req.headers("authorization"));
        var info = new Gson().fromJson(req.body(), CreateGameRequest.class);
        if (gameService.checkAuth(authorization))
        {
            int gameID = gameService.createGame(info.gameName());
            CreateGameResponse response = new CreateGameResponse(gameID);
            res.status(200);
            return new Gson().toJson(response);
        }
        else
        {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    private Object listGames(Request req, Response res)
    {
        AuthData authorization = new AuthData(req.headers("authorization"));
        if (gameService.checkAuth(authorization))
        {
            res.status(200);
            Collection<GameData> gamesList = gameService.listGames();
            return new Gson().toJson(gamesList);
        }
        else
        {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }

    public static void main(String[] args) {
        new Server(new MemoryDataAccess()).run(8080);
    }
}
