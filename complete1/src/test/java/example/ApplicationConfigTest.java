package example;

import static org.junit.Assert.assertNotNull;

import lombok.SneakyThrows;
import org.junit.Test;

public class ApplicationConfigTest {

    private ApplicationConfig config() {
        return new ApplicationConfig();
    }

    @Test
    @SneakyThrows
    public void shouldCreateRestTemplate() {
        assertNotNull(config().restTemplate());
    }

}
