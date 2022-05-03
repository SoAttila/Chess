import gui.ChessFrame;

public class Boot {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new ChessFrame();
        });
    }
}
