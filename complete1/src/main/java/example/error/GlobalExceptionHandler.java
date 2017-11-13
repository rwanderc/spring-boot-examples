package example.error;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Error.
     *
     * @param status the status
     * @param message the message
     * @return the response entity
     */
    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), message);
        return new ResponseEntity<>(error, status);
    }

    /**
     * Gets the constraint violation message.
     *
     * @param violation the violation
     * @return the constraint violation message
     */
    private String getConstraintViolationMessage(ConstraintViolation<?> violation) {
        Path path = violation.getPropertyPath();
        if (path != null) {
            String[] fields = path.toString().split("\\.");
            return fields[fields.length - 1].concat(" ").concat(violation.getMessage());
        } else {
            return violation.getMessage();
        }
    }

    /**
     * Gets the validation error message.
     *
     * @param errors the errors
     * @return the validation error message
     */
    private String getValidationErrorMessage(List<ObjectError> errors) {
        return !CollectionUtils.isEmpty(errors)
                ? errors.stream().map(error -> getValidationErrorMessage(error))
                        .reduce((error1, error2) -> error1.concat("; ").concat(error2)).get()
                : "";
    }

    /**
     * Gets the validation error message.
     *
     * @param error the error
     * @return the validation error message
     */
    private String getValidationErrorMessage(ObjectError error) {
        return error instanceof FieldError
                ? ((FieldError) error).getField().concat(" ").concat(error.getDefaultMessage())
                : error.getObjectName().concat(" ").concat(error.getDefaultMessage());
    }

    /**
     * Access denied.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(HttpServletRequest request, AccessDeniedException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Illegal argument.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler({IllegalArgumentException.class, MissingServletRequestPartException.class})
    public ResponseEntity<?> illegalArgument(HttpServletRequest request, Exception ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Illegal state.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> illegalState(HttpServletRequest request, IllegalStateException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Servlet request binding.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<?> servletRequestBinding(HttpServletRequest request, ServletRequestBindingException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Constraint violation.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolation(HttpServletRequest request, ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(this::getConstraintViolationMessage)
                .reduce((error1, error2) -> error1.concat("; ").concat(error2))
                .get();
        return error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Method argument not valid.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        return error(HttpStatus.BAD_REQUEST, getValidationErrorMessage(ex.getBindingResult().getAllErrors()));
    }

    /**
     * HTTP Client Error.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> httpClientError(HttpServletRequest request, HttpClientErrorException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Authentication service.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<?> authenticationService(HttpServletRequest request, AuthenticationServiceException ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * Null pointer.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> nullPointer(HttpServletRequest request, NullPointerException ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * Empty Result Data.
     *
     * @param request the request
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> emptyResultData(HttpServletRequest request, EmptyResultDataAccessException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles data integrity violation from database.
     *
     * @param ex the exception thrown
     * @return the response entity with proper message
     */
    @ResponseBody
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrityViolation(DataIntegrityViolationException ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    /**
     * Handles wrong values for parameters such as Enums.
     *
     * @param ex the exception thrown
     * @return the response entity with proper message
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> typeMismatch(MethodArgumentTypeMismatchException ex) {
        return error(HttpStatus.BAD_REQUEST, String.format("Wrong input for parameter '%s'", ex.getName()));
    }

}
