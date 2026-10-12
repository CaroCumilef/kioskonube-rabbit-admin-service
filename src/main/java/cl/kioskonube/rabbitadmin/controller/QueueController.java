package cl.kioskonube.rabbitadmin.controller;

import cl.kioskonube.rabbitadmin.dto.QueueInfoResponse;
import cl.kioskonube.rabbitadmin.dto.QueueRequest;
import cl.kioskonube.rabbitadmin.dto.ValidationRules;
import cl.kioskonube.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/queues")
@RequiredArgsConstructor
public class QueueController {

    private final RabbitAdminService service;

    @PostMapping
    public ResponseEntity<QueueInfoResponse> create(@Valid @RequestBody QueueRequest request) {
        QueueInfoResponse created = service.createQueue(request);
        return ResponseEntity.created(URI.create("/queues/" + created.name())).body(created);
    }

    @GetMapping("/{name}")
    public QueueInfoResponse get(
            @PathVariable @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE) String name) {
        return service.getQueue(name);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(
            @PathVariable @Pattern(regexp = ValidationRules.NAME_REGEX, message = ValidationRules.NAME_MESSAGE) String name) {
        service.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }
}
