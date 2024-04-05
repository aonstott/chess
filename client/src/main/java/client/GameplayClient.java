package client;

import chess.ChessGame;
import server.ServerFacade;

import java.util.Arrays;
import Exception.ResponseException;
import ui.EscapeSequences;

public class GameplayClient {
    private final String serverURL;

    private int state = 2;
    private String authData;
    private final ServerFacade serverFacade;

    public GameplayClient(String serverURL, String authData)
    {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
    }
    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "quit" -> "quit";
            default -> drawBoard();
        };
    }

    public String help() {
        return """
                - Help - display available commands
                - Quit - exit chess application
                """;
    }
    public String drawBoard() {
        String[][] board = {
                {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK},
                {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
                {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK}
        };
        StringBuilder result = new StringBuilder();
        result.append("  a\u2003b\u2003c\u2003d\u2003e\u2003f\u2003g\u2003h\n");
        result.append(" +--------------------+\n");
        for (int i = 0; i < 8; i++) {
            result.append(8 - i).append("|");
            for (int j = 0; j < 8; j++) {
                result.append(board[i][j]).append("|");
            }
            result.append("\n");
        }
        result.append(" +--------------------+\n");

        result.append("  a\u2003b\u2003c\u2003d\u2003e\u2003f\u2003g\u2003h\n");
        result.append(" +--------------------+\n");
        for (int i = 7; i >= 0; i--) { // Start with row 7 and go down to row 0
            result.append(i + 1).append("|");
            for (int j = 7; j >= 0; j--) {
                result.append(board[i][j]).append("|");
            }
            result.append("\n");
        }
        result.append(" +--------------------+\n");

        return result.toString();
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
