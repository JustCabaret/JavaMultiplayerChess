package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ChessServer {
    public static void main(String[] args) {
        try {
            // Create and export a registry on port 1099
            LocateRegistry.createRegistry(1099);
            
            // Create an instance of the ChessGame implementation
            ChessGame game = new ChessGameImpl();
            
            // Bind the remote object's stub in the registry
            Naming.rebind("rmi://localhost:1099/ChessGame", game);
            
            // Indicate that the chess server is ready
            System.out.println("Chess server ready.");
        } catch (Exception e) {
            // Print the stack trace in case of an exception
            e.printStackTrace();
        }
    }
}
