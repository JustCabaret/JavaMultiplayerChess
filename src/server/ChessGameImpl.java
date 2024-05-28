package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import common.Piece;
import client.ClientCallback;

public class ChessGameImpl extends UnicastRemoteObject implements ChessGame {

    private Piece[][] board = new Piece[8][8];
    private List<ClientCallback> clients = new ArrayList<>();
    private List<String> spectators = new ArrayList<>();
    private String player1 = null;
    private String player2 = null;

    public ChessGameImpl() throws RemoteException {
        initializePieces(); // Initialize the chess pieces on the board
    }

    private void initializePieces() {
        // Initialize board with null values
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }

        // Set up white pieces
        board[7][0] = new Piece(7, 0, true, 3); // Rook
        board[7][1] = new Piece(7, 1, true, 1); // Knight
        board[7][2] = new Piece(7, 2, true, 2); // Bishop
        board[7][3] = new Piece(7, 3, true, 4); // Queen
        board[7][4] = new Piece(7, 4, true, 5); // King
        board[7][5] = new Piece(7, 5, true, 2); // Bishop
        board[7][6] = new Piece(7, 6, true, 1); // Knight
        board[7][7] = new Piece(7, 7, true, 3); // Rook
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Piece(6, i, true, 0); // Pawns
        }

        // Set up black pieces
        board[0][0] = new Piece(0, 0, false, 3); // Rook
        board[0][1] = new Piece(0, 1, false, 1); // Knight
        board[0][2] = new Piece(0, 2, false, 2); // Bishop
        board[0][3] = new Piece(0, 3, false, 4); // Queen
        board[0][4] = new Piece(0, 4, false, 5); // King
        board[0][5] = new Piece(0, 5, false, 2); // Bishop
        board[0][6] = new Piece(0, 6, false, 1); // Knight
        board[0][7] = new Piece(0, 7, false, 3); // Rook
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Piece(1, i, false, 0); // Pawns
        }
    }

    @Override
    public void movePiece(String player, int xOrigin, int yOrigin, int xDestiny, int yDestiny) throws RemoteException {
        Piece piece = board[xOrigin][yOrigin];
        if (piece != null) {
            String movement = String.format("%s moved %s-%s", player, algebricNotation(xOrigin, yOrigin), algebricNotation(xDestiny, yDestiny));
            System.out.println(movement);
            board[xOrigin][yOrigin] = null;
            piece.move(xDestiny, yDestiny);
            board[xDestiny][yDestiny] = piece;
            notifyClients();
        }
    }

    @Override
    public Piece[][] obtainBoard() throws RemoteException {
        return board;
    }

    @Override
    public void registerCallback(ClientCallback callback) throws RemoteException {
        clients.add(callback);
    }

    @Override
    public void removeCallback(ClientCallback callback) throws RemoteException {
        clients.remove(callback);
    }

    @Override
    public synchronized String registerPlayer(String username) throws RemoteException, nameInUseException {
        if (spectators.contains(username) || username.equals(player1) || username.equals(player2)) {
            throw new nameInUseException("The username '" + username + "' is already in use.");
        }
        if (player1 == null) {
            player1 = username;
            System.out.println(username + " joined the game as player1");
            return "player1";
        } else if (player2 == null) {
            player2 = username;
            System.out.println(username + " joined the game as player2");
            return "player2";
        } else {
            spectators.add(username);
            System.out.println(username + " joined the game as a spectator");
            return "spectator";
        }
    }

    @Override
    public void tidyPieces() throws RemoteException {
        initializePieces(); // Reset the board to the initial state
        notifyClients();
        System.out.println("The pieces have been reset.");
    }

    @Override
    public void sendMessage(String player, String message) throws RemoteException {
        String fullMessage = player + ": " + message;
        System.out.println(fullMessage);
        for (ClientCallback client : clients) {
            client.receiveMessage(fullMessage);
        }
    }

    @Override
    public void removePieces() throws RemoteException {
        // Clear the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        notifyClients();
        System.out.println("All pieces have been removed from the board.");
    }

    @Override
    public void addPiece(int color, int type, int x, int y) throws RemoteException {
        board[x][y] = new Piece(x, y, color == 0, type);
        notifyClients();
        System.out.println(String.format("Piece added at %s: %s %s", algebricNotation(x, y), (color == 0 ? "White" : "Black"), pieceType(type)));
    }

    @Override
    public void removePiece(int x, int y) throws RemoteException {
        board[x][y] = null;
        notifyClients();
        System.out.println(String.format("Piece removed from %s", algebricNotation(x, y)));
    }

    @Override
    public synchronized String switchToPlayer1(String username) throws RemoteException {
        if (player1 != null && !player1.equals(username)) {
            throw new RemoteException("Player 1 is already taken.");
        }
        if (player2 != null && player2.equals(username)) {
            player2 = null;
        } else if (spectators.contains(username)) {
            spectators.remove(username);
        }
        player1 = username;
        System.out.println(username + " is now player1");
        notifyClients();
        return "player1";
    }

    @Override
    public synchronized String switchToPlayer2(String username) throws RemoteException {
        if (player2 != null && !player2.equals(username)) {
            throw new RemoteException("Player 2 is already taken.");
        }
        if (player1 != null && player1.equals(username)) {
            player1 = null;
        } else if (spectators.contains(username)) {
            spectators.remove(username);
        }
        player2 = username;
        System.out.println(username + " is now player2");
        notifyClients();
        return "player2";
    }

    @Override
    public synchronized String switchToSpectator(String username) throws RemoteException {
        if (player1 != null && player1.equals(username)) {
            player1 = null;
        } else if (player2 != null && player2.equals(username)) {
            player2 = null;
        }
        spectators.add(username);
        System.out.println(username + " is now a spectator");
        notifyClients();
        return "spectator";
    }

    @Override
    public synchronized void leave(String username) throws RemoteException {
        if (player1 != null && player1.equals(username)) {
            player1 = null;
        } else if (player2 != null && player2.equals(username)) {
            player2 = null;
        } else {
            spectators.remove(username);
        }
        System.out.println(username + " left the game.");
        notifyClients();
    }

    @Override
    public synchronized String getPlayer1() throws RemoteException {
        return player1;
    }

    @Override
    public synchronized String getPlayer2() throws RemoteException {
        return player2;
    }

    @Override
    public synchronized List<String> getSpectators() throws RemoteException {
        return new ArrayList<>(spectators);
    }

    private String pieceType(int type) {
        switch (type) {
            case 0: return "Pawn";
            case 1: return "Knight";
            case 2: return "Bishop";
            case 3: return "Rook";
            case 4: return "Queen";
            case 5: return "King";
            default: return "Unknown";
        }
    }

    private void notifyClients() throws RemoteException {
        // Notify all clients to update their boards
        for (ClientCallback client : clients) {
            client.updateBoard();
        }
    }

    private String algebricNotation(int x, int y) {
        char column = (char) ('a' + y);
        int row = 8 - x;
        return "" + column + row;
    }
}
