package cl.kioskonube.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BindingRequest {

    @NotBlank(message = "La cola es obligatoria")
    @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE)
    private String queue;

    @NotBlank(message = "El exchange es obligatorio")
    @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE)
    private String exchange;

    // Puede ser vacío (por ejemplo en exchanges fanout), pero no nulo
    @NotNull(message = "routingKey es obligatorio (puede ser vacío)")
    @Size(max = 255, message = "routingKey no puede superar 255 caracteres")
    private String routingKey;
}
