package de.adorsys.psd2.sandbox.xs2a;

import de.adorsys.psd2.sandbox.xs2a.web.filter.MockCertificateFilter;
import de.adorsys.psd2.sandbox.xs2a.web.filter.TabDelimitedCertificateFilter;
import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = {
        "de.adorsys.psd2.xs2a",
        "de.adorsys.psd2.aspsp",
        "de.adorsys.psd2.consent",
        "de.adorsys.psd2.sandbox.xs2a"
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = QwacCertificateFilter.class
    )
)
@PropertySource(
    value = {
        "classpath:xs2a-application.properties",
        "classpath:xs2a-application-${spring.profiles.active}.properties"
    },
    ignoreResourceNotFound = true
)
public class Xs2aConfig {

  @Configuration
  public static class CertificateConfig {

    @Value("${certificate.filter}")
    private String certFilter;

    @Bean
    QwacCertificateFilter setCertificateFilter() {
      switch (certFilter) {
        case "tab":
          return new TabDelimitedCertificateFilter();
        case "mock":
          return new MockCertificateFilter();
        default:
          return new QwacCertificateFilter();
      }
    }
  }
}
