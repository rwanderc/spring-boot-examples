package example.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ErrorResponseTest {

    private static final int DUMMY_STATUS = 400;
    private static final String DUMMY_ERROR = "dummy-error";
    private static final String DUMMY_MESSAGE = "dummy-message";

    @Test
    public void shouldGetError() {
        ErrorResponse response = new ErrorResponse(DUMMY_STATUS, DUMMY_ERROR, DUMMY_MESSAGE);
        assertNotNull(response.getError().getTimestamp());
        assertEquals(DUMMY_STATUS, response.getError().getStatus());
        assertEquals(DUMMY_ERROR, response.getError().getError());
        assertEquals(DUMMY_MESSAGE, response.getError().getMessage());
    }

}
