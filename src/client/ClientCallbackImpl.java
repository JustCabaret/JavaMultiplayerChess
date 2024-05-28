package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientCallbackImpl extends UnicastRemoteObject implements ClientCallback {

    private ChessGUI gui; // Reference to the ChessGUI

    // Constructor to initialize the callback with the GUI reference
    public ClientCallbackImpl(ChessGUI gui) throws RemoteException {
        this.gui = gui;
    }

    // Method to update the chess board
    @Override
    public void updateBoard() throws RemoteException {
        gui.updateBoard();
    }

    // Method to receive a message and pass it to the GUI
    @Override
    public void receiveMessage(String message) throws RemoteException {
        gui.receiveMessage(message);
    }
}
