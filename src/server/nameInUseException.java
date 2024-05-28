package server;

// Exception thrown when a name is already in use
public class nameInUseException extends Exception {
    // Constructor with a message parameter
    public nameInUseException(String message) {
        super(message); // Call the superclass constructor with the message
    }
}
