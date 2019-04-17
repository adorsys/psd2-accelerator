package de.adorsys.psd2.sandbox.portal;

import de.adorsys.psd2.sandbox.ContextHolder;
import de.adorsys.psd2.sandbox.certificate.EnableCertificateGeneratorService;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCertificateGeneratorService
@SpringBootApplication(exclude = {
    // TODO no persistence for now
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    // TODO disable security for now
    SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
@Import(ContextHolder.class)
@EnableSwagger2
public class PortalConfig {

}
