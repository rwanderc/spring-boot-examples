package example.error;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler() {
        return new GlobalExceptionHandler();
    }

    private void assertStatus(HttpStatus status, ResponseEntity<?> response) {
        assertEquals(status, response.getStatusCode());
    }

    @Test
    public void shouldTreatAuthenticationService() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.INTERNAL_SERVER_ERROR, handler().authenticationService(request,
                new AuthenticationServiceException("mocked error")));
    }

    @Test
    public void shouldTreatAccessDenied() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.UNAUTHORIZED,
                handler().accessDenied(request, new AccessDeniedException("mocked error")));
    }

    @Test
    public void shouldTreatIllegalArgument() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.BAD_REQUEST,
                handler().illegalArgument(request, new IllegalArgumentException("mocked error")));
    }

    @Test
    public void shouldTreatIllegalState() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.BAD_REQUEST,
                handler().illegalState(request, new IllegalStateException("mocked error")));
    }

    @Test
    public void shouldTreatMethodArgumentNotValid() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors())
                .thenReturn(Arrays.asList(new ObjectError("objName", "defaultMsg"),
                        new FieldError("objname", "fieldName", "defaultMsg")));
        MethodParameter param = mock(MethodParameter.class);
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.BAD_REQUEST, handler().methodArgumentNotValid(request,
                new MethodArgumentNotValidException(param, bindingResult)));
    }

    @Test
    public void shouldTreatNullPointer() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.INTERNAL_SERVER_ERROR,
                handler().nullPointer(request, new NullPointerException("mocked error")));
    }

    @Test
    public void shouldTreatServletRequestBinding() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.BAD_REQUEST, handler().servletRequestBinding(request,
                new ServletRequestBindingException("mocked error")));
    }

    @Test
    public void shouldTreatHttpClientError() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.NOT_FOUND, handler().httpClientError(request,
                new HttpClientErrorException(HttpStatus.NOT_FOUND, "mocked error")));
    }

    @Test
    public void shouldEmptyResultData() {
        when(request.getHeader(eq("Accept"))).thenReturn("application/json");
        assertStatus(HttpStatus.NOT_FOUND, handler().emptyResultData(request,
                new EmptyResultDataAccessException(1)));
    }

    @Test
    public void shouldTreatMethodArgumentTypeMismatchException() {
        MethodArgumentTypeMismatchException mockedException = mock(MethodArgumentTypeMismatchException.class);
        when(mockedException.getName()).thenReturn("dummy-name");

        ResponseEntity<ErrorResponse> response = handler().typeMismatch(mockedException);
        assertStatus(HttpStatus.BAD_REQUEST, response);
        assertEquals("Wrong input for parameter 'dummy-name'", response.getBody().getError().getMessage());
    }

}
