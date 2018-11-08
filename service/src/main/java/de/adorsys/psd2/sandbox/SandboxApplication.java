package de.adorsys.psd2.sandbox;

import de.adorsys.aspsp.xs2a.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ComponentScan(
    basePackages = {"de.adorsys.psd2.sandbox", "de.adorsys.aspsp.xs2a", "de.adorsys.psd2"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class)
)
@SpringBootApplication
public class SandboxApplication {

  // CHECKSTYLE:OFF
  public static void main(String[] args) {
    // CHECKSTYLE:ON
    SpringApplication.run(SandboxApplication.class, args);
  }
}

