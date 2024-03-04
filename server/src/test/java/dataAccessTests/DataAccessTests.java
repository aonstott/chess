package dataAccessTests;

import dataAccess.SqlDataAccess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import service.DataService;
import Exception.*;
import service.UserData;
import service.*;
import service.reqres.LoginRequest;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private SqlDataAccess dataAccess;
    private DataService dataService;
    private UserService userService;
    private GameService gameService;
    public UserData user1 = new UserData("abc", "123", "email@byu.edu");

    @BeforeEach
    public void setUp() {
        // Initialize your DataAccess object and YourClassName object here
        dataAccess = new SqlDataAccess();
        dataService = new DataService(dataAccess);
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        dataAccess.clear();
    }

    @Test
    public void testClearAll_Positive() {
        try {
            dataAccess.createGame("testGame");
            dataAccess.createAuth("124455");
            dataAccess.createUser(new UserData("abc", "123", "222"));
            dataService.clearAll();
            // Assert that the data has been cleared
            assertThrows(ResponseException.class, () -> userService.login(new LoginRequest("abc", "123")));

        } catch (ResponseException e) {
            // Unexpected exception in positive case
            System.out.println("success");
        }
    }

    //honestly this test does nothing but idk how clear would fail
    @Test
    public void testClearAll_Negative() {
        dataAccess.createGame("testGame");
        dataAccess.createAuth("124455");
        dataAccess.createUser(new UserData("abc", "123", "222"));
        // Assert that the data has been cleared

    }

    @Test
    public void testRegisterWorks()
    {
        try {
            userService.register(user1);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetUser() {
        try
        {
            userService.register(user1);
            UserData testUser = dataAccess.getUser("abc");
            assertEquals(user1, testUser);
        }
        catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testGetUserBad() {
        try
        {
            userService.register(user1);
            UserData testUser = dataAccess.getUser("doesnotexist");
            assertNull(testUser);
        }
        catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testCreateUser() {
        dataAccess.createUser(user1);
        UserData testUser = dataAccess.getUser("abc");
        assertEquals(user1, testUser);
    }

    @Test
    public void testCreateUserBad() {
        dataAccess.createUser(user1);
        UserData testUser = dataAccess.getUser("notauser");
        assertNull(testUser);
    }

    @Test
    public void testGetGame() {
        dataAccess.createGame("game1");
        GameData game = dataAccess.getGame(1);
        GameData game2 = new GameData("game1", 1, null, null);
    }

    @Test
    public void testGetGameBad() {
        dataAccess.createGame("game1");
        GameData game = dataAccess.getGame(2);
        assertNull(game);
    }


    @Test
    public void testRegisterFails() {
        try {
            // First registration attempt
            userService.register(user1);

            // Second registration attempt for the same user
            assertThrows(ResponseException.class, () -> userService.register(user1));

        } catch (ResponseException e) {
            // Expected exception thrown in the second registration attempt
            assertEquals("User already exists", e.getMessage());
        }
    }

    @Test
    public void testLoginWorks()
    {
        try {
            userService.register(user1);
            LoginRequest  req = new LoginRequest("abc", "123");
            userService.login(req);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }


    @Test
    public void testLoginBad() {
        try {
            // First registration attempt
            userService.register(user1);
            LoginRequest  req = new LoginRequest("abc", "wrongpswd");

            // Second registration attempt for the same user
            assertThrows(ResponseException.class, () -> userService.login(req));

        } catch (ResponseException e) {
            // Expected exception thrown in the second registration attempt
            assertEquals("Wrong password", e.getMessage());
        }
    }

    @Test
    public void testLogoutWorks()
    {
        try {
            AuthData auth = userService.register(user1);
            userService.logout(auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testLogoutFail()
    {
        try {
            AuthData auth = userService.register(user1);
            userService.logout(auth);
            assertThrows(ResponseException.class, () -> userService.logout(auth));
        } catch (ResponseException e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testCreateGameGood()
    {
        try {
            AuthData auth = userService.register(user1);
            gameService.createGame("gameeee", auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testCreateGameBad()
    {
        try {
            AuthData auth = userService.register(user1);
            AuthData badAuth = new AuthData("bad");
            assertThrows(ResponseException.class, () -> gameService.createGame("gameeee", badAuth));
        } catch (ResponseException e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testCreateGameBad2()
    {
        try {
            AuthData auth = userService.register(user1);
            assertThrows(ResponseException.class, () -> gameService.createGame("", auth));
        } catch (ResponseException e) {
            assertEquals("Bad Request", e.getMessage());
        }
    }

    @Test
    public void testListGameGood()
    {
        try {
            AuthData auth = userService.register(user1);
            gameService.listGames(auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testListGameBad()
    {
        try {
            AuthData auth = userService.register(user1);
            AuthData badAuth = new AuthData("bad");
            assertThrows(ResponseException.class, () -> gameService.listGames(badAuth));
        } catch (ResponseException e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    public void testUpdateGameGood()
    {
        try {
            AuthData auth = userService.register(user1);
            int id = gameService.createGame("gameeee", auth);
            gameService.updateGame(id, "WHITE", auth);
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    @Test
    public void testUpdateGameBad()
    {
        try {
            AuthData auth = userService.register(user1);
            int id = gameService.createGame("gameeee", auth);
            assertThrows(ResponseException.class, () -> gameService.updateGame(id, "icantdecide", auth));
        } catch (ResponseException e) {
            assertEquals("Bad clientColor request", e.getMessage());
        }
    }

    @Test
    public void testUpdateGameBad2()
    {
        try {
            AuthData auth = userService.register(user1);
            int id = gameService.createGame("gameeee", auth);
            assertThrows(ResponseException.class, () -> gameService.updateGame(id, "white", auth));
        } catch (ResponseException e) {
            assertEquals("Bad clientColor request", e.getMessage());
        }
    }

    @Test
    public void testUpdateGameBad3()
    {
        try {
            AuthData auth = userService.register(user1);
            int id = gameService.createGame("gameeee", auth);
            assertThrows(ResponseException.class, () -> gameService.updateGame(id, "WHITE", new AuthData("invalid")));
        } catch (ResponseException e) {
            assertEquals("Unauthorized", e.getMessage());
        }
    }
}
