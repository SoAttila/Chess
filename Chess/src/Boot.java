import gui.ChessFrame;

public class Boot {
    public static void main(String[] args) {
        //Board board=new Board();
        //System.out.println(board);
        java.awt.EventQueue.invokeLater(() -> {
            new ChessFrame();
        });
    }
}
