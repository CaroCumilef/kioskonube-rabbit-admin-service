package cl.kioskonube.rabbitadmin.controller;

import cl.kioskonube.rabbitadmin.dto.ExchangeRequest;
import cl.kioskonube.rabbitadmin.dto.ValidationRules;
import cl.kioskonube.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final RabbitAdminService service;

    @PostMapping
    public ResponseEntity<ExchangeRequest> create(@Valid @RequestBody ExchangeRequest request) {
        service.createExchange(request);
        return ResponseEntity.created(URI.create("/exchanges/" + request.getName())).body(request);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(
            @PathVariable @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE) String name) {
        service.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }
}
