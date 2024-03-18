package server;

import com.google.gson.Gson;
import Exception.ResponseException;

import java.io.*;
import java.net.*;
import java.util.Collection;

import reqres.*;
import service.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public LoginResult register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, LoginResult.class);
    }

    public LoginResult login(LoginRequest info) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, info, LoginResult.class);
    }

    public void logout(AuthData auth) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, "authorization", auth.getAuthToken(), null, null);
    }

    public CreateGameResponse createGame(CreateGameRequest gameName, AuthData auth) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, "authorization", auth.getAuthToken(), gameName, CreateGameResponse.class);
    }

    public ListGamesResponse listGames(AuthData auth) throws ResponseException
    {
        var path = "/game";
        return this.makeRequest("GET", path, "authorization", auth.getAuthToken(), null, ListGamesResponse.class);
    }

    public void updateGame(AuthData auth, UpdateGameRequest req) throws ResponseException
    {
        var path = "/game";
        this.makeRequest("PUT", path, "authorization", auth.getAuthToken(), req, null);
    }
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        return makeRequest(method, path, null, null, request, responseClass);
    }
    private <T> T makeRequest(String method, String path, String headerKey, String headerValue, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            // Set the header if provided
            if (headerKey != null && headerValue != null) {
                http.setRequestProperty(headerKey, headerValue);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
