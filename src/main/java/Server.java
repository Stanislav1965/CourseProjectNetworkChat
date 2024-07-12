import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) throws IOException {
        new Server();
    }

    public static  List<TCPConnection> tcpConnectionList = new ArrayList<>(); // список подключений
    public static  Log serverLog = Log.getInstance("server.log");

    private Server() throws IOException {
        Settings settings = new Settings();
        try (ServerSocket serverSocket = new ServerSocket(settings.getPort())) {
            System.out.println("Сервер запущен ... ");
            serverLog.log("INFO", "Сервер запущен");
            while (true) {
                // accept() будет ждать пока кто-нибудь подключиться
                Socket clientSocket = serverSocket.accept();
                // установив связь и воссоздав сокет для общения с клиентом можно перейти
                try {
                    tcpConnectionList.add(new TCPConnection(clientSocket, serverLog));
                } catch (IOException e) {
                    serverLog.log("ERROR", e.getMessage());
                    clientSocket.close();
                }
            }
        } finally {
            serverLog.log("INFO", "Сервер остановлен");
            System.out.println("Сервер остановлен ... ");
        }
    }
}