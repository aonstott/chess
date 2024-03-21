package clientTests;

import org.junit.jupiter.api.*;
import reqres.CreateGameRequest;
import reqres.LoginRequest;
import reqres.LoginResult;
import reqres.UpdateGameRequest;
import server.Server;
import server.ServerFacade;
import Exception.ResponseException;
import service.AuthData;
import service.UserData;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        String serverUrl = "http://localhost:" + port;
        System.out.println(serverUrl);
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.clear();
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterEach
    void clearDB() throws ResponseException {serverFacade.clear();}

    @AfterAll
    static void stopServer() {
        server.stop();
    }




    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testRegister()
    {
        try {
            serverFacade.register(new UserData("johnny", "password", "aaaa"));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void testRegisterBad()
    {
        try {
            serverFacade.register(new UserData("johnny", "password", "aaaa"));
            assertThrows(ResponseException.class, () -> serverFacade.register(new UserData("johnny", "password", "aaaa")));
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testLogin()
    {
        try {
            serverFacade.register(new UserData("123", "panda", "aaaa"));

            serverFacade.login(new LoginRequest("123", "panda"));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void testLoginBad()
    {
        try {
            serverFacade.login(new LoginRequest("123", "panda"));
            assertThrows(ResponseException.class, () -> serverFacade.login(new LoginRequest("123", "panda")));
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

    }


    @Test
    public void testLogout()
    {
        try {
            LoginResult res = serverFacade.register(new UserData("1234", "panda", "aaaa"));
            AuthData auth = new AuthData(res.authToken());
            serverFacade.logout(auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void testLogoutBad()
    {
        AuthData auth = new AuthData("bad");
        assertThrows(ResponseException.class, () -> serverFacade.logout(auth));

    }

    @Test
    public void testCreate()
    {
        try {
            LoginResult res = serverFacade.register(new UserData("123", "panda", "aaaa"));
            AuthData auth = new AuthData(res.authToken());
            serverFacade.createGame(new CreateGameRequest("gamey"), auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void testCreateBad()
    {
        assertThrows(ResponseException.class, () ->  serverFacade.createGame(new CreateGameRequest("gamey"), new AuthData("abcde")));
    }

    @Test
    public void testList()
    {
        try {
            LoginResult res = serverFacade.register(new UserData("12325", "panda", "aaaa"));
            AuthData auth = new AuthData(res.authToken());
            serverFacade.listGames(auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void testListBad()
    {
        assertThrows(ResponseException.class, () ->  serverFacade.listGames(new AuthData("abcde")));
    }

    @Test
    public void testUpdate()
    {
        try {
            LoginResult res = serverFacade.register(new UserData("123456", "panda", "aaaa"));
            AuthData auth = new AuthData(res.authToken());
            serverFacade.createGame(new CreateGameRequest("gamey"), auth);
            serverFacade.updateGame(auth, new UpdateGameRequest("WHITE", 1));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    @Test
    public void testUpdateBad() throws ResponseException {
        LoginResult res = serverFacade.register(new UserData("123", "panda", "aaaa"));
        AuthData auth = new AuthData(res.authToken());
        assertThrows(ResponseException.class, () ->  serverFacade.updateGame(auth, new UpdateGameRequest("idk", 5)));
    }

    @Test
    public void testClear() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void testClear2() throws ResponseException {
        serverFacade.clear();
    }

}
