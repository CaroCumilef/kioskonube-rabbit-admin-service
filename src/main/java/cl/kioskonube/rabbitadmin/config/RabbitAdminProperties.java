package cl.kioskonube.rabbitadmin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kioskonube.rabbit-admin")
public class RabbitAdminProperties {
    private String dlxSuffix = ".dlx";
    private String dlqSuffix = ".dlq";
}
