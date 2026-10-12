package cl.kioskonube.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ExchangeRequest {

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE)
    private String name;

    @NotNull(message = "El tipo es obligatorio (DIRECT, TOPIC, FANOUT o HEADERS)")
    private ExchangeKind type;

    private boolean durable = true;
    private boolean autoDelete = false;
}
