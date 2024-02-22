package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import org.eclipse.jetty.server.Authentication;
import service.DataService;
import service.UserService;
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
        res.status(204);
        return "";
    }

    public static void main(String[] args) {
        new Server(new MemoryDataAccess()).run(8080);
    }
}
