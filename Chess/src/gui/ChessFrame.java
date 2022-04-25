package gui;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {
    private ChessGamePanel gamePanel;

    public ChessFrame() {
        setTitle(ChessConstants.TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(ChessConstants.WINDOW_SIZE, ChessConstants.WINDOW_SIZE));
        gamePanel = new ChessGamePanel();
        getContentPane().add(gamePanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
