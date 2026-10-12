package cl.kioskonube.rabbitadmin.controller;

import cl.kioskonube.rabbitadmin.dto.BindingRequest;
import cl.kioskonube.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bindings")
@RequiredArgsConstructor
public class BindingController {

    private final RabbitAdminService service;

    @PostMapping
    public ResponseEntity<BindingRequest> create(@Valid @RequestBody BindingRequest request) {
        service.createBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@Valid @RequestBody BindingRequest request) {
        service.deleteBinding(request);
        return ResponseEntity.noContent().build();
    }
}
