package de.adorsys.psd2.sandbox;

import de.adorsys.aspsp.xs2a.config.SwaggerConfig;
import de.adorsys.psd2.sandbox.certificateserver.service.CertificateService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;

@ComponentScan(
    basePackages = {"de.adorsys.psd2.sandbox", "de.adorsys.aspsp.xs2a", "de.adorsys.psd2"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class)
)
@SpringBootApplication
public class SandboxApplication {

  private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

  // CHECKSTYLE:OFF
  public static void main(String[] args) throws UnknownHostException {
    // CHECKSTYLE:ON
    SpringApplication app = new SpringApplication(SandboxApplication.class);

    Environment env = app.run(args).getEnvironment();
    String protocol = "http";
    if (env.getProperty("server.ssl.key-store") != null) {
      protocol = "https";
    }
    log.info("\n----------------------------------------------------------\n\t"
            + "Application '{}' is running! Access URLs:\n\t"
            + "Local: \t\t{}://localhost:{}\n\t"
            + "External: \t{}://{}:{}\n\t"
            + "Profile(s): \t{}\n----------------------------------------------------------",
        env.getProperty("spring.application.name"),
        protocol,
        env.getProperty("server.port"),
        protocol,
        InetAddress.getLocalHost().getHostAddress(),
        env.getProperty("server.port"),
        env.getActiveProfiles());
  }
}

