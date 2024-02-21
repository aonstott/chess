package service;

import dataAccess.DataAccess;

public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao)
    {
        this.dao = dao;
    }
    public AuthData register(UserData user) {
        return new AuthData();
    }
    public AuthData login(UserData user) {
        return new AuthData();
    }
    public void logout(UserData user) {}
}
