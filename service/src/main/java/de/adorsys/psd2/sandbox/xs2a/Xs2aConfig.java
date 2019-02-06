package de.adorsys.psd2.sandbox.xs2a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import de.adorsys.psd2.consent.domain.InstanceDependableEntity;
import de.adorsys.psd2.consent.domain.PsuData;
import de.adorsys.psd2.consent.repository.PsuDataRepository;
import de.adorsys.psd2.sandbox.ContextHolder;
import de.adorsys.psd2.sandbox.xs2a.web.filter.MockCertificateFilter;
import de.adorsys.psd2.sandbox.xs2a.web.filter.TabDelimitedCertificateFilter;
import de.adorsys.psd2.xs2a.config.ObjectMapperConfig;
import de.adorsys.psd2.xs2a.service.validator.tpp.TppInfoHolder;
import de.adorsys.psd2.xs2a.service.validator.tpp.TppRoleValidationService;
import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = PsuDataRepository.class)
@EntityScan(basePackageClasses = PsuData.class)
@EnableAutoConfiguration
@ComponentScan(
    basePackages = {
        "de.adorsys.psd2.xs2a",
        "de.adorsys.psd2.aspsp",
        "de.adorsys.psd2.consent",
        "de.adorsys.psd2.sandbox.xs2a",
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.CUSTOM, classes = Xs2aConfig.Xs2aComponentFilter.class
    )
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

  public static class Xs2aComponentFilter implements TypeFilter {

    private static Logger log = LoggerFactory.getLogger(Xs2aComponentFilter.class);

    static Set<String> blacklist = Sets.newHashSet(
        ObjectMapperConfig.class.getName(),
        QwacCertificateFilter.class.getName()
    );

    @Override
    public boolean match(MetadataReader metadataReader,
        MetadataReaderFactory metadataReaderFactory) {

      String className = metadataReader.getClassMetadata().getClassName();
      if (blacklist.contains(className)) {
        log.debug("Ignore blacklisted XS2A component: {}", className);
        return true;
      }

      return false;
    }
  }

  /*
   * TODO we have to override de.adorsys.psd2.xs2a.config.ObjectMapperConfig because
   *  a) we can't exclude it (see Xs2aComponentFilter) and
   *  b) the XS2A ObjectMapperConfig does not mark the OM as @Primary which clashes with
   *    org.springframework.hateoas.config.HypermediaSupportBeanDefinitionRegistrar
   *    but somehow only in tests
   */
  @Configuration
  static class SandboxObjectMapperConfig extends ObjectMapperConfig {

    private ObjectMapperConfig original;

    public SandboxObjectMapperConfig(ObjectMapperConfig original) {
      this.original = original;
    }

    @Bean
    @Primary
    ObjectMapper sandboxObjectMapper() {
      return super.objectMapper();
    }
  }

  @Slf4j
  @Component
  public static class HardcodedInstanceIdSetter implements PreInsertEventListener {

    private static final String PROPERTY_NAME = "instanceId";
    private static final String SANDBOX_INSTANCE = "sandbox";

    HardcodedInstanceIdSetter(EntityManagerFactory emf) {
      SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
      EventListenerRegistry registry = sessionFactory.getServiceRegistry()
          .getService(EventListenerRegistry.class);
      registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(this);
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
      Object object = event.getEntity();

      if (object instanceof InstanceDependableEntity) {
        InstanceDependableEntity entity = (InstanceDependableEntity) object;
        String[] propertyNames = event.getPersister()
            .getEntityMetamodel()
            .getPropertyNames();

        int instanceProperty = ArrayUtils.indexOf(propertyNames, PROPERTY_NAME);
        if (instanceProperty >= 0) {
          event.getState()[instanceProperty] = SANDBOX_INSTANCE;
        } else {
          String errorMessage = String.format(
              "Field '%s' not found on entity '%s'.",
              PROPERTY_NAME,
              entity.getClass().getName()
          );
          // InstanceDependableEntity provides this property so it must exist
          throw new IllegalStateException(errorMessage);
        }
      }
      return false;
    }
  }


}
