package com.ventionteams.medfast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.ventionteams.medfast.config.properties")
public class MedfastApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedfastApplication.class, args);
    }

}
