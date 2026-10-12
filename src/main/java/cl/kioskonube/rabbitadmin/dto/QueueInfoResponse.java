package cl.kioskonube.rabbitadmin.dto;

public record QueueInfoResponse(String name, long messageCount, int consumerCount) {
}