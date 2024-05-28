package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import common.Piece;
import client.ClientCallback;

public interface ChessGame extends Remote {
    
    // Move a piece from the origin position to the destination position
    void movePiece(String player, int xOrigin, int yOrigin, int xDestiny, int yDestiny) throws RemoteException;
    
    // Obtain the current state of the board
    Piece[][] obtainBoard() throws RemoteException;
    
    // Register a client callback for updates
    void registerCallback(ClientCallback callback) throws RemoteException;
    
    // Remove a client callback
    void removeCallback(ClientCallback callback) throws RemoteException;
    
    // Register a player with a given username
    String registerPlayer(String username) throws RemoteException, nameInUseException;
    
    // Reset the board to the initial state
    void tidyPieces() throws RemoteException;
    
    // Send a message from a player to all clients
    void sendMessage(String player, String message) throws RemoteException;
    
    // Remove all pieces from the board
    void removePieces() throws RemoteException;
    
    // Add a piece to the board at the specified position
    void addPiece(int color, int type, int x, int y) throws RemoteException;
    
    // Remove a piece from the board at the specified position
    void removePiece(int x, int y) throws RemoteException;
    
    // Switch the role of the specified user to player1
    String switchToPlayer1(String username) throws RemoteException;
    
    // Switch the role of the specified user to player2
    String switchToPlayer2(String username) throws RemoteException;
    
    // Switch the role of the specified user to spectator
    String switchToSpectator(String username) throws RemoteException;
    
    // Remove the specified user from the game
    void leave(String username) throws RemoteException;
    
    // Get the username of player1
    String getPlayer1() throws RemoteException;
    
    // Get the username of player2
    String getPlayer2() throws RemoteException;
    
    // Get the list of spectators
    List<String> getSpectators() throws RemoteException;
}
