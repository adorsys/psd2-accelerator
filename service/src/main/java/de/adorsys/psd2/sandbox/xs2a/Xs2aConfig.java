package de.adorsys.psd2.sandbox.xs2a;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
    "de.adorsys.psd2.xs2a",
    "de.adorsys.psd2.aspsp",
    "de.adorsys.psd2.consent",
    "de.adorsys.psd2.sandbox.xs2a"
})
@PropertySource(
    value = {
        "classpath:xs2a-application.properties",
        "classpath:xs2a-application-${spring.profiles.active}.properties"
    },
    ignoreResourceNotFound = true
)
public class Xs2aConfig {

}
