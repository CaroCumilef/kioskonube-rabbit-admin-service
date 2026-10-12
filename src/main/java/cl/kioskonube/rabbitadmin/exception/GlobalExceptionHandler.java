package cl.kioskonube.rabbitadmin.exception;

import cl.kioskonube.rabbitadmin.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBody(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleParams(HandlerMethodValidationException ex) {
        List<String> details = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Parámetros inválidos", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "Cuerpo de la petición ilegible o con valores no permitidos (por ejemplo, un tipo de exchange inválido)",
                List.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ResourceConflictException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    @ExceptionHandler(AmqpConnectException.class)
    public ResponseEntity<ApiError> handleConnect(AmqpConnectException ex) {
        log.error("No se pudo conectar con RabbitMQ", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, "RabbitMQ no está disponible", List.of());
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<ApiError> handleAmqp(AmqpException ex) {
        log.error("RabbitMQ rechazó la operación", ex);
        return build(HttpStatus.BAD_GATEWAY,
                "RabbitMQ rechazó la operación (revisa que los recursos existan y que la configuración sea compatible)",
                List.of());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, List<String> details) {
        ApiError body = new ApiError(Instant.now().toString(), status.value(), message, details);
        return ResponseEntity.status(status).body(body);
    }
}
