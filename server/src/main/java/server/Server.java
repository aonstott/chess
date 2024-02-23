package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import org.eclipse.jetty.server.Authentication;
import service.*;
import spark.*;

import javax.xml.crypto.Data;

public class Server {

    private final DataService dataService;
    private final UserService userService;

    public Server(DataAccess dataAccess)
    {
        this.dataService = new DataService(dataAccess);
        this.userService = new UserService(dataAccess);
    }

    public Server()
    {
        this.dataService = new DataService(new MemoryDataAccess());
        this.userService = new UserService(new MemoryDataAccess());
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res)
    {
        dataService.clearAll();
        res.status(200);
        return "{}";
    }

    private Object register(Request req, Response res)
    {
        var user = new Gson().fromJson(req.body(), UserData.class);
        AuthData auth = userService.register(user);
        LoginResult result = new LoginResult(user.password(), auth.getAuthToken());
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
        var info = new Gson().fromJson(req.headers().toString(), AuthData.class);
        if (userService.logout(info))
        {
            res.status(200);
            return "{}";
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
