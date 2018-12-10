package de.adorsys.psd2.sandbox;

import de.adorsys.psd2.sandbox.xs2a.Xs2aConfig;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
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
@PropertySource(
    value = {
        "classpath:sandbox-application.properties",
        "classpath:sandbox-application-${spring.profiles.active}.properties"
    },
    ignoreResourceNotFound = true
)
@EnableSwagger2
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "de\\.adorsys\\.psd2\\.sandbox\\.xs2a\\.(.*)"
    ))
public class SandboxApplication {

  /**
   * Starts our spring boot app.
   *
   * @param args CLI args
   */
  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .parent(EmptyConfiguration.class).web(false)
        .child(SandboxApplication.class).web(true)
        .sibling(Xs2aConfig.class).web(true)
        .run(args);
  }

  /*
   * Looks like we can't have two unrelated contexts, so we need an empty parent
   */
  @Configuration
  static class EmptyConfiguration {

  }

}
