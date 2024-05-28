package client;

import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import javax.swing.*;
import common.Piece;
import server.ChessGame;
import java.util.List;

public class ChessGUI extends JFrame {

    private SquarePanel[][] board = new SquarePanel[8][8];
    private ChessGame game;
    private Piece selectedPiece = null;
    private int selectedX, selectedY;
    private ClientCallback callback;
    private String username;
    private String role;

    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton sendButton;

    private JPanel availablePiecesPanel;
    private JButton[] availablePiecesButtons = new JButton[32];
    private Piece[] availablePieces = new Piece[32];

    private JButton player1Button;
    private JButton player2Button;
    private JButton spectatorButton;
    private JButton leaveButton;
    private JButton viewPlayersButton;

    public ChessGUI(ChessGame game, String username, String role) {
        this.game = game;
        this.username = username;
        this.role = role;

        try {
            callback = new ClientCallbackImpl(this);
            game.registerCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        setTitle("Chess Game - " + username + " (" + role + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                leaveGame();
            }
        });

        setLayout(new BorderLayout());

        JPanel chessPanel = new JPanel();
        chessPanel.setLayout(new GridLayout(8, 8));

        SquarePanel.loadPieceImages();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                SquarePanel sqPanel = new SquarePanel(i, j, this);
                sqPanel.setBackColor((i + j) % 2);
                board[i][j] = sqPanel;
                chessPanel.add(sqPanel);
            }
        }

        add(chessPanel, BorderLayout.CENTER);

        // Add side panel with buttons and chat
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.fill = GridBagConstraints.HORIZONTAL;
        gbcButton.insets = new Insets(5, 5, 5, 5);
        gbcButton.gridx = 0;

        JButton arrangeButton = new JButton("Arrange Pieces");
        gbcButton.gridy = 0;
        buttonPanel.add(arrangeButton, gbcButton);
        arrangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tidyPieces();
            }
        });

        JButton removeButton = new JButton("Remove Pieces");
        gbcButton.gridy = 1;
        buttonPanel.add(removeButton, gbcButton);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePieces();
            }
        });

        player1Button = new JButton("Switch to Player 1");
        gbcButton.gridy = 2;
        buttonPanel.add(player1Button, gbcButton);
        player1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToPlayer1();
            }
        });

        player2Button = new JButton("Switch to Player 2");
        gbcButton.gridy = 3;
        buttonPanel.add(player2Button, gbcButton);
        player2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToPlayer2();
            }
        });

        spectatorButton = new JButton("Switch to Spectator");
        gbcButton.gridy = 4;
        buttonPanel.add(spectatorButton, gbcButton);
        spectatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToSpectator();
            }
        });

        leaveButton = new JButton("Leave Game");
        gbcButton.gridy = 5;
        buttonPanel.add(leaveButton, gbcButton);
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveGame();
            }
        });

        viewPlayersButton = new JButton("View Players");
        gbcButton.gridy = 6;
        buttonPanel.add(viewPlayersButton, gbcButton);
        viewPlayersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPlayers();
            }
        });

        gbc.gridy = 0;
        sidePanel.add(buttonPanel, gbc);

        // Available Pieces Panel
        availablePiecesPanel = new JPanel();
        availablePiecesPanel.setLayout(new GridLayout(8, 4));
        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        sidePanel.add(availablePiecesPanel, gbc);

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel();
        chatInputPanel.setLayout(new BorderLayout());

        chatInput = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        sidePanel.add(chatPanel, gbc);

        add(sidePanel, BorderLayout.EAST);

        updateBoard();
        updateAvailablePieces();
    }

    private void sendMessage() {
        if (role.equals("spectator")) {
            JOptionPane.showMessageDialog(this, "Spectators cannot send messages.");
            return;
        }

        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            try {
                game.sendMessage(username, message);
                chatInput.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateAvailablePieces() {
        availablePiecesPanel.removeAll();

        for (int i = 0; i < availablePiecesButtons.length; i++) {
            availablePiecesButtons[i] = null;
            availablePieces[i] = null;
        }

        int index = 0;
        Piece[][] pieces = null;
        try {
            pieces = game.obtainBoard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Initialize white pieces off the board
        int[] whitePieces = new int[]{8, 2, 2, 2, 1, 1}; // Pawn, Knight, Bishop, Rook, Queen, King
        int[] blackPieces = new int[]{8, 2, 2, 2, 1, 1}; // Pawn, Knight, Bishop, Rook, Queen, King

        if (pieces != null) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (pieces[i][j] != null) {
                        if (pieces[i][j].isWhite()) {
                            whitePieces[pieces[i][j].getType()]--;
                        } else {
                            blackPieces[pieces[i][j].getType()]--;
                        }
                    }
                }
            }
        }

        for (int type = 0; type < whitePieces.length; type++) {
            for (int i = 0; i < whitePieces[type]; i++) {
                availablePieces[index] = new Piece(6, i, true, type);
                availablePiecesButtons[index] = createPieceButton(0, type, index);
                availablePiecesPanel.add(availablePiecesButtons[index]);
                index++;
            }
        }

        // Initialize black pieces off the board
        for (int type = 0; type < blackPieces.length; type++) {
            for (int i = 0; i < blackPieces[type]; i++) {
                availablePieces[index] = new Piece(1, i, false, type);
                availablePiecesButtons[index] = createPieceButton(1, type, index);
                availablePiecesPanel.add(availablePiecesButtons[index]);
                index++;
            }
        }

        availablePiecesPanel.revalidate();
        availablePiecesPanel.repaint();
    }

    private JButton createPieceButton(int color, int type, int index) {
        JButton button = new JButton(new ImageIcon(SquarePanel.pieceImage[color][type]));
        button.setPreferredSize(new Dimension(40, 40));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertPieceOnTheBoard(color, type);
            }
        });
        return button;
    }

    private void insertPieceOnTheBoard(int color, int type) {
        String position = JOptionPane.showInputDialog(this, "Enter the position (e.g., e4):");
        if (position != null && position.matches("[a-h][1-8]")) {
            int x = 8 - Character.getNumericValue(position.charAt(1));
            int y = position.charAt(0) - 'a';
            try {
                game.addPiece(color, type, x, y);
                updateBoard();
                updateAvailablePieces();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid position. Please try again.");
        }
    }

    private void switchToPlayer1() {
        try {
            role = game.switchToPlayer1(username);
            setTitle("Chess Game - " + username + " (" + role + ")");
            JOptionPane.showMessageDialog(this, "You are now " + role);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void switchToPlayer2() {
        try {
            role = game.switchToPlayer2(username);
            setTitle("Chess Game - " + username + " (" + role + ")");
            JOptionPane.showMessageDialog(this, "You are now " + role);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void switchToSpectator() {
        try {
            role = game.switchToSpectator(username);
            setTitle("Chess Game - " + username + " (" + role + ")");
            JOptionPane.showMessageDialog(this, "You are now " + role);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void leaveGame() {
        try {
            game.leave(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void displayPlayers() {
        try {
            String player1 = game.getPlayer1();
            String player2 = game.getPlayer2();
            List<String> spectators = game.getSpectators();

            StringBuilder sb = new StringBuilder();
            sb.append("Player 1: ").append(player1 != null ? player1 : "").append("\n");
            sb.append("Player 2: ").append(player2 != null ? player2 : "").append("\n");
            sb.append("Spectators:\n");
            for (String spectator : spectators) {
                sb.append(spectator).append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString(), "Players and Spectators", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(String message) {
        chatArea.append(message + "\n");
    }

    public void updateBoard() {
        try {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board[i][j].removePiece();
                }
            }

            Piece[][] pieces = game.obtainBoard();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (pieces[i][j] != null) {
                        board[i][j].setPiece(pieces[i][j].isWhite() ? 0 : 1, pieces[i][j].getType());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selected(int x, int y) {
        if (role.equals("spectator")) {
            System.out.println("Spectators cannot move pieces.");
            return;
        }

        System.out.printf("mouse pressed at: %d - %d\n", x, y);
        if (selectedPiece == null) {
            try {
                Piece[][] pieces = game.obtainBoard();
                if (pieces[x][y] != null) {
                    selectedPiece = pieces[x][y];
                    selectedX = x;
                    selectedY = y;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                board[selectedX][selectedY].removePiece();
                board[x][y].setPiece(selectedPiece.isWhite() ? 0 : 1, selectedPiece.getType());
                game.movePiece(username, selectedX, selectedY, x, y);
                updateAvailablePieces();
            } catch (Exception e) {
                e.printStackTrace();
            }
            selectedPiece = null;
        }
    }

    private void tidyPieces() {
        try {
            game.tidyPieces();
            updateBoard();
            updateAvailablePieces();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void removePieces() {
        try {
            game.removePieces();
            updateBoard();
            updateAvailablePieces();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
