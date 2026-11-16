import java.io.*;
import java.net.Socket;

// Wraps a Socket so both host and client can use the same code.
// A separate thread keeps reading lines so the GUI never freezes.
public class Connection {

    public interface Listener {
        void onMessage(String line);
        void onDisconnect();
    }

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Listener listener;
    private volatile boolean open = true;

    public Connection(Socket socket, Listener listener) throws IOException {
        this.socket = socket;
        this.listener = listener;
        socket.setTcpNoDelay(true);   // send small messages immediately, less lag
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
    }

    public void start() {
        Thread reader = new Thread(this::readLoop);
        reader.setDaemon(true);
        reader.start();
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                listener.onMessage(line);
            }
        } catch (IOException e) {
            // socket got closed or the network dropped, nothing to do here
        } finally {
            if (open) {
                open = false;
                listener.onDisconnect();
            }
        }
    }

    public synchronized void send(String message) {
        if (open) {
            out.println(message);
        }
    }

    // closing on purpose does NOT call onDisconnect
    public void close() {
        open = false;
        try {
            socket.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
