package service;

public class GameData {
    private String gameName;
    private int gameID;
    private String whiteUsername;
    private String blackUsername;

    public GameData(String gameName, int gameID, String whiteUsername, String blackUsername) {
        this.gameName = gameName;
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }


    public int getGameID() {
        return gameID;
    }


}
