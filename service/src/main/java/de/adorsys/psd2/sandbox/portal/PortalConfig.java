package de.adorsys.psd2.sandbox.portal;

import de.adorsys.psd2.sandbox.ContextHolder;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {
    // TODO no persistence for now
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    // TODO disable security for now
    SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
@Import(ContextHolder.class)
@PropertySource(
    value = {
        "classpath:portal-application.properties",
        "classpath:portal-application-${spring.profiles.active}.properties"
    },
    ignoreResourceNotFound = true
)
@EnableSwagger2
public class PortalConfig {

}
