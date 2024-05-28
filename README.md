
# Java Multiplayer Chess

This project provides a multiplayer chess application implemented in Java. It utilizes Java RMI (Remote Method Invocation) to allow multiple users to play chess in real-time over a network. The application features a graphical user interface (GUI) for easy interaction.

## Features

- **GUI for Interaction**: A user-friendly interface that allows seamless interaction with the chess functionalities.
- **Multiplayer Chess**: Users can join a game and play chess against each other in real-time.
- **Roles**: Players can join as Player 1, Player 2, or Spectators.
- **Real-time Updates**: The chess board updates in real-time for all connected players and spectators.
- **Chat Functionality**: Includes a chat feature for players to communicate during the game.

## Preview

![image](https://github.com/JustCabaret/JavaMultiplayerChess/assets/67253335/1ce450f7-44b4-4368-b7c8-927d27b77666)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or above.

### Running the Application

1. **Clone the repository to your local machine:**
   ```sh
   git clone https://github.com/JustCabaret/JavaMultiplayerChess.git
   cd JavaMultiplayerChess
   ```

2. **Start the Chess Server:**
   Navigate to the `server` directory and start the server:
   ```sh
   cd src/server
   javac ChessServer.java
   java server.ChessServer
   ```

3. **Run the Chess Client:**
   In a new terminal, navigate to the `cliente` directory and run the client:
   ```sh
   cd src/cliente
   javac InicioGUI.java
   java cliente.InicioGUI
   ```

## Usage

- Upon launching the application, enter the server's hostname/IP, port number, and your username.
- Click "Join" to connect to the server and enter the game lobby.
- Depending on availability, you can join as Player 1, Player 2, or a Spectator.
- Use the chess board to make moves if you are a player. Spectators can watch the game in real-time.
- Utilize the chat feature to communicate with other players.

## Contributing

Feel free to fork the project, submit pull requests, report bugs, or suggest features.

## Authors

- **Hugo Cabaret** - [JustCabaret](https://github.com/JustCabaret)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
