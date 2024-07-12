import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Settings {
    private int port;
    private String host;

    // Считаем настройкипорта из файла настроек
    public Settings() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("settings.txt"));
            // читаем строку
            String str = reader.readLine();
            while (str != null) {
                if (str.contains("port")) {
                    String[] words = str.split(" ");
                    this.port = Integer.parseInt(words[2]);
                }
                if (str.contains("host")) {
                    String[] words = str.split(" ");
                    this.host = words[2];
                }
                str = reader.readLine();
            }
            reader.close();
        } catch (
                IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    // Номер порта
    public int getPort() {
        return port;
    }

    // Наименование сервера или ip адрес
    public String getHost() {
        return host;
    }
}
