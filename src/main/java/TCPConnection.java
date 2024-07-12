import java.io.*;
import java.net.Socket;

public class TCPConnection extends Thread {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final Log serverLog;


    public TCPConnection(Socket socket, Log serverLog) throws IOException {
        this.socket = socket;
        this.serverLog = serverLog;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = in.readLine();
                if (msg.equals("exit")) {
                    this.disconnect();
                    break;
                }
                System.out.println(msg);
                serverLog.log("INFO", msg);
                sendToAllConnections(msg);
            }
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            this.disconnect();
        }
    }

    // рассылка сообщения
    public synchronized void sendMessage(String msg) {
        try {
            serverLog.log("INFO", msg);
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }
    // рассылаем сообщения всем кто подключен, кроме себя
    private synchronized void sendToAllConnections(String string) {
        for (TCPConnection conn : Server.tcpConnectionList) {
            if (!conn.equals(this)) {
                conn.sendMessage(string);
            }
        }

    }

    public void disconnect() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (TCPConnection conn : Server.tcpConnectionList) {
                    if (conn.equals(this)) conn.interrupt();
                    Server.tcpConnectionList.remove(this);
                }
            }
        } catch (IOException ignored) {
        }
    }
}
