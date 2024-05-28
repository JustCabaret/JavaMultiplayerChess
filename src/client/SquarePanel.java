package client;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

public class SquarePanel extends JPanel {

    private int row, column; // Row and column of the square on the chess board
    private ChessGUI cg; // Reference to the main GUI
    private JLabel imageLabel; // Label to display the piece image

    public static Image pieceImage[][] = new Image[2][6]; // Array to hold piece images
    private static String imageFilename[][] = {
        {"wp.gif", "wn.gif", "wb.gif", "wr.gif", "wq.gif", "wk.gif"},
        {"bp.gif", "bn.gif", "bb.gif", "br.gif", "bq.gif", "bk.gif"}
    };

    // Colors: 0 - white; 1 - black
    // Pieces: 0 - pawn; 1 - knight; 2 - bishop
    //         3 - rook; 4 - queen; 5 - king
    public SquarePanel(int x, int y, ChessGUI c) {
        row = x;
        column = y;
        cg = c;
        setPreferredSize(new Dimension(42, 42));
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(32, 32));
        add(imageLabel);
        addMouseListener(new SquareMouseListener());
    }

    // Load piece images from resources
    public static void loadPieceImages() {
        URL iconURL;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                iconURL = ClassLoader.getSystemResource("images/" + imageFilename[i][j]);
                pieceImage[i][j] = Toolkit.getDefaultToolkit().getImage(iconURL);
            }
        }
    }

    // Set the background color of the square
    public void setBackColor(int color) {
        if (color == 0) {
            setBackground(Color.white);
        } else {
            setBackground(Color.gray);
        }
    }

    // Set the piece image on the square
    public void setPiece(int color, int type) {
        imageLabel.setIcon(new ImageIcon(pieceImage[color][type]));
    }

    // Remove the piece image from the square
    public void removePiece() {
        imageLabel.setIcon(null);
    }

    // Inner class to handle mouse events on the square
    class SquareMouseListener extends MouseAdapter {

        // Highlight the square border when mouse enters
        public void mouseEntered(MouseEvent e) {
            setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        }

        // Remove the border highlight when mouse exits
        public void mouseExited(MouseEvent e) {
            setBorder(null);
        }

        // Handle the mouse press event to select the square
        public void mousePressed(MouseEvent e) {
            cg.selected(row, column);
            setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }
    }
}
