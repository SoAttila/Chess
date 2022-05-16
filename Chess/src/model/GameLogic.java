package model;

import gui.ChessConstants;

import javax.crypto.AEADBadTagException;
import java.util.ArrayList;

public class GameLogic {
    private Board board;
    private PlayerEnum turn;
    private GameStatusEnum gameStatus;
    private Position selectedPiecePos;
    private ArrayList<Position> changedPositions;
    private final ArrayList<Position> attackedPositions;
    private Position blackKingPos, whiteKingPos;
    private Position possibleEnPassantPos;

    public GameLogic() {
        board = new Board();
        turn = PlayerEnum.WHITE;
        gameStatus = GameStatusEnum.ONGOING;
        selectedPiecePos = null;
        changedPositions = new ArrayList<>();
        attackedPositions = new ArrayList<>();
        blackKingPos = new Position(4, 0);
        whiteKingPos = new Position(4, 7);
    }

    public void selectAndMovePiece(int x, int y) {
        //select
        if (board.getPieceByIndices(x, y) != null && turn == board.getPieceByIndices(x, y).getPlayer()) {

            if (selectedPiecePos != null) changedPositions.add(selectedPiecePos);
            selectedPiecePos = new Position(x, y);
            changedPositions.add(selectedPiecePos);
        }
        //move
        else if (getSelectedPiecePos() != null && (board.getPieceByIndices(x, y) == null || turn != board.getPieceByIndices(x, y).getPlayer())) {
            int selectedX = selectedPiecePos.getX();
            int selectedY = selectedPiecePos.getY();
            MoveTypeEnum moveType = getMoveType(board.getPieceByIndices(selectedX, selectedY), selectedPiecePos, new Position(x, y));

            if (moveType != MoveTypeEnum.ILLEGAL) {
                Board tempBoard = new Board(board);
                Position tempSelectedPiecePos = selectedPiecePos;
                Position tempBlackKingPos = blackKingPos, tempWhiteKingPos = whiteKingPos;
                Position tempPossibleEnPassantPos = possibleEnPassantPos;

                board.getPieceByIndices(selectedX, selectedY).setMoved(true);
                if (moveType == MoveTypeEnum.EN_PASSANT) {
                    board.setPieceByIndices(possibleEnPassantPos.getX(), possibleEnPassantPos.getY() + (turn == PlayerEnum.WHITE ? 1 : -1), null);
                    changedPositions.add(new Position(possibleEnPassantPos.getX(), possibleEnPassantPos.getY() + (turn == PlayerEnum.WHITE ? 1 : -1)));
                } else if (moveType == MoveTypeEnum.SHORT_CASTLING) {
                    board.getPieceByIndices(7, selectedY).setMoved(true);
                    board.setPieceByIndices(selectedX + 1, selectedY, board.getPieceByIndices(7, selectedY));
                    board.setPieceByIndices(7, selectedY, null);
                    if (selectedY == 0) blackKingPos = new Position(x, y);
                    else whiteKingPos = new Position(x, y);
                    changedPositions.add(new Position(selectedX + 1, selectedY));
                    changedPositions.add(new Position(7, selectedY));
                } else if (moveType == MoveTypeEnum.LONG_CASTLING) {
                    board.getPieceByIndices(0, selectedY).setMoved(true);
                    board.setPieceByIndices(selectedX - 1, selectedY, board.getPieceByIndices(0, selectedY));
                    board.setPieceByIndices(0, selectedY, null);
                    if (selectedY == 0) blackKingPos = new Position(x, y);
                    else whiteKingPos = new Position(x, y);
                    changedPositions.add(new Position(selectedX - 1, selectedY));
                    changedPositions.add(new Position(0, selectedY));
                } else if (moveType == MoveTypeEnum.KING_MOVE) {
                    if (turn == PlayerEnum.BLACK) blackKingPos = new Position(x, y);
                    else whiteKingPos = new Position(x, y);
                } else if (moveType == MoveTypeEnum.PROMOTION) {
                    board.setPieceByIndices(selectedX, selectedY, new Piece(turn, PieceEnum.QUEEN));
                }
                if (moveType == MoveTypeEnum.PAWN_2_SQUARE)
                    possibleEnPassantPos = new Position(selectedX, selectedY + (turn == PlayerEnum.WHITE ? -1 : 1));
                else possibleEnPassantPos = null;
                changedPositions.add(selectedPiecePos);
                changedPositions.add(new Position(x, y));
                board.setPieceByIndices(x, y, board.getPieceByIndices(selectedX, selectedY));
                board.setPieceByIndices(selectedX, selectedY, null);
                selectedPiecePos = null;

                if (isInCheck(turn)) {
                    board = tempBoard;
                    selectedPiecePos = tempSelectedPiecePos;
                    changedPositions.clear();
                    blackKingPos = tempBlackKingPos;
                    whiteKingPos = tempWhiteKingPos;
                    possibleEnPassantPos = tempPossibleEnPassantPos;
                    if (moveType == MoveTypeEnum.SHORT_CASTLING) {
                        board.getPieceByIndices(7, selectedY).setMoved(false);
                        board.getPieceByIndices(selectedX, selectedY).setMoved(false);
                    } else if (moveType == MoveTypeEnum.LONG_CASTLING) {
                        board.getPieceByIndices(0, selectedY).setMoved(false);
                        board.getPieceByIndices(selectedX, selectedY).setMoved(false);
                    }
                } else {
                    nextTurn();
                }
            }
        }
        updateAttackedPositions();
    }

