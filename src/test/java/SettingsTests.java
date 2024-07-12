import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SettingsTests {
    private Settings settings;

    @BeforeEach
    void setUp() {
        settings = new Settings();
    }

    @Test
    public void testGetPort() {
        int expected = 8086;
        int actual = settings.getPort();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void testGetHost() {
        String expected = "127.0.0.1";
        String actual = settings.getHost();
        Assertions.assertEquals(actual,expected);
    }

}
