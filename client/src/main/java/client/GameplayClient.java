package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketFacade;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import Exception.ResponseException;
import service.AuthData;
import ui.EscapeSequences;

public class GameplayClient {
    private final String serverURL;

    private ChessGame chessGame;

    private int gameID;

    private WebSocketFacade ws;

    private int state = 2;
    private String authData;
    private final ServerFacade serverFacade;

    private final ServerMessageHandler serverMessageHandler;

    public GameplayClient(String serverURL, String authData, ServerMessageHandler serverMessageHandler)
    {
        this.serverURL = serverURL;
        this.serverFacade = new ServerFacade(serverURL);
        this.authData = authData;
        this.serverMessageHandler = serverMessageHandler;
    }
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "help" -> help();
                case "draw" -> drawBoard(chessGame);
                case "move" -> makeMove(params);
                default -> "";
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String help() {
        return """
                - Help - display available commands
                - Quit - exit chess application
                - Draw - draw the chess board for the current game
                - Move - make a move <start-position> <end-position>
                """;
    }
    public String drawBoard(ChessGame game) {
        System.out.println(EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.SET_TEXT_COLOR_BLACK);
        String[][] board = new String[8][8];
        ChessBoard board2 = game.getBoard();
        this.chessGame = game;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                ChessPiece piece = board2.getPiece(new ChessPosition(i + 1, j + 1));
                if (piece != null) {
                    switch (piece.getPieceType())
                    {
                        case ChessPiece.PieceType.PAWN:
                        {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                board[i][j] = EscapeSequences.WHITE_PAWN;
                            else
                                board[i][j] = EscapeSequences.BLACK_PAWN;
                            break;
                        }
                        case ChessPiece.PieceType.KING:
                        {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                board[i][j] = EscapeSequences.WHITE_KING;
                            else
                                board[i][j] = EscapeSequences.BLACK_KING;
                            break;
                        }
                        case ChessPiece.PieceType.BISHOP:
                        {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                board[i][j] = EscapeSequences.WHITE_BISHOP;
                            else
                                board[i][j] = EscapeSequences.BLACK_BISHOP;
                            break;
                        }
                        case ChessPiece.PieceType.KNIGHT:
                        {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                board[i][j] = EscapeSequences.WHITE_KNIGHT;
                            else
                                board[i][j] = EscapeSequences.BLACK_KNIGHT;
                            break;
                        }
                        case ChessPiece.PieceType.ROOK:
                        {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                board[i][j] = EscapeSequences.WHITE_ROOK;
                            else
                                board[i][j] = EscapeSequences.BLACK_ROOK;
                            break;
                        }
                        case ChessPiece.PieceType.QUEEN:
                        {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE)
                                board[i][j] = EscapeSequences.WHITE_QUEEN;
                            else
                                board[i][j] = EscapeSequences.BLACK_QUEEN;
                            break;
                        }
                        default:
                        {
                            board[i][j] = EscapeSequences.EMPTY;
                            break;
                        }
                    }
                }
                else
                {
                    board[i][j] = EscapeSequences.EMPTY;
                }
            }
        }
        StringBuilder result = new StringBuilder();
        result.append("  h\u2003g\u2003f\u2003e\u2003d\u2003c\u2003b\u2003a\n");
        result.append(" +--------------------+\n");
        for (int i = 7; i >= 0; i--) {
            result.append(i + 1).append("|");
            for (int j = 7; j >= 0; j--) {
                result.append(board[i][j]).append("|");
            }
            result.append("\n");
        }
        result.append(" +--------------------+\n");

        result.append("  a\u2003b\u2003c\u2003d\u2003e\u2003f\u2003g\u2003h\n");
        result.append(" +--------------------+\n");
        for (int i = 0; i < 8; i++) { // Start with row 7 and go down to row 0
            result.append(i + 1).append("|");
            for (int j = 0; j < 8; j++) {
                result.append(board[i][j]).append("|");
            }
            result.append("\n");
        }
        result.append(" +--------------------+\n");

        return result.toString();
    }
    public String makeMove(String... params) throws ResponseException
    {
        if (params.length == 2)
        {
            this.ws = new WebSocketFacade(serverURL, serverMessageHandler);
            String startPos = params[0].toLowerCase();
            String endPos = params[1].toLowerCase();
            if (!isValidPosition(startPos) || !isValidPosition(endPos))
            {
                return "Enter valid position letter a-h number 1-8:";
            }
            ChessPosition start = convertPosition(startPos);
            ChessPosition end = convertPosition(endPos);
            ChessMove move = new ChessMove(start, end, null);
            ws.makeMove(new AuthData(authData), gameID, move);
            return "Move succesful!";
        }
        else
        {
            return "Format: move <start-position> <end-position>";
        }
    }

    private ChessPosition convertPosition(String positionString)
    {
        int row = 0;
        int col = 0;
        switch (positionString.charAt(0))
        {
            case 'a':
            {
                col = 1;
                break;
            }
            case 'b':
            {
                col = 2;
                break;
            }
            case 'c':
            {
                col = 3;
                break;
            }
            case 'd':
            {
                col = 4;
                break;
            }
            case 'e':
            {
                col = 5;
                break;
            }
            case 'f':
            {
                col = 6;
                break;
            }
            case 'g':
            {
                col = 7;
                break;
            }
            case 'h':
            {
                col = 8;
                break;
            }
        }
        row = Character.getNumericValue(positionString.charAt(1));
        return new ChessPosition(row, col);
    }

    private boolean isValidPosition(String positionString)
    {
        if (positionString.length() != 2)
            return false;
        Set<Character> charSet = new HashSet<>();

        // Add characters 'a' to 'h'
        for (char c = 'a'; c <= 'h'; c++) {
            charSet.add(c);
        }

        // Add characters '1' to '8'
        for (char c = '1'; c <= '8'; c++) {
            charSet.add(c);
        }
        return charSet.contains(positionString.charAt(0)) && charSet.contains(positionString.charAt(1));
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getState() {
        return state;
    }
}
