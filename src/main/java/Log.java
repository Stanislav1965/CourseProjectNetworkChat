import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Log {
    private static Log INSTANCE = null;
    private static FileWriter fileWriter = null;

    private Map<String, Integer> freqs = new HashMap<>();

    private Log(String fileName) {
        createFile(fileName);
        try {
            fileWriter = new FileWriter(fileName, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Log getInstance(String fileName){
        if (INSTANCE == null) {
            synchronized (Log.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Log(fileName);
                }
            }
        }
        return INSTANCE;
    }

    public void log(String level, String msg) throws IOException {
        freqs.put(level, freqs.getOrDefault(level, 0) + 1);
        fileWriter.write("[" + level + "#" + freqs.get(level) + "]" +
                LocalDateTime.now() + " === " + msg + "\n");
        fileWriter.flush();
    }

    public void close() throws IOException {
        fileWriter.close();
    }


    private static void createFile(String name) {
        File file = new File(name);
        if (!(file.exists() || file.isFile())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}