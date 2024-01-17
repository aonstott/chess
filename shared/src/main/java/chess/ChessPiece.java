package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        if (currentPiece != null)
        {
            switch (currentPiece.getPieceType())
            {
                case PAWN:
                    validMoves.addAll(getPawnMoves(board, myPosition, currentPiece.getTeamColor()));
                    break;
                case BISHOP:
                    validMoves.addAll(getBishopMoves(board, myPosition, currentPiece.getTeamColor()));

            }
        }
        return validMoves;

    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {


        Collection<ChessMove> pawnMoves = new ArrayList<>();
        int forwardRow = (teamColor == ChessGame.TeamColor.WHITE) ? position.getRow() + 1 : position.getRow() - 1;
        int currentCol = position.getColumn();

        if (isValidPosition(forwardRow, currentCol) && board.getPiece(new ChessPosition(forwardRow, currentCol)) == null) {
            pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol), null));
        }

        return pawnMoves;
    }

    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {


        Collection<ChessMove> bishopMoves = new ArrayList<>();
        //starting position
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        //save it again for backwards moves (this seems inefficient but oh well)
        int currentRow2 = position.getRow();
        int currentCol2 = position.getColumn();

        //get forward diagonal moves
        while (isValidPosition(currentRow2, currentCol2))
        {
            currentRow2++;
            currentCol2++;

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null)
            {
                //same color?
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != this.getTeamColor())
                    bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
                //don't add anymore because we are blocked
                break;
            }

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) == null) {
                bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
            }

        }

        currentCol2 = currentCol;
        currentRow2 = currentRow;

        //backwards diagonal moves
        while(isValidPosition(currentRow2, currentCol2))
        {
            currentRow2--;
            currentCol2++;

            //found a piece at this spot, could capture??
            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null)
            {
                //same color?
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != this.getTeamColor())
                    bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
                //don't add anymore because we are blocked
                break;
            }
            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) == null) {
                bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
            }
        }

        currentCol2 = currentCol;
        currentRow2 = currentRow;



        //backwards diagonal moves
        while(isValidPosition(currentRow2, currentCol2))
        {
            currentRow2--;
            currentCol2--;

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null)
            {
                //cant capture same color
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != this.getTeamColor())
                    bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
                //don't add anymore because we are blocked
                break;
            }

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) == null) {
                bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
            }
        }

        currentCol2 = currentCol;
        currentRow2 = currentRow;

        //backwards diagonal moves
        while(isValidPosition(currentRow2, currentCol2))
        {
            currentRow2++;
            currentCol2--;

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null)
            {
                //same color??
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != this.getTeamColor())
                    bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
                //don't add anymore because we are blocked
                break;
            }

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) == null) {
                bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
            }
        }


        for (ChessMove item : bishopMoves) {
            System.out.println(item.toStr());
        }
        return bishopMoves;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }
}
