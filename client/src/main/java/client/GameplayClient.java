package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import server.ServerFacade;

import java.util.Arrays;
import Exception.ResponseException;
import ui.EscapeSequences;

public class GameplayClient {
    private final String serverURL;

    private ChessGame chessGame;

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
            case "help" -> help();
            case "draw" -> drawBoard(chessGame);
            default -> "";
        };
    }

    public String help() {
        return """
                - Help - display available commands
                - Quit - exit chess application
                - Draw - draw the chess board for the current game
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
