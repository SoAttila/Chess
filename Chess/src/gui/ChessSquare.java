package gui;

import javax.swing.*;
import java.awt.*;

public class ChessSquare extends JLabel {
    private final int x, y;

    public ChessSquare(int x, int y) {
        this.x = x;
        this.y = y;
        setPreferredSize(new Dimension(ChessConstants.WINDOW_SIZE / 10, ChessConstants.WINDOW_SIZE / 10));
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setOpaque(true);
    }

    public int getPosX() {
        return x;
    }

    public int getPosY() {
        return y;
    }

}
