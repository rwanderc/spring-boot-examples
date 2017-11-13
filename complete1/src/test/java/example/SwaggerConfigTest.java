package example;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfigTest {

    @Test
    public void shouldCreateSwaggerDocket() {
        assertNotNull(new SwaggerConfig().api());
    }

}
