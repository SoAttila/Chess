package gui;

import model.GameLogic;
import model.GameStatusEnum;
import model.Piece;
import model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ChessGamePanel extends JPanel {
    private final ChessSquare[][] squares;
    private GameLogic logic;

    public ChessGamePanel() {
        squares = new ChessSquare[ChessConstants.BOARD_SIZE][ChessConstants.BOARD_SIZE];
        logic = new GameLogic();
        setupGamePanel();
    }

    private ImageIcon getScaledImageIcon(ImageIcon baseImageIcon) {
        return new ImageIcon(baseImageIcon.getImage().getScaledInstance(ChessConstants.WINDOW_SIZE / 10, ChessConstants.WINDOW_SIZE / 10, java.awt.Image.SCALE_SMOOTH));
    }    private final MouseListener select = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            ChessSquare clickedSquare = ((ChessSquare) e.getComponent());
            logic.selectAndMovePiece(clickedSquare.getPosX(), clickedSquare.getPosY());
            for (Position pos : logic.getChangedPositions()) {
                updateSquare(pos);
            }
            for (int i = 0; i < ChessConstants.BOARD_SIZE; ++i)
                for (int j = 0; j < ChessConstants.BOARD_SIZE; ++j) {
                    if ((i + j) % 2 == 0) squares[i][j].setBackground(ChessConstants.LIGHT_SQUARE_COLOR);
                    else squares[i][j].setBackground(ChessConstants.DARK_SQUARE_COLOR);
                }
            for (Position pos : logic.getAttackedPositions()) {
                highlightPos(pos);
            }
            logic.getChangedPositions().clear();
            logic.updateGameStatus();
            if (logic.getGameStatus() != GameStatusEnum.ONGOING) {
                switch (logic.getGameStatus()) {
                    case WHITE_WIN -> JOptionPane.showMessageDialog(getParent(), "WHITE WIN!");
                    case BLACK_WIN -> JOptionPane.showMessageDialog(getParent(), "BLACK WIN!");
                    case DRAW -> JOptionPane.showMessageDialog(getParent(), "DRAW!");
                }
                for (ChessSquare[] row : squares)
                    for (ChessSquare square : row) square.removeMouseListener(select);
            }
            revalidate();
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    };

    private void setupGamePanel() {
        setLayout(new GridLayout(ChessConstants.BOARD_SIZE, ChessConstants.BOARD_SIZE));
        updateGamePanel();
    }

    private void highlightPos(Position pos) {
        int x = pos.getX(), y = pos.getY();

        if ((x + y) % 2 == 0) squares[x][y].setBackground(ChessConstants.ATTACKED_LIGHT_SQUARE_COLOR);
        else squares[x][y].setBackground(ChessConstants.ATTACKED_DARK_SQUARE_COLOR);
    }

    private void updateGamePanel() {
        removeAll();
        for (int j = 0; j < ChessConstants.BOARD_SIZE; ++j) {
            for (int i = 0; i < ChessConstants.BOARD_SIZE; ++i) {
                squares[i][j] = new ChessSquare(i, j);
                if ((i + j) % 2 == 0) squares[i][j].setBackground(ChessConstants.LIGHT_SQUARE_COLOR);
                else squares[i][j].setBackground(ChessConstants.DARK_SQUARE_COLOR);
                squares[i][j].addMouseListener(select);
                add(squares[i][j]);
                Piece squareModel = logic.getBoard().getPieceByIndices(i, j);
                if (squareModel != null) {
                    switch (squareModel.getPlayer()) {
                        case WHITE:
                            switch (squareModel.getType()) {
                                case KING -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_KING));
                                case QUEEN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_QUEEN));
                                case ROOK -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_ROOK));
                                case KNIGHT -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_KNIGHT));
                                case BISHOP -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_BISHOP));
                                case PAWN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_PAWN));
                            }
                            break;
                        case BLACK:
                            switch (squareModel.getType()) {
                                case KING -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_KING));
                                case QUEEN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_QUEEN));
                                case ROOK -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_ROOK));
                                case KNIGHT -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_KNIGHT));
                                case BISHOP -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_BISHOP));
                                case PAWN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_PAWN));
                            }
                            break;
                    }
                }
            }
        }
        if (logic.getSelectedPiecePos() != null) {
            int x = logic.getSelectedPiecePos().getX();
            int y = logic.getSelectedPiecePos().getY();
            squares[x][y].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
        }
        revalidate();
        repaint();
    }

    public void updateSquare(Position pos) {
        int i = pos.getX();
        int j = pos.getY();

        Piece squareModel = logic.getBoard().getPieceByIndices(i, j);
        if (squareModel != null) {
            switch (squareModel.getPlayer()) {
                case WHITE:
                    switch (squareModel.getType()) {
                        case KING -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_KING));
                        case QUEEN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_QUEEN));
                        case ROOK -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_ROOK));
                        case KNIGHT -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_KNIGHT));
                        case BISHOP -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_BISHOP));
                        case PAWN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.WHITE_PAWN));
                    }
                    break;
                case BLACK:
                    switch (squareModel.getType()) {
                        case KING -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_KING));
                        case QUEEN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_QUEEN));
                        case ROOK -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_ROOK));
                        case KNIGHT -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_KNIGHT));
                        case BISHOP -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_BISHOP));
                        case PAWN -> squares[i][j].setIcon(getScaledImageIcon(ChessConstants.BLACK_PAWN));
                    }
                    break;
            }
        } else squares[i][j].setIcon(null);

        if (logic.getSelectedPiecePos() != null && i == logic.getSelectedPiecePos().getX() && j == logic.getSelectedPiecePos().getY()) {
            squares[i][j].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
        } else squares[i][j].setBorder(null);

        revalidate();
        repaint();
    }




}
