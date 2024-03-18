package service;

import dataAccess.DataAccess;

import java.util.Objects;
import Exception.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reqres.LoginRequest;

public class UserService {

    private final DataAccess dao;

    public UserService(DataAccess dao)
    {
        this.dao = dao;
    }
    public AuthData register(UserData user) throws ResponseException {
        if (dao.getUser(user.username()) != null)
        {
            throw new RegisterException(403, "Already taken");
        }
        if (Objects.equals(user.username(), null) || Objects.equals(user.password(), null))
        {
            throw new BadRequest(400, "bad request");
        }
        if (Objects.equals(user.username(), "") || Objects.equals(user.password(), ""))
        {
            throw new BadRequest(400, "Bad Request");
        }
        dao.createUser(user);
        return dao.createAuth(user.username());

    }

    public AuthData login(LoginRequest info) throws ResponseException {
        if (dao.getUser(info.username()) == null)
        {
            throw new LoginFailed(401, "Error: unauthorized");
        }
        //if (Objects.equals(info.password(), dao.getUser(info.username()).password()))
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(dao.getUser(info.username()).password());
        System.out.println(encoder.encode(info.password()));
        if (encoder.matches(info.password(), dao.getUser(info.username()).password()))
        {
            return dao.createAuth(info.username());
        }
        else
        {
            throw new LoginFailed(401, "Wrong password");
        }
    }

    public void logout(AuthData info) throws ResponseException
    {
        if (dao.authExists(info))
        {
            dao.deleteAuth(info);
        }
        else
        {
            throw new UnauthorizedException(401, "Unauthorized");
        }
    }

}
