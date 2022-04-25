package model;

public class Piece {
    //private Position pos;
    private PlayerEnum player;
    private PieceEnum type;
    private boolean moved;

    public Piece(PlayerEnum player, PieceEnum type) {
        this.player = player;
        this.type = type;
        this.moved = false;
    }

    public PieceEnum getType() {
        return type;
    }

    public PlayerEnum getPlayer() {
        return player;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @Override
    public String toString() {
        return (player == PlayerEnum.WHITE ? "w" : "b") + type.name();
    }
}
