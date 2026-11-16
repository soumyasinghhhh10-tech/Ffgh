import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;

// First screen: pick a name, then either host a game or join one.
public class MenuFrame extends JFrame {

    private final JTextField nameField = new JTextField("Player");
    private final JTextField ipField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("5000");
    private final JButton hostButton = new JButton("Host Game");
    private final JButton joinButton = new JButton("Join Game");

    public MenuFrame() {
        setTitle("NeonSnake LAN");
        setIconImage(AppIcon.create(64));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        root.setBackground(new Color(15, 23, 42));

        // logo + title
        JLabel title = new JLabel("NeonSnake LAN", new ImageIcon(AppIcon.create(72)), SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        root.add(title, BorderLayout.NORTH);

        // form
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 10));
        form.setOpaque(false);
        form.add(label("Your name"));
        form.add(nameField);
        form.add(label("Host IP (to join)"));
        form.add(ipField);
        form.add(label("Port"));
        form.add(portField);
        root.add(form, BorderLayout.CENTER);

        // buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(hostButton);
        buttons.add(joinButton);
        root.add(buttons, BorderLayout.SOUTH);

        hostButton.addActionListener(e -> hostGame());
        joinButton.addActionListener(e -> joinGame());

        setContentPane(root);
        pack();
        setSize(430, getHeight());
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(203, 213, 225));
        return l;
    }

    // removes characters that would break our message format
    private String cleanName() {
        String name = nameField.getText().replaceAll("[|;,]", "").trim();
        if (name.isEmpty()) {
            name = "Player";
        }
        return name.length() > 12 ? name.substring(0, 12) : name;
    }

    private int readPort() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            if (port >= 1024 && port <= 65535) {
                return port;
            }
        } catch (NumberFormatException e) {
            // falls through to the error below
        }
        JOptionPane.showMessageDialog(this, "Port must be a number between 1024 and 65535.",
                "Invalid port", JOptionPane.WARNING_MESSAGE);
        return -1;
    }

    private void hostGame() {
        int port = readPort();
        if (port == -1) {
            return;
        }
        try {
            ServerSocket server = new ServerSocket(port);
            setVisible(false);
            new GameWindow(this, cleanName(), server);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not start the server on port " + port + ".\nIt may already be in use.\n(" + e.getMessage() + ")",
                    "Host error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void joinGame() {
        final String ip = ipField.getText().trim();
        final int port = readPort();
        final String name = cleanName();
        if (port == -1) {
            return;
        }
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type the host's IP address.",
                    "Missing IP", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // connecting can take a few seconds, so do it in a thread to keep the window alive
        joinButton.setEnabled(false);
        joinButton.setText("Connecting...");
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), 4000);   // 4 second timeout
                SwingUtilities.invokeLater(() -> {
                    resetJoinButton();
                    setVisible(false);
                    new GameWindow(this, name, socket);
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    resetJoinButton();
                    JOptionPane.showMessageDialog(this,
                            "Could not connect to " + ip + ":" + port + ".\n"
                            + "Check the IP, the port, and that the host already started the game.\n"
                            + "Both PCs must be on the same network.",
                            "Connection failed", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void resetJoinButton() {
        joinButton.setEnabled(true);
        joinButton.setText("Join Game");
    }
}
