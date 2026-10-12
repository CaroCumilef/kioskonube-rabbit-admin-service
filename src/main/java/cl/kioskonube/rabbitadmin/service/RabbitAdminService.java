package cl.kioskonube.rabbitadmin.service;

import cl.kioskonube.rabbitadmin.config.RabbitAdminProperties;
import cl.kioskonube.rabbitadmin.dto.BindingRequest;
import cl.kioskonube.rabbitadmin.dto.ExchangeRequest;
import cl.kioskonube.rabbitadmin.dto.QueueInfoResponse;
import cl.kioskonube.rabbitadmin.dto.QueueRequest;
import cl.kioskonube.rabbitadmin.exception.ResourceConflictException;
import cl.kioskonube.rabbitadmin.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;
    private final RabbitAdminProperties props;

    // ---------- Colas ----------

    public QueueInfoResponse createQueue(QueueRequest req) {
        if (amqpAdmin.getQueueInfo(req.getName()) != null) {
            throw new ResourceConflictException("La cola '" + req.getName() + "' ya existe");
        }

        QueueBuilder builder = req.isDurable()
                ? QueueBuilder.durable(req.getName())
                : QueueBuilder.nonDurable(req.getName());

        if (req.isAutoDelete()) {
            builder.autoDelete();
        }
        if (req.getTtlMs() != null) {
            builder.ttl(req.getTtlMs());
        }
        if (req.getMaxLength() != null) {
            builder.maxLength(req.getMaxLength());
        }

        if (req.isWithDeadLetter()) {
            String dlxName = req.getName() + props.getDlxSuffix();
            String dlqName = req.getName() + props.getDlqSuffix();

            DirectExchange dlx = new DirectExchange(dlxName, true, false);
            Queue dlq = QueueBuilder.durable(dlqName).build();

            amqpAdmin.declareExchange(dlx);
            amqpAdmin.declareQueue(dlq);
            amqpAdmin.declareBinding(BindingBuilder.bind(dlq).to(dlx).with(req.getName()));

            builder.deadLetterExchange(dlxName).deadLetterRoutingKey(req.getName());
            log.info("DLX '{}' y DLQ '{}' declarados para la cola '{}'", dlxName, dlqName, req.getName());
        }

        amqpAdmin.declareQueue(builder.build());
        log.info("Cola '{}' creada", req.getName());
        return getQueue(req.getName());
    }

    public QueueInfoResponse getQueue(String name) {
        QueueInformation info = amqpAdmin.getQueueInfo(name);
        if (info == null) {
            throw new ResourceNotFoundException("La cola '" + name + "' no existe");
        }
        return new QueueInfoResponse(info.getName(), info.getMessageCount(), info.getConsumerCount());
    }

    public void deleteQueue(String name) {
        requireQueue(name);
        amqpAdmin.deleteQueue(name);
        log.info("Cola '{}' eliminada", name);
    }

    // ---------- Exchanges ----------

    public void createExchange(ExchangeRequest req) {
        ExchangeBuilder builder = switch (req.getType()) {
            case DIRECT -> ExchangeBuilder.directExchange(req.getName());
            case TOPIC -> ExchangeBuilder.topicExchange(req.getName());
            case FANOUT -> ExchangeBuilder.fanoutExchange(req.getName());
            case HEADERS -> ExchangeBuilder.headersExchange(req.getName());
        };
        builder.durable(req.isDurable());
        if (req.isAutoDelete()) {
            builder.autoDelete();
        }
        amqpAdmin.declareExchange(builder.build());
        log.info("Exchange '{}' ({}) creado", req.getName(), req.getType());
    }

    public void deleteExchange(String name) {
        // RabbitMQ trata el borrado de un exchange inexistente como éxito (idempotente)
        amqpAdmin.deleteExchange(name);
        log.info("Exchange '{}' eliminado", name);
    }

    // ---------- Bindings ----------

    public void createBinding(BindingRequest req) {
        requireQueue(req.getQueue());
        amqpAdmin.declareBinding(toBinding(req));
        log.info("Binding creado: {} -> {} ({})", req.getExchange(), req.getQueue(), req.getRoutingKey());
    }

    public void deleteBinding(BindingRequest req) {
        requireQueue(req.getQueue());
        amqpAdmin.removeBinding(toBinding(req));
        log.info("Binding eliminado: {} -> {} ({})", req.getExchange(), req.getQueue(), req.getRoutingKey());
    }

    // ---------- Auxiliares ----------

    private void requireQueue(String name) {
        if (amqpAdmin.getQueueInfo(name) == null) {
            throw new ResourceNotFoundException("La cola '" + name + "' no existe");
        }
    }

    private Binding toBinding(BindingRequest req) {
        return new Binding(req.getQueue(), Binding.DestinationType.QUEUE,
                req.getExchange(), req.getRoutingKey(), null);
    }
}