    public void updateAttackedPositions() {
        attackedPositions.clear();
        if (selectedPiecePos!=null) {
            for (int i=0;i<ChessConstants.BOARD_SIZE;++i)
                for (int j=0;j<ChessConstants.BOARD_SIZE;++j) {
                    Position attackedPos=new Position(i,j);
                    if (isLegalMove(getBoard().getPieceByIndices(selectedPiecePos.getX(), selectedPiecePos.getY()),selectedPiecePos,attackedPos))
                        attackedPositions.add(attackedPos);
                }
        }
    }

    private MoveTypeEnum getMoveType(Piece piece, Position from, Position to) {
        int fx = from.getX(), fy = from.getY(), tx = to.getX(), ty = to.getY();
        int dx = Math.abs(fx - tx), dy = Math.abs(fy - ty);
        Piece[][] pieces = board.getPieces();
        if (pieces[tx][ty] != null && piece.getPlayer() == pieces[tx][ty].getPlayer()) return MoveTypeEnum.ILLEGAL;
        switch (piece.getType()) {
            case PAWN:
                switch (piece.getPlayer()) {
                    case BLACK:
                        //pawn capture
                        if (pieces[tx][ty] != null && ((fx == tx - 1 && fy == ty - 1) || (fx == tx + 1 && fy == ty - 1))) {
                            if (ty == 7)
                                return MoveTypeEnum.PROMOTION;
                            return MoveTypeEnum.MOVE;
                        } else if (possibleEnPassantPos != null && tx == possibleEnPassantPos.getX() && ty == possibleEnPassantPos.getY() && fy == 4 && Math.abs(fx - tx) == 1)
                            return MoveTypeEnum.EN_PASSANT;
                            //pawn step
                        else if (pieces[tx][ty] == null) {
                            if (fx == tx && fy == ty - 1) {
                                if (ty == 7)
                                    return MoveTypeEnum.PROMOTION;
                                return MoveTypeEnum.MOVE;
                            } else if (fy == 1 && (fx == tx && fy == ty - 2) && pieces[fx][fy + 1] == null)
                                return MoveTypeEnum.PAWN_2_SQUARE;
                        }
                        return MoveTypeEnum.ILLEGAL;
                    case WHITE:
                        //pawn capture
                        if (pieces[tx][ty] != null && ((fx == tx - 1 && fy == ty + 1) || (fx == tx + 1 && fy == ty + 1))) {
                            if (ty == 0)
                                return MoveTypeEnum.PROMOTION;
                            return MoveTypeEnum.MOVE;
                        } else if (possibleEnPassantPos != null && tx == possibleEnPassantPos.getX() && ty == possibleEnPassantPos.getY() && fy == 3 && Math.abs(fx - tx) == 1) {
                            return MoveTypeEnum.EN_PASSANT;
                        }
                        //pawn step
                        else if (pieces[tx][ty] == null) {
                            if (fx == tx && fy == ty + 1) {
                                if (ty == 0)
                                    return MoveTypeEnum.PROMOTION;
                                return MoveTypeEnum.MOVE;
                            } else if (fy == 6 && (fx == tx && fy == ty + 2) && pieces[fx][fy - 1] == null)
                                return MoveTypeEnum.PAWN_2_SQUARE;
                        }
                        return MoveTypeEnum.ILLEGAL;
                }
            case KNIGHT:
                if ((dx == 1 && dy == 2) || (dx == 2 && dy == 1))
                    return MoveTypeEnum.MOVE;
                return MoveTypeEnum.ILLEGAL;
            case ROOK:
                if (fx == tx) {
                    int startY = Math.min(fy, ty);
                    for (int i = 1; i < Math.abs(fy - ty); ++i) {
                        if (pieces[fx][startY + i] != null) return MoveTypeEnum.ILLEGAL;
                    }
                    return MoveTypeEnum.MOVE;
                } else if (fy == ty) {
                    int startX = Math.min(fx, tx);
                    for (int i = 1; i < Math.abs(fx - tx); ++i) {
                        if (pieces[startX + i][fy] != null) return MoveTypeEnum.ILLEGAL;
                    }
                    return MoveTypeEnum.MOVE;
                }
                return MoveTypeEnum.ILLEGAL;
            case BISHOP:
                if (dx == dy) {
                    int cx = (fx < tx ? 1 : -1), cy = (fy < ty ? 1 : -1);
                    for (int i = 1; i < dx; ++i) {
                        if (pieces[fx + i * cx][fy + i * cy] != null) return MoveTypeEnum.ILLEGAL;
                    }
                    return MoveTypeEnum.MOVE;
                }
                return MoveTypeEnum.ILLEGAL;
            case QUEEN:
                if (dx == dy) {
                    int cx = (fx < tx ? 1 : -1), cy = (fy < ty ? 1 : -1);
                    for (int i = 1; i < dx; ++i) {
                        if (pieces[fx + i * cx][fy + i * cy] != null) return MoveTypeEnum.ILLEGAL;
                    }
                    return MoveTypeEnum.MOVE;
                } else if (fx == tx) {
                    int startY = Math.min(fy, ty);
                    for (int i = 1; i < Math.abs(fy - ty); ++i) {
                        if (pieces[fx][startY + i] != null) return MoveTypeEnum.ILLEGAL;
                    }
                    return MoveTypeEnum.MOVE;
                } else if (fy == ty) {
                    int startX = Math.min(fx, tx);
                    for (int i = 1; i < Math.abs(fx - tx); ++i) {
                        if (pieces[startX + i][fy] != null) return MoveTypeEnum.ILLEGAL;
                    }
                    return MoveTypeEnum.MOVE;
                }
                return MoveTypeEnum.ILLEGAL;
            case KING:
                if (dx <= 1 && dy <= 1)
                    return MoveTypeEnum.KING_MOVE;
                else if (!pieces[fx][fy].isMoved() && fy == ty && !isInCheck(piece.getPlayer())) {
                    if (tx == 2 && pieces[0][ty] != null && !pieces[0][ty].isMoved()) {
                        for (int i = 1; i < 4; ++i)
                            if (pieces[i][ty] != null || (i!=1 && isInCheck(piece.getPlayer(), new Position(i, ty))))
                                return MoveTypeEnum.ILLEGAL;
                        return MoveTypeEnum.LONG_CASTLING;
                    } else if (tx == 6 && pieces[7][ty] != null && !pieces[7][ty].isMoved()) {
                        for (int i = 5; i < 7; ++i)
                            if (pieces[i][ty] != null || isInCheck(piece.getPlayer(), new Position(i, ty)))
                                return MoveTypeEnum.ILLEGAL;
                        return MoveTypeEnum.SHORT_CASTLING;
                    }
                }
                return MoveTypeEnum.ILLEGAL;
        }
        return MoveTypeEnum.ILLEGAL;
    }

