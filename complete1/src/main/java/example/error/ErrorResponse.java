package example.error;

import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final Error error;

    public ErrorResponse(int status, String phrase, String message) {
        this.error = new Error(new Date(), status, phrase, message);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Error {

        private final Date timestamp;
        private final int status;
        private final String error;
        private final String message;

    }

}
