package chess;
import java.util.Arrays;
import java.lang.Object;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //clear board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                this.squares[row][col] = null;
            }
        }


        //initialize white
        ChessPiece rookw1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        this.squares[0][0] = rookw1;
        ChessPiece rookw2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        this.squares[0][7] = rookw2;
        ChessPiece knightw1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        this.squares[0][1] = knightw1;
        ChessPiece knightw2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        this.squares[0][6] = knightw2;
        ChessPiece bishopw1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        this.squares[0][2] = bishopw1;
        ChessPiece bishopw2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        this.squares[0][5] = bishopw2;
        ChessPiece queenw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        this.squares[0][3] = queenw;
        ChessPiece kingw = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        this.squares[0][4] = kingw;

        ChessPiece pawnw1 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][0] = pawnw1;
        ChessPiece pawnw2 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][1] = pawnw2;
        ChessPiece pawnw3 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][2] = pawnw3;
        ChessPiece pawnw4 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][3] = pawnw4;
        ChessPiece pawnw5 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][4] = pawnw5;
        ChessPiece pawnw6 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][5] = pawnw6;
        ChessPiece pawnw7 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][6] = pawnw7;
        ChessPiece pawnw8 = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        this.squares[1][7] = pawnw8;

        //initialize black
        ChessPiece rookb1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        this.squares[7][0] = rookb1;
        ChessPiece rookb2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        this.squares[7][7] = rookb2;
        ChessPiece knightb1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        this.squares[7][1] = knightb1;
        ChessPiece knightb2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        this.squares[7][6] = knightb2;
        ChessPiece bishopb1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        this.squares[7][2] = bishopb1;
        ChessPiece bishopb2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        this.squares[7][5] = bishopb2;
        ChessPiece queenb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        this.squares[7][3] = queenb;
        ChessPiece kingb = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        this.squares[7][4] = kingb;

        ChessPiece pawnb1 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][0] = pawnb1;
        ChessPiece pawnb2 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][1] = pawnb2;
        ChessPiece pawnb3 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][2] = pawnb3;
        ChessPiece pawnb4 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][3] = pawnb4;
        ChessPiece pawnb5 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][4] = pawnb5;
        ChessPiece pawnb6 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][5] = pawnb6;
        ChessPiece pawnb7 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][6] = pawnb7;
        ChessPiece pawnb8 = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        this.squares[6][7] = pawnb8;

    }

@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ChessBoard otherBoard = (ChessBoard) obj;
        return Arrays.deepEquals(this.squares, otherBoard.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.squares);
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clonedBoard = (ChessBoard) super.clone();

            // Clone the individual ChessPiece objects
            clonedBoard.squares = new ChessPiece[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (this.squares[i][j] != null) {
                        clonedBoard.squares[i][j] = this.squares[i][j].clone();
                    }
                }
            }

            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            // This should not happen, as ChessBoard implements Cloneable
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
