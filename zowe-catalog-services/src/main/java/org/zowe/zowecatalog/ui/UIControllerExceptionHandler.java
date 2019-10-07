package org.zowe.zowecatalog.ui;

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
import org.zowe.zowecatalog.catalog.service.CatalogNotFoundException;

/**
 * Creates responses for exceptional behavior of the {@link UIController}.
 */
@ControllerAdvice(assignableTypes = { UIController.class })
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class UIControllerExceptionHandler {

    private final ErrorService errorService;

    @ExceptionHandler(CatalogNotFoundException.class)
    public ResponseEntity<ApiMessage> handleEmptyName(CatalogNotFoundException exception) {
        ApiMessage message = errorService.createApiMessage("org.zowe.commons.rest.notFound");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON_UTF8).body(message);
    }
}
