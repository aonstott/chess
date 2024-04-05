package service;

import chess.ChessGame;

public class GameData {
    private String gameName;
    private int gameID;
    private String whiteUsername;
    private String blackUsername;

    private ChessGame game;

    public GameData(String gameName, int gameID, String whiteUsername, String blackUsername) {
        this.gameName = gameName;
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.game = new ChessGame();
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

    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame()
    {
        return game;
    }


}
