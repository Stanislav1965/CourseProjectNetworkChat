import java.io.*;
import java.net.Socket;

public class Client {

    private static Socket clientSocket; //клиент сокет
    private static BufferedReader inputReader; // ридер читающий с консоли
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет
    private String nickname; // псевдоним клиента
    public static  Log clientLog = Log.getInstance("client.log");

    public static void main(String[] args) throws IOException {
        new Client();
    }


    public Client() throws IOException {

        Settings settings = new Settings(); // считываем настройки из файла
        try {
            clientSocket = new Socket(settings.getHost(), settings.getPort());
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            this.inputNickname(); // перед началом вводи псевдоним
            new ReadMsg().start(); // нить читающая сообщения из сокета в бесконечном цикле
            new WriteMsg().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле
        } catch (IOException e) {
            // Сокет должен быть закрыт при любой
            // ошибке, кроме ошибки конструктора сокета:
            Client.this.closeService();
        }

    }

    private void closeService() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                in.close();
                out.close();
                clientLog.close();
            }
        } catch (IOException | NullPointerException ignored) {
        }
    }

    private void inputNickname() {
        System.out.print("Введите псевдоним: ");
        try {
            nickname = inputReader.readLine();
            sendMessage(nickname + ": подключился! ");
            System.out.println("Добро пожаловать: " + nickname);
        } catch (IOException ignored) {
        }
    }

    // поток читающий сообщения с сервера
    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String msg;
            try {
                while (true) {
                    msg = in.readLine(); // ждем сообщения с сервера
                    if (msg.equals("exit")) {
                        Client.this.closeService(); // закрываем соединение
                        break; // выходим из цикла
                    }
                    System.out.println(msg); // пишем сообщение с сервера на консоль
                }
            } catch (IOException e) {
                Client.this.closeService();
            }
        }
    }

    // поток сообщений приходящих с консоли на сервер
    public class WriteMsg extends Thread {
        @Override
        public void run() {
            while (true) {
                String userMsg;
                try {
                    userMsg = inputReader.readLine(); // сообщения с консоли
                    if (userMsg.equals("exit")) {
                        sendMessage(nickname + ": вышел из чата.");
                        Client.this.closeService(); // при выходе из цикла закрываем поток
                        break; // выходим из цикла
                    } else {
                        sendMessage(nickname + ": " + userMsg); // отправляем сообщение на сервер
                    }
                } catch (IOException e) {
                    Client.this.closeService(); // в случае исключения закрываем поток
                }
            }

        }
    }

    private synchronized void sendMessage(String msg) throws IOException {
        out.write(msg + "\n");
        out.flush();
        clientLog.log("INFO", msg);
    }

}

