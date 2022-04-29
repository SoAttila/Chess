package model;

public class Board {
    private Piece[][] pieces;

    public Board() {
        pieces = new Piece[8][8];
        for (int i = 0; i < 8; ++i) {
            //PAWNS
            pieces[i][1] = new Piece(PlayerEnum.BLACK, PieceEnum.PAWN);
            pieces[i][6] = new Piece(PlayerEnum.WHITE, PieceEnum.PAWN);
        }
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                //ROOKS
                pieces[j * 7][i * 7] = new Piece((i == 0 ? PlayerEnum.BLACK : PlayerEnum.WHITE), PieceEnum.ROOK);
                //KNIGHTS
                pieces[1 + j * 5][i * 7] = new Piece((i == 0 ? PlayerEnum.BLACK : PlayerEnum.WHITE), PieceEnum.KNIGHT);
                //BISHOPS
                pieces[2 + j * 3][i * 7] = new Piece((i == 0 ? PlayerEnum.BLACK : PlayerEnum.WHITE), PieceEnum.BISHOP);
            }
            //QUEEN
            pieces[3][i * 7] = new Piece((i == 0 ? PlayerEnum.BLACK : PlayerEnum.WHITE), PieceEnum.QUEEN);
            //KING
            pieces[4][i * 7] = new Piece((i == 0 ? PlayerEnum.BLACK : PlayerEnum.WHITE), PieceEnum.KING);
        }
    }

    public Board(Board board) {
        pieces = new Piece[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                pieces[i][j] = board.getPieceByIndices(i, j);
            }
        }
    }

    @Override
    public String toString() {
        String piecesString = "";
        for (int i = 0; i < 8; ++i) {
            piecesString += "[";
            for (int j = 0; j < 8; ++j) {
                piecesString += getPieceByIndices(j, i);
                if (j != 7) piecesString += ", ";
            }
            piecesString += "]\n";
        }
        return "Board{" +
                "pieces=\n" + piecesString +
                '}';
    }

    public Piece getPieceByNotation(String pos) {
        //TBD error handling
        char fileChar = pos.charAt(0); //x
        char rankChar = pos.charAt(1); //y
        int x = 0, y;
        switch (fileChar) {
            case 'a' -> x = 0;
            case 'b' -> x = 1;
            case 'c' -> x = 2;
            case 'd' -> x = 3;
            case 'e' -> x = 4;
            case 'f' -> x = 5;
            case 'g' -> x = 6;
            case 'h' -> x = 7;
        }
        y = 8 - Character.getNumericValue(rankChar);

        return pieces[x][y];
    }

    public Piece getPieceByIndices(int x, int y) {
        return pieces[x][y];
    }

    public void setPieceByIndices(int x, int y, Piece piece) {
        pieces[x][y] = piece;
    }

    public Piece[][] getPieces() {
        return pieces;
    }
}