    private boolean isInCheck(PlayerEnum team) {
        for (int i = 0; i < ChessConstants.BOARD_SIZE; ++i) {
            for (int j = 0; j < ChessConstants.BOARD_SIZE; ++j) {
                Piece piece = board.getPieces()[i][j];
                if (piece != null && piece.getPlayer() != team && getMoveType(piece, new Position(i, j), (team == PlayerEnum.BLACK ? blackKingPos : whiteKingPos)) != MoveTypeEnum.ILLEGAL)
                    return true;
            }
        }
        return false;
    }

    private boolean isInCheck(PlayerEnum team, Position pos) {
        for (int i = 0; i < ChessConstants.BOARD_SIZE; ++i) {
            for (int j = 0; j < ChessConstants.BOARD_SIZE; ++j) {
                Piece piece = board.getPieces()[i][j];
                if (piece != null && piece.getPlayer() != team && getMoveType(piece, new Position(i, j), pos) != MoveTypeEnum.ILLEGAL)
                    return true;
            }
        }
        return false;
    }

    private boolean isLegalMove(Piece piece, Position from, Position to) {
        MoveTypeEnum moveType = getMoveType(piece, from, to);
        if (moveType != MoveTypeEnum.ILLEGAL) {
            Board tempBoard = new Board(board);
            Position tempSelectedPiecePos = selectedPiecePos;
            ArrayList<Position> tempChangedPositions=new ArrayList<>(changedPositions);
            Position tempBlackKingPos = blackKingPos, tempWhiteKingPos = whiteKingPos;
            Position tempPossibleEnPassantPos = possibleEnPassantPos;

            int selectedX = from.getX(), selectedY = from.getY();
            int x = to.getX(), y = to.getY();

            if (moveType == MoveTypeEnum.EN_PASSANT) {
                board.setPieceByIndices(possibleEnPassantPos.getX(), possibleEnPassantPos.getY() + (turn == PlayerEnum.WHITE ? 1 : -1), null);
                changedPositions.add(new Position(possibleEnPassantPos.getX(), possibleEnPassantPos.getY() + (turn == PlayerEnum.WHITE ? 1 : -1)));
            } else if (moveType == MoveTypeEnum.SHORT_CASTLING) {
                board.setPieceByIndices(selectedX + 1, selectedY, board.getPieceByIndices(7, selectedY));
                board.setPieceByIndices(7, selectedY, null);
                if (selectedY == 0) blackKingPos = new Position(x, y);
                else whiteKingPos = new Position(selectedX, selectedY);
                changedPositions.add(new Position(selectedX + 1, selectedY));
                changedPositions.add(new Position(7, selectedY));
            } else if (moveType == MoveTypeEnum.LONG_CASTLING) {
                board.setPieceByIndices(selectedX - 1, selectedY, board.getPieceByIndices(0, selectedY));
                board.setPieceByIndices(0, selectedY, null);
                if (selectedY == 0) blackKingPos = new Position(x, y);
                else whiteKingPos = new Position(x, y);
                changedPositions.add(new Position(selectedX - 1, selectedY));
                changedPositions.add(new Position(0, selectedY));
            } else if (moveType == MoveTypeEnum.KING_MOVE) {
                if (turn == PlayerEnum.BLACK) blackKingPos = new Position(x, y);
                else whiteKingPos = new Position(x, y);
            } else if (moveType == MoveTypeEnum.PROMOTION) {
                board.setPieceByIndices(selectedX, selectedY, new Piece(turn, PieceEnum.QUEEN));
            }
            if (moveType == MoveTypeEnum.PAWN_2_SQUARE)
                possibleEnPassantPos = new Position(selectedX, selectedY + (turn == PlayerEnum.WHITE ? -1 : 1));
            else possibleEnPassantPos = null;
            changedPositions.add(selectedPiecePos);
            changedPositions.add(new Position(x, y));
            board.setPieceByIndices(x, y, board.getPieceByIndices(selectedX, selectedY));
            board.setPieceByIndices(selectedX, selectedY, null);
            selectedPiecePos = null;

            boolean isInCheck = isInCheck(piece.getPlayer());

            board = tempBoard;
            selectedPiecePos = tempSelectedPiecePos;
            changedPositions=tempChangedPositions;
            blackKingPos = tempBlackKingPos;
            whiteKingPos = tempWhiteKingPos;
            possibleEnPassantPos = tempPossibleEnPassantPos;

            return !isInCheck;
        }
        return false;
    }

