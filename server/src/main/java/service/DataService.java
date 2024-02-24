package service;

import dataAccess.DataAccess;
import Exception.ResponseException;
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
}
