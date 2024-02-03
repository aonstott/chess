package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable{

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
        if (currentPiece != null) {
            switch (currentPiece.getPieceType()) {
                case PAWN:
                    validMoves.addAll(getPawnMoves(board, myPosition, currentPiece.getTeamColor()));
                    break;
                case BISHOP:
                    validMoves.addAll(getBishopMoves(board, myPosition, currentPiece.getTeamColor()));
                    break;
                case ROOK:
                    validMoves.addAll(getRookMoves(board, myPosition, currentPiece.getTeamColor()));
                    break;
                case QUEEN:
                    validMoves.addAll(getQueenMoves(board, myPosition, currentPiece.getTeamColor()));
                    break;
                case KNIGHT:
                    validMoves.addAll(getKnightMoves(board, myPosition, currentPiece.getTeamColor()));
                    break;
                case KING:
                    validMoves.addAll(getKingMoves(board, myPosition, currentPiece.getTeamColor()));



            }
        }
        return validMoves;

    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {


        Collection<ChessMove> pawnMoves = new ArrayList<>();
        int forwardRow = (teamColor == ChessGame.TeamColor.WHITE) ? position.getRow() + 1 : position.getRow() - 1;
        int currentCol = position.getColumn();

        if ((isValidPosition(forwardRow, currentCol + 1)))
        {
            if (board.getPiece(new ChessPosition(forwardRow, currentCol + 1)) != null) {
                //same color?
                if (board.getPiece(new ChessPosition(forwardRow, currentCol + 1)).getTeamColor() != teamColor) {
                    //can promote
                    if (forwardRow == 8 || forwardRow == 1) {
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol + 1), PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol + 1), PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol + 1), PieceType.KNIGHT));
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol + 1), PieceType.ROOK));
                    } else {
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol + 1), null));
                    }

                }
            }
        }

        if (isValidPosition(forwardRow, currentCol - 1)) {
            if (board.getPiece(new ChessPosition(forwardRow, currentCol - 1)) != null) {
                //same color?
                if (board.getPiece(new ChessPosition(forwardRow, currentCol - 1)).getTeamColor() != teamColor) {
                    //can promote
                    if (forwardRow == 8 || forwardRow == 1) {
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol - 1), PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol - 1), PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol - 1), PieceType.KNIGHT));
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol - 1), PieceType.ROOK));
                    } else {
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol - 1), null));
                    }

                }
            }
        }

        if (isValidPosition(forwardRow, currentCol)) {
            if (board.getPiece(new ChessPosition(forwardRow, currentCol)) == null) {
                if (forwardRow == 1 || forwardRow == 8) {
                    pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol), PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol), PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol), PieceType.KNIGHT));
                    pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol), PieceType.ROOK));
                } else {
                    pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow, currentCol), null));
                }
            }
        }

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2) {
                if (isValidPosition(forwardRow + 1, currentCol)) {
                    if (board.getPiece(new ChessPosition(forwardRow + 1, currentCol)) == null && board.getPiece(new ChessPosition(forwardRow, currentCol)) == null) {
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow + 1, currentCol), null));
                    }
                }
            }
        }

        if (pieceColor == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7) {
                if (isValidPosition(forwardRow - 1, currentCol)) {
                    if (board.getPiece(new ChessPosition(forwardRow - 1, currentCol)) == null && board.getPiece(new ChessPosition(forwardRow, currentCol)) == null) {
                        pawnMoves.add(new ChessMove(position, new ChessPosition(forwardRow - 1, currentCol), null));
                    }
                }
            }
        }


        return pawnMoves;
    }

    //this function gets moves for bishop and returns as a collection
    private Collection<ChessMove> getBishopMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {


        Collection<ChessMove> bishopMoves = new ArrayList<>();
        //starting position
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        //save it again for backwards moves (this seems inefficient but oh well)
        int currentRow2 = position.getRow();
        int currentCol2 = position.getColumn();

        //get forward diagonal moves
        while (isValidPosition(currentRow2, currentCol2)) {
            currentRow2++;
            currentCol2++;

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null) {
                //same color?
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != teamColor)
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
        while (isValidPosition(currentRow2, currentCol2)) {
            currentRow2--;
            currentCol2++;

            //found a piece at this spot, could capture??
            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null) {
                //same color?
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != teamColor)
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
        while (isValidPosition(currentRow2, currentCol2)) {
            currentRow2--;
            currentCol2--;

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null) {
                //cant capture same color
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != teamColor)
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
        while (isValidPosition(currentRow2, currentCol2)) {
            currentRow2++;
            currentCol2--;

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null) {
                //same color??
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != teamColor)
                    bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
                //don't add anymore because we are blocked
                break;
            }

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) == null) {
                bishopMoves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
            }
        }

        return bishopMoves;
    }

    private Collection<ChessMove> getRookMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        //starting position
        rookMoves.addAll(getMovesLoop(board, position, teamColor, 1, 0));
        rookMoves.addAll(getMovesLoop(board, position, teamColor, 0, 1));
        rookMoves.addAll(getMovesLoop(board, position, teamColor, 2, 0));
        rookMoves.addAll(getMovesLoop(board, position, teamColor, 0, 2));

        return rookMoves;
    }


    private Collection<ChessMove> getQueenMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> queenMoves = new ArrayList<>();
        queenMoves.addAll(getRookMoves(board, position, teamColor));
        queenMoves.addAll(getBishopMoves(board, position, teamColor));
        return queenMoves;
    }


    //this is a stupid name but oh well
    //0 is nothing, 1 add, 2 subtract
    private Collection<ChessMove> getMovesLoop(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor, int row_op, int col_op) {
        Collection<ChessMove> moves = new ArrayList<>();

        //save it again for backwards moves (this seems inefficient but oh well)
        int currentRow2 = position.getRow();
        int currentCol2 = position.getColumn();

        //get forward diagonal moves
        while (isValidPosition(currentRow2, currentCol2)) {
            if (row_op == 1)
                currentRow2++;
            if (row_op == 2)
                currentRow2--;
            if (col_op == 1)
                currentCol2++;
            if (col_op == 2)
                currentCol2--;

            if (col_op > 2 || col_op < 0) {
                throw new RuntimeException("col_op is not in acceptable range");
            }
            if (row_op > 2 || row_op < 0) {
                throw new RuntimeException("row_op is not in acceptable range");
            }

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) != null) {
                //same color?
                if (board.getPiece(new ChessPosition(currentRow2, currentCol2)).getTeamColor() != teamColor)
                    moves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
                //don't add anymore because we are blocked
                break;
            }

            if (isValidPosition(currentRow2, currentCol2) && board.getPiece(new ChessPosition(currentRow2, currentCol2)) == null) {
                moves.add(new ChessMove(position, new ChessPosition(currentRow2, currentCol2), null));
            }

        }

        return moves;
    }


    private Collection<ChessMove> getKnightMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> knightMoves = new ArrayList<>();
        Collection<ChessPosition> muevas = new ArrayList<>();
        muevas.add(new ChessPosition(position.getRow() + 2, position.getColumn() + 1));
        muevas.add(new ChessPosition(position.getRow() + 2, position.getColumn() - 1));
        muevas.add(new ChessPosition(position.getRow() - 2, position.getColumn() + 1));
        muevas.add(new ChessPosition(position.getRow() - 2, position.getColumn() - 1));
        muevas.add(new ChessPosition(position.getRow() + 1, position.getColumn() + 2));
        muevas.add(new ChessPosition(position.getRow() + 1, position.getColumn() - 2));
        muevas.add(new ChessPosition(position.getRow() - 1, position.getColumn() + 2));
        muevas.add(new ChessPosition(position.getRow() - 1, position.getColumn() - 2));

        for (ChessPosition pos : muevas)
        {
            if (isValidPosition(pos.getRow(), pos.getColumn()))
            {
                if (board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != teamColor)
                {
                    knightMoves.add(new ChessMove(position, pos, null));
                }
            }
        }
        return knightMoves;
    }

    private Collection<ChessMove> getKingMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor)
    {
        Collection<ChessMove> kingMoves = new ArrayList<>();
        Collection<ChessPosition> muevas = new ArrayList<>();
        muevas.add(new ChessPosition(position.getRow(), position.getColumn() + 1));
        muevas.add(new ChessPosition(position.getRow(), position.getColumn() - 1));
        muevas.add(new ChessPosition(position.getRow() - 1, position.getColumn()));
        muevas.add(new ChessPosition(position.getRow() + 1, position.getColumn()));
        muevas.add(new ChessPosition(position.getRow() + 1, position.getColumn() + 1));
        muevas.add(new ChessPosition(position.getRow() + 1, position.getColumn() - 1));
        muevas.add(new ChessPosition(position.getRow() - 1, position.getColumn() + 1));
        muevas.add(new ChessPosition(position.getRow() - 1, position.getColumn() - 1));

        for (ChessPosition pos : muevas)
        {
            if (isValidPosition(pos.getRow(), pos.getColumn()))
            {
                if (board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != teamColor)
                {
                    kingMoves.add(new ChessMove(position, pos, null));
                }
            }
        }
        return kingMoves;
    }


    //returns whether a move is on the board
    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ChessPiece other = (ChessPiece) obj;
        return pieceColor == other.pieceColor && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should not happen, as ChessPiece implements Cloneable
            throw new InternalError(e);
        }
    }

}
