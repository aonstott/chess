package service;

import dataAccess.DataAccess;
import Exception.*;
import dataAccess.MemoryDataAccess;
import spark.Response;

import javax.xml.crypto.Data;

public class DataService {

    private final DataAccess dataAccess;

    public DataService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }
    public void clearAll() throws ResponseException
    {
        dataAccess.clear();
    }
    public boolean checkAuth(AuthData auth) {
        return dataAccess.authExists(auth);
    }
}
