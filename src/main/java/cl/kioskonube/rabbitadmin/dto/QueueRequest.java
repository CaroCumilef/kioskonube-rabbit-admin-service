package cl.kioskonube.rabbitadmin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QueueRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE)
    private String name;

    private boolean durable = true;
    private boolean autoDelete = false;

    /** Si es true, crea también <name>.dlx, <name>.dlq y su binding. */
    private boolean withDeadLetter = false;

    @Min(value = 1, message = "ttlMs debe ser mayor a 0")
    private Integer ttlMs;

    @Min(value = 1, message = "maxLength debe ser mayor a 0")
    private Integer maxLength;
}
