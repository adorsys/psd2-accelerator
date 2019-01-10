package de.adorsys.psd2.sandbox.xs2a;

import de.adorsys.psd2.sandbox.ContextHolder;
import de.adorsys.psd2.sandbox.xs2a.web.filter.MockCertificateFilter;
import de.adorsys.psd2.sandbox.xs2a.web.filter.TabDelimitedCertificateFilter;
import de.adorsys.psd2.xs2a.service.validator.tpp.TppInfoHolder;
import de.adorsys.psd2.xs2a.service.validator.tpp.TppRoleValidationService;
import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
    basePackages = {
        "de.adorsys.psd2.xs2a",
        "de.adorsys.psd2.aspsp",
        "de.adorsys.psd2.consent",
        "de.adorsys.psd2.sandbox.xs2a",
        "de.adorsys.psd2.sandbox.portal.testdata"
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
@Import(ContextHolder.class)
public class Xs2aConfig {

  @Bean
  QwacCertificateFilter sandboxCertificateFilter(
      TppRoleValidationService roleValidationService,
      TppInfoHolder tppInfoHolder,
      @Value("${certificate.filter}") String certificateFilterType
  ) {
    switch (certificateFilterType) {
      case "tab":
        return new TabDelimitedCertificateFilter(roleValidationService, tppInfoHolder);
      case "mock":
        return new MockCertificateFilter(roleValidationService, tppInfoHolder);
      default:
        return new QwacCertificateFilter(roleValidationService, tppInfoHolder);
    }
  }

  // Looks like swagger-ui can't be disabled via config
  @Controller
  public static class SwaggerUiDisabler {

    @RequestMapping("swagger-ui.html")
    public void nope() {
      // for proper default 404 error page instead of blank 404
      throw new ForcedNotFoundException();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class ForcedNotFoundException extends RuntimeException {

    }

  }
}
