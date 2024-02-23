package service;

import dataAccess.DataAccess;

import java.util.Objects;

public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao)
    {
        this.dao = dao;
    }
    public AuthData register(UserData user) {
        dao.createUser(user);
        return dao.createAuth(user.username());

    }

    public AuthData login(LoginRequest info) {
        if (dao.getUser(info.username()) == null)
        {
            System.out.println("No user");
            return null;
        }
        if (Objects.equals(info.password(), dao.getUser(info.username()).password()))
        {
            return dao.createAuth(info.username());
        }
        System.out.println("Wrong password");
        return null;
    }

    public boolean logout(AuthData info)
    {
        if (dao.authExists(info))
        {
            dao.deleteAuth(info);
            return true;
        }
        return false;
    }

}
