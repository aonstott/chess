package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;

import javax.xml.crypto.Data;

public class DataService {

    private final DataAccess dataAccess;

    public DataService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }
    public void clearAll()
    {
        dataAccess.clear();
    }
}
