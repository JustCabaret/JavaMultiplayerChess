package common;

import java.io.Serializable;

public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x, y; // Coordinates of the piece on the board
    private boolean white; // Color of the piece (true for white, false for black)
    private int type; // Type of the piece (0: Pawn, 1: Knight, 2: Bishop, 3: Rook, 4: Queen, 5: King)

    // Constructor to initialize a piece with its position, color, and type
    public Piece(int x, int y, boolean white, int type) {
        this.x = x;
        this.y = y;
        this.white = white;
        this.type = type;
    }

    // Method to move the piece to a new position
    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // Method to check if the piece is white
    public boolean isWhite() {
        return white;
    }

    // Method to get the x-coordinate of the piece
    public int getX() {
        return x;
    }

    // Method to get the y-coordinate of the piece
    public int getY() {
        return y;
    }

    // Method to get the type of the piece
    public int getType() {
        return type;
    }
}
