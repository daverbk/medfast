package com.ventionteams.medfast;

import com.ventionteams.medfast.config.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class MedfastApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedfastApplication.class, args);
    }

}
