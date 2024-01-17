package com.dustincode.ecommerce;

import com.dustincode.ecommerce.core.configs.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class SimpleEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleEcommerceApplication.class, args);
    }

}
