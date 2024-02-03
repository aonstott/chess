package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor color;
    private ChessBoard board;

    public ChessGame() {
        this.color = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.color;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.color = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        ChessBoard ogBoard = board.clone();

        if (currentPiece == null)
            return null;

        Collection<ChessMove> valid = new ArrayList<>();
        Collection<ChessMove> possibleMoves = new ArrayList<>(currentPiece.pieceMoves(board, startPosition));
        for (ChessMove mov : possibleMoves) {
            ogBoard = board.clone();
            testMove(mov);
            if (!isInCheck(currentPiece.getTeamColor()))
                valid.add(mov);
            board = ogBoard;
        }
        return valid;
    }

    //makes whatever move you want, no error checking.  Only to test hypothetical moves.
    public void testMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() == null)
            this.board.addPiece(move.getEndPosition(), piece);
        else
            this.board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        this.board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        Collection<ChessMove> valid = new ArrayList<>();
        if (!(move.getStartPosition().getRow() > 0 && move.getStartPosition().getRow() <= 8 && move.getStartPosition().getColumn() > 0 && move.getStartPosition().getColumn() <= 8)) {
            throw new InvalidMoveException("Bad start position");
        }
        if (!(move.getEndPosition().getRow() > 0 && move.getEndPosition().getRow() <= 8 && move.getEndPosition().getColumn() > 0 && move.getEndPosition().getColumn() <= 8)) {
            throw new InvalidMoveException("Bad end position");
        }
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }
        valid = validMoves(move.getStartPosition());

        //System.out.println("Before:");
        //System.out.println(getTeamTurn());
        //System.out.println("turn2");

        //check if move is contained in valid moves
        if (!(valid.contains(move))) {
            throw new InvalidMoveException("Move is invalid");
        }

        if (piece.getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException("Not your turn!");
        }

        if (move.getPromotionPiece() == null)
            this.board.addPiece(move.getEndPosition(), piece);
        else
            this.board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        this.board.addPiece(move.getStartPosition(), null);

        //change team turn
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
        //System.out.println("After: ");
        //System.out.println(getTeamTurn());
        //System.out.println("Turn");

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor otherTeamColor;
        if (teamColor == TeamColor.WHITE)
            otherTeamColor = TeamColor.BLACK;
        else
            otherTeamColor = TeamColor.WHITE;
        ChessPosition kingPos = findKing(teamColor);
        //System.out.println("KING POS: ");
        //System.out.println(kingPos);
        if (kingPos == null)
            return false;

        Collection<ChessPosition> otherTeamPositions = getTeamPositions(otherTeamColor);

        for (ChessPosition pos : otherTeamPositions) {
            ChessPiece otherPiece = board.getPiece(pos);
            Collection<ChessMove> moves = new ArrayList<>(otherPiece.pieceMoves(board, pos));
            for (ChessMove mov : moves) {

                if (mov.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }

        }
        return false;

    }

    public Collection<ChessPosition> getTeamPositions(TeamColor color) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null) {
                    if (piece.getTeamColor() == color)
                        positions.add(new ChessPosition(i, j));
                }

            }
        }

        return positions;
    }

    //returns king for given team, null if it doesn't exist
    public ChessPosition findKing(TeamColor color) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null) {
                    if (piece.getTeamColor() == color && piece.getPieceType() == ChessPiece.PieceType.KING)
                        return new ChessPosition(i, j);
                }

            }
        }

        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (getTeamTurn() != teamColor)
            return false;

        if (!(isInCheck(teamColor)))
            return false;
        Collection<ChessPosition> teamPositions = new ArrayList<>(getTeamPositions(teamColor));

        for (ChessPosition pos : teamPositions) {
            if (!validMoves(pos).isEmpty()) {
                return false;
            }

        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor))
            return false;
        else
        {
            Collection<ChessPosition> positions = getTeamPositions(teamColor);
            for (ChessPosition pos : positions)
            {

            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
