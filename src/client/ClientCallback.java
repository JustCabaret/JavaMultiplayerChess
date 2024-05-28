package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    
    // Method to update the chess board
    void updateBoard() throws RemoteException;

    // Method to receive a message from the server
    void receiveMessage(String message) throws RemoteException;
}
