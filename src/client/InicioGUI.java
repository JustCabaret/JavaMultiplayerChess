package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import server.ChessGame;
import server.nameInUseException;

public class InicioGUI extends JFrame {
    private JTextField txtHostname;
    private JTextField txtPort;
    private JTextField txtUsername;
    private JButton btnSubmit;

    public InicioGUI() {
        setTitle("Initial Configuration");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JLabel lblHostname = new JLabel("Hostname/IP:");
        txtHostname = new JTextField("localhost");
        JLabel lblPort = new JLabel("Port:");
        txtPort = new JTextField("1099");
        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField();
        btnSubmit = new JButton("Join");

        // Add components to the frame
        add(lblHostname);
        add(txtHostname);
        add(lblPort);
        add(txtPort);
        add(lblUsername);
        add(txtUsername);
        add(new JLabel()); // Placeholder for layout spacing
        add(btnSubmit);

        // Action listener for the submit button
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGame();
            }
        });

        setVisible(true);
    }

    private void joinGame() {
        String hostname = txtHostname.getText();
        String port = txtPort.getText();
        String username = txtUsername.getText();

        try {
            // Construct the RMI URL
            String url = "rmi://" + hostname + ":" + port + "/ChessGame";
            ChessGame game = (ChessGame) Naming.lookup(url);
            try {
                // Register the player and start the game GUI
                String role = game.registerPlayer(username);
                ChessGUI mainFrame = new ChessGUI(game, username, role);
                mainFrame.setVisible(true);
                this.dispose();
            } catch (nameInUseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InicioGUI::new);
    }
}
