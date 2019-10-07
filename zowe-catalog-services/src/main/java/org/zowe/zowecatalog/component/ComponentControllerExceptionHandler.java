package org.zowe.zowecatalog.component;

import com.ca.mfaas.error.ErrorService;
import com.ca.mfaas.rest.response.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Creates responses for exceptional behavior of the {@link ComponentController}.
 */
@ControllerAdvice(assignableTypes = { ComponentController.class })
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ComponentControllerExceptionHandler {

    private final ErrorService errorService;

    @ExceptionHandler(ComponentNotFoundException.class)
    public ResponseEntity<ApiMessage> handleEmptyName(ComponentNotFoundException exception) {
        ApiMessage message = errorService.createApiMessage("org.zowe.commons.rest.notFound");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON_UTF8).body(message);
    }
}
