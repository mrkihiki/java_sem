package org.example.java_sem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class ServerPort {

    @Value("${server.port}")
    private int serverPort;


    public int getPort() {
        return serverPort;
    }
}
