package cl.kioskonube.rabbitadmin.dto;

import java.util.List;

public record ApiError(String timestamp, int status, String message, List<String> details) {
}