    private boolean isThereAnyLegalMove() {
        for (int i = 0; i < ChessConstants.BOARD_SIZE; ++i) {
            for (int j = 0; j < ChessConstants.BOARD_SIZE; ++j) {
                Piece piece = board.getPieceByIndices(i, j);
                if (piece != null && piece.getPlayer() == turn) {
                    for (int ti = 0; ti < ChessConstants.BOARD_SIZE; ++ti) {
                        for (int tj = 0; tj < ChessConstants.BOARD_SIZE; ++tj) {
                            if (isLegalMove(piece, new Position(i, j), new Position(ti, tj))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void nextTurn() {
        switch (turn) {
            case WHITE -> turn = PlayerEnum.BLACK;
            case BLACK -> turn = PlayerEnum.WHITE;
        }
    }

    public void updateGameStatus() {
        if (!isThereAnyLegalMove()) {
            if (isInCheck(turn)) {
                gameStatus = (turn == PlayerEnum.WHITE ? GameStatusEnum.BLACK_WIN : GameStatusEnum.WHITE_WIN);
            } else gameStatus = GameStatusEnum.DRAW;
        } else gameStatus = GameStatusEnum.ONGOING;
    }

    public Board getBoard() {
        return board;
    }

    public Position getSelectedPiecePos() {
        return selectedPiecePos;
    }

    public ArrayList<Position> getChangedPositions() {
        return changedPositions;
    }

    public ArrayList<Position> getAttackedPositions() {
        return attackedPositions;
    }

    public GameStatusEnum getGameStatus() {
        return gameStatus;
    }
}
